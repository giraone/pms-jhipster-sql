package com.giraone.pms.service.impl;

import com.giraone.pms.domain.*;
import com.giraone.pms.domain.filter.PersonFilter;
import com.giraone.pms.repository.CompanyRepository;
import com.giraone.pms.repository.EmployeeRepository;
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
import java.util.concurrent.atomic.AtomicInteger;

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

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper,
                               EntityManager em, CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.em = em;
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    /**
     * Save an employee.
     *
     * @param employeeDTO the entity to save
     * @return the persisted entity
     */
    @Transactional
    public EmployeeDTO save(EmployeeDTO employeeDTO) {
        log.debug("Request to save Employee : {}", employeeDTO);
        Employee employee = employeeMapper.toEntity(employeeDTO);
        employee.normalizeAndTrim(); // remove unnecessary stuff like white spaces
        employee = employeeRepository.save(employee);
        return employeeMapper.toDto(employee);
    }

    /**
     * Get all the employees.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDTO> findAllByFilter(Pageable pageable) {
        log.debug("Request to get all Employees");
        return employeeRepository.findAll(pageable)
            .map(employeeMapper::toDto);
    }


    /**
     * Get one employee by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeDTO> findOne(Long id) {
        log.debug("Request to get Employee : {}", id);
        return employeeRepository.findById(id)
            .map(employeeMapper::toDto);
    }

    /**
     * Delete the employee by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Employee : {}", id);
        employeeRepository.deleteById(id);
    }

    //-- Extensions ----------------------------------------------------------------------------------------------------

    /**
     * Query the employees of a company.
     *
     * @param companyExternalId restrict the query to employees of this company, if null no restrictions are applied
     * @param personFilter      restrict the query to employees matching this filter
     * @param pageable          the pagination information
     * @return the list of entities or an empty optional, if the company was invalid
     */
    @Timed
    @Transactional(readOnly = true)
    public Optional<Page<EmployeeDTO>> findAllByFilter(String companyExternalId, PersonFilter personFilter, Pageable pageable) {

        log.debug("Service request to query employees companyExternalId={}, personFilter={}", companyExternalId, personFilter);

        Optional<Company> company;
        if (companyExternalId != null) {
            company = companyRepository.findOneByExternalId(companyExternalId);
            if (!company.isPresent()) {
                log.warn("Company companyExternalId={} NOT FOUND!", companyExternalId);
                return Optional.empty();
            }
        } else {
            company = Optional.empty();
        }

        Page<Employee> page = getEmployees(personFilter, pageable, company.orElse(null));

        return Optional.of(page.map(employeeMapper::toDto));
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
        return this.companyRepository.findCompaniesOfUser(userLogin, Pageable.unpaged()).map(companyMapper::toDto).getContent();
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

    private Page<Employee> getEmployees(PersonFilter personFilter, Pageable pageable, Company company) {

        log.debug("getEmployees company={}, personFilter={}", company == null ? "" : company.getExternalId(), personFilter);

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        final Root<Employee> employeeTable = cq.from(Employee.class);
        //employeeTable.alias("e");

        final List<Predicate> predicates = new ArrayList<>();
        if (company != null) {
            predicates.add(cb.equal(employeeTable.get(Employee_.company), company));
        }
        if (personFilter.getDateOfBirth() != null) {
            predicates.add(cb.equal(employeeTable.get(Employee_.dateOfBirth), personFilter.getDateOfBirth()));
        }

        if (personFilter.hasNames()) {
            //final AtomicInteger en = new AtomicInteger();
            personFilter.getNames().forEach(nameFilter -> {
                final Root<EmployeeName> employeeNameTable = cq.from(EmployeeName.class);
                //employeeNameTable.alias("en" + en.getAndIncrement());
                predicates.add(
                    cb.and(
                        cb.equal(employeeNameTable.get(EmployeeName_.id).get(EmployeeNameCompoundKey_.owner), employeeTable.get(Employee_.id)),
                        cb.equal(employeeNameTable.get(EmployeeName_.id).get(EmployeeNameCompoundKey_.nameKey), nameFilter.getKey()),
                        cb.like(employeeNameTable.get(EmployeeName_.id).get(EmployeeNameCompoundKey_.nameValue), nameFilter.getValue()))
                    // TODO: equal instead of like when SL, GL
                );
            });
        }

        final Predicate[] predicatesArray = predicates.toArray(new Predicate[]{});

        // SELECT COUNT
        /*
        final CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.where(predicatesArray);
        final Root<Employee> employeeCountTable = countQuery.from(Employee.class);
        employeeCountTable.alias("e");
        countQuery.select(cb.count(employeeCountTable));
        final long count = em.createQuery(countQuery).getSingleResult();
        */
        final long count = 4711;

        // SELECT employee
        cq.select(employeeTable);
        cq.where(predicatesArray);
        cq.distinct(true);
        defineOrder(employeeTable, cq, cb, pageable);
        final TypedQuery<Employee> typedQuery = em.createQuery(cq);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        final List<Employee> pageList = typedQuery.getResultList();


        return new PageImpl<>(pageList, pageable, count);
    }

    private void defineOrder(Root<Employee> table, CriteriaQuery cq, CriteriaBuilder cb, Pageable pageable) {
        if (pageable.getSort().isUnsorted()) return;
        Sort.Order order = pageable.getSort().iterator().next();
        if (order.isAscending()) {
            cb.asc(table.get(order.getProperty()));
        } else {
            cb.desc(table.get(order.getProperty()));
        }
    }
}
