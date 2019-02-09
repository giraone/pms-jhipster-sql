package com.giraone.pms.service.impl;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import com.giraone.pms.domain.enumeration.StringSearchMode;
import com.giraone.pms.domain.filter.EmployeeFilter;
import com.giraone.pms.domain.filter.EmployeeFilterPair;
import com.giraone.pms.repository.CompanyRepository;
import com.giraone.pms.repository.EmployeeRepository;
import com.giraone.pms.service.EmployeeService;
import com.giraone.pms.service.NameNormalizeService;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import com.giraone.pms.service.mapper.CompanyMapper;
import com.giraone.pms.service.mapper.EmployeeMapper;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final NameNormalizeService nameNormalizeService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper,
                               CompanyRepository companyRepository, CompanyMapper companyMapper,
                               NameNormalizeService nameNormalizeService) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.nameNormalizeService = nameNormalizeService;
    }

    /**
     * Save an employee.
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
     * @param employeeFilter restrict the query to employees matching this filter
     * @param pageable the pagination information
     * @return the list of entities or an empty optional, if the company was invalid
     */
    @Timed
    @Transactional(readOnly = true)
    public Optional<Page<EmployeeDTO>> findAllByFilter(String companyExternalId, EmployeeFilter employeeFilter, Pageable pageable) {

        log.debug("Service request to query employees companyExternalId={}, employeeFilter={}", companyExternalId, employeeFilter);

        Optional<Company> company;
        if (companyExternalId != null) {
            company = companyRepository.findOneByExternalId(companyExternalId);
            if (!company.isPresent()) {
                return Optional.empty();
            }
        } else {
            company = Optional.empty();
        }

        Page<Employee> page;
        if (company.isPresent()) {
            // USER with access to only one company
            page = getEmployees(employeeFilter, pageable, company.get());
        } else {
            // ADMIN with full access to all companies
            page = getEmployees(employeeFilter, pageable);
        }

        return Optional.of(page.map(employeeMapper::toDto));
    }


    /**
     * Get a list of companies to which a user has access
     * @param userLogin login of the user
     * @return the list of companies
     */
    @Transactional(readOnly = true)
    public List<CompanyDTO> getAllCompaniesOfEmployee(String userLogin) {

        log.debug("getAllCompaniesOfEmployee userLogin={}", userLogin);
        return this.companyRepository.findCompaniesOfUser(userLogin, Pageable.unpaged()).map(companyMapper::toDto).getContent();
    }

    //------------------------------------------------------------------------------------------------------------------

    private Page<Employee> getEmployees(EmployeeFilter employeeFilter, Pageable pageable) {
        Page<Employee> page;
        if (employeeFilter.getSurname() != null) {
            final EmployeeFilterPair pair = employeeFilter.buildQueryValue(nameNormalizeService);
            if (pair.getKey() == null) {
                final String likeSuffix = employeeFilter.getSurnameSearchMode() == StringSearchMode.PREFIX ? "%" : "";
                page = employeeRepository.findAllBySurnameLike(
                    pair.getValue() + likeSuffix, pageable);
            } else {
                page = employeeRepository.findAllByKeyPairLike(
                    pair.getKey(), pair.getValue(), pageable);
            }
        } else {
            page = employeeRepository.findAll(pageable);
        }
        return page;
    }

    private Page<Employee> getEmployees(EmployeeFilter employeeFilter, Pageable pageable, Company company) {
        Page<Employee> page;
        if (employeeFilter.getSurname() != null) {
            final EmployeeFilterPair pair = employeeFilter.buildQueryValue(nameNormalizeService);
            if (pair.getKey() == null) {
                final String likeSuffix = employeeFilter.getSurnameSearchMode() == StringSearchMode.PREFIX ? "%" : "";
                page = employeeRepository.findAllByCompanyAndSurnameLike(
                    company, pair.getValue() + likeSuffix, pageable);
            } else {
                page = employeeRepository.findAllByCompanyAndKeyPairLike(
                    company, pair.getKey(), pair.getValue(), pageable);
            }
        } else {
            page = employeeRepository.findAllByCompany(company, pageable);
        }
        return page;
    }
}
