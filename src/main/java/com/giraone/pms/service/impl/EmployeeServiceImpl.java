package com.giraone.pms.service.impl;

import com.giraone.pms.domain.*;
import com.giraone.pms.domain.filter.PersonFilter;
import com.giraone.pms.repository.CompanyRepository;
import com.giraone.pms.repository.EmployeeRepository;
import com.giraone.pms.service.AuthorizationService;
import com.giraone.pms.service.EmployeeService;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import com.giraone.pms.service.mapper.CompanyMapper;
import com.giraone.pms.service.mapper.EmployeeMapper;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing Employee.
 */
@Service
@Transactional
@SuppressWarnings("unused")
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    // Extensions
    private final EntityManager em;
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final AuthorizationService authorizationService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper,
                               EntityManager em, CompanyRepository companyRepository,
                               CompanyMapper companyMapper, AuthorizationService authorizationService) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.em = em;
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.authorizationService = authorizationService;
    }

    /**
     * Save an employee.
     *
     * @param employeeDTO the entity to save
     * @return the persisted entity
     */
    @Transactional
    public EmployeeDTO save(EmployeeDTO employeeDTO) {

        final boolean isAdmin = authorizationService.isAdmin();
        log.debug("Request to save Employee admin={} employee={}", isAdmin, employeeDTO);
        Employee employee = employeeMapper.toEntity(employeeDTO);
        employee.normalizeAndTrim(); // remove unnecessary stuff like white spaces

        if (employee.getCompany() == null || employee.getCompany().getId() == null) {
            throw new IllegalArgumentException("companyId of employee may not be null!");
        }

        // Fetch the company fresh from the database, because the DTO contains only the ID
        employee.setCompany(companyRepository.getOne(employee.getCompany().getId()));

        if (!isAdmin && !authorizationService.check(employee.getCompany())) {
            throw new AccessDeniedException(
                String.format("SECURITY-WARNING: User with id=%s tries to store employee %s into company id=%s without access rights!",
                    authorizationService.getCurrentUserLogin(), employee, employee.getCompany().getExternalId()));
        }
        employee = employeeRepository.save(employee);
        return employeeMapper.toDto(employee);
    }

    /**
     * Get one employee by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeDTO> findOne(long id) {

        final boolean isAdmin = authorizationService.isAdmin();
        log.debug("Request to get employee admin={} id={}", isAdmin, id);

        Optional<Employee> employee = employeeRepository.findById(id);
        if (!employee.isPresent()) {
            log.warn("find employee id={} NOT FOUND!", id);
            return Optional.empty();
        }

        if (!isAdmin && !authorizationService.check(employee.get().getCompany())) {
            throw new AccessDeniedException(
                String.format("SECURITY-WARNING: User with id=%s tries to find employee %d of company id=%s without access rights!",
                    authorizationService.getCurrentUserLogin(), id, employee.get().getCompany().getExternalId()));
        }
        return employee.map(employeeMapper::toDto);
    }

    /**
     * Delete the employee by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(long id) {

        final boolean isAdmin = authorizationService.isAdmin();
        log.debug("Request to delete employee admin={} id={}", isAdmin, id);

        Optional<Employee> employee = employeeRepository.findById(id);
        if (!employee.isPresent()) {
            log.warn("delete employee id={} NOT FOUND!", id);
            return;
        }

        if (!isAdmin && !authorizationService.check(employee.get().getCompany())) {
            throw new AccessDeniedException(
                String.format("SECURITY-WARNING: User with id=%d tries to delete employee %d of company id=%s without access rights!",
                    authorizationService.getCurrentUserId(), id, employee.get().getCompany().getExternalId()));
        }
        employeeRepository.deleteById(id);
    }

    //-- Extensions ----------------------------------------------------------------------------------------------------

    /**
     * Query the employees of a company.
     *
     * @param companyExternalId restrict the query to employees of this company, if null an empty page is returned
     * @param personFilter      restrict the query to employees matching this filter
     * @param pageable          the pagination information
     * @return the list of entities or an empty optional, if the company was invalid
     */
    @Timed
    @Transactional(readOnly = true)
    public Optional<Page<EmployeeDTO>> findAllByFilter(String companyExternalId, PersonFilter personFilter, Pageable pageable) {

        log.debug("Service request to query employees companyExternalId={}, personFilter={}, pageable={}",
            companyExternalId, personFilter, pageable);

        Optional<Company> company;
        if (companyExternalId != null) {
            company = companyRepository.findOneByExternalId(companyExternalId);
            if (!company.isPresent()) {
                log.warn("findAllByFilter: Company companyExternalId={} NOT FOUND!", companyExternalId);
                return Optional.empty();
            }
        } else {
            log.warn("findAllByFilter: Company companyExternalId was null!");
            return Optional.empty();
        }

        final Page<Employee> page = getEmployees(personFilter, pageable, company.get().getId());

        return Optional.of(page.map(e -> { e.setCompany(company.get()); return e; }).map(employeeMapper::toDto));
    }


    /**
     * Get a list of companies to which a user has access
     *
     * @param userLogin login of the user
     * @return the list of companies
     */
    @Transactional(readOnly = true)
    public List<CompanyDTO> getAllCompaniesOfEmployee(String userLogin) {

        log.debug("getAllCompaniesOfEmployee userLogin={}", userLogin);
        return this.companyRepository.findCompaniesOfUserByLogin(userLogin, Pageable.unpaged()).map(companyMapper::toDto).getContent();
    }

    //------------------------------------------------------------------------------------------------------------------

    /*
    static Specification<Employee> ofCompany(Company company) {
        return (employee, cq, cb) -> cb.equal(employee.get(Employee_.company), company);
    }

    static Specification<Employee> hasDateOfBirth(LocalDate dateOfBirth) {
        return (employee, cq, cb) -> cb.equal(employee.get(Employee_.dateOfBirth), dateOfBirth);
    }
    */

    private Page<Employee> getEmployees(PersonFilter personFilter, Pageable pageable, long companyId) {

        log.debug("getEmployees companyId={}, personFilter={}", companyId, personFilter);

        final CriteriaBuilder cb = em.getCriteriaBuilder();

        final CriteriaQuery<Employee> fetchQuery = cb.createQuery(Employee.class);
        final Root<Employee> employeeFetchTable = fetchQuery.from(Employee.class);
        //employeeFetchTable.fetch(Employee_.company); // fetch also the company information without need for additional queries

        final CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        final Root<Employee> employeeCountTable = countQuery.from(Employee.class);

        final Root<EmployeeName> employeeNameTableFetch = fetchQuery.from(EmployeeName.class);
        final Root<EmployeeName> employeeNameTableCount = countQuery.from(EmployeeName.class);

        final List<Predicate> fetchPredicates = new ArrayList<>();
        final List<Predicate> countPredicates = new ArrayList<>();

        fetchPredicates.add(cb.equal(employeeNameTableFetch.get(EmployeeName_.company).get(Company_.id), companyId));
        countPredicates.add(cb.equal(employeeNameTableCount.get(EmployeeName_.company).get(Company_.id), companyId));

        if (personFilter != null) {
            if (personFilter.getDateOfBirth() != null) {
                fetchPredicates.add(cb.equal(employeeFetchTable.get(Employee_.dateOfBirth), personFilter.getDateOfBirth()));
                countPredicates.add(cb.equal(employeeCountTable.get(Employee_.dateOfBirth), personFilter.getDateOfBirth()));
            }

            if (personFilter.hasNames()) {
                //final AtomicInteger en = new AtomicInteger();
                personFilter.getNames().forEach(nameFilter -> {

                    //employeeNameTable.alias("en" + en.getAndIncrement());
                    fetchPredicates.add(
                        cb.and(
                            cb.equal(employeeNameTableFetch.get(EmployeeName_.id).get(EmployeeNameCompoundKey_.owner), employeeFetchTable.get(Employee_.id)),
                            cb.equal(employeeNameTableFetch.get(EmployeeName_.id).get(EmployeeNameCompoundKey_.nameKey), nameFilter.getKey()),
                            nameFilter.getKey().startsWith("L")
                                ? cb.equal(employeeNameTableFetch.get(EmployeeName_.id).get(EmployeeNameCompoundKey_.nameValue), nameFilter.getValue())
                                : cb.like(employeeNameTableFetch.get(EmployeeName_.id).get(EmployeeNameCompoundKey_.nameValue), nameFilter.getValue() + "%")
                        )
                    );
                    countPredicates.add(
                        cb.and(
                            cb.equal(employeeNameTableCount.get(EmployeeName_.id).get(EmployeeNameCompoundKey_.owner), employeeCountTable.get(Employee_.id)),
                            cb.equal(employeeNameTableCount.get(EmployeeName_.id).get(EmployeeNameCompoundKey_.nameKey), nameFilter.getKey()),
                            nameFilter.getKey().startsWith("L")
                                ? cb.equal(employeeNameTableCount.get(EmployeeName_.id).get(EmployeeNameCompoundKey_.nameValue), nameFilter.getValue())
                                : cb.like(employeeNameTableCount.get(EmployeeName_.id).get(EmployeeNameCompoundKey_.nameValue), nameFilter.getValue() + "%")
                        )
                    );
                });
            }
        }

        // SELECT COUNT
        countQuery.select(cb.countDistinct(employeeCountTable));
        countQuery.where(countPredicates.toArray(new Predicate[]{}));
        final TypedQuery<Long> typedCountQuery = em.createQuery(countQuery);
        final long count = typedCountQuery.getSingleResult();

        // SELECT employee
        fetchQuery.select(employeeFetchTable);
        fetchQuery.where(fetchPredicates.toArray(new Predicate[]{}));
        fetchQuery.distinct(true);
        defineOrder(employeeFetchTable, fetchQuery, cb, pageable);
        final TypedQuery<Employee> typedQuery = em.createQuery(fetchQuery);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        final List<Employee> pageList = typedQuery.getResultList();

        return new PageImpl<>(pageList, pageable, count);
    }

    private void defineOrder(Root<Employee> table, CriteriaQuery cq, CriteriaBuilder cb, Pageable pageable) {

        if (pageable.getSort().isUnsorted()) return;
        Sort.Order order = pageable.getSort().iterator().next();
        log.debug("defineOrder pageable={} => order={}", pageable, order);
        if (order.isAscending()) {
            cq.orderBy(cb.asc(table.get(order.getProperty())));
        } else {
            cq.orderBy(cb.desc(table.get(order.getProperty())));
        }
    }
}
