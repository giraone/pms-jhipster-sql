package com.giraone.pms.service.impl;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import com.giraone.pms.domain.enumeration.EmployeeNameFilterKey;
import com.giraone.pms.domain.filter.EmployeeFilterPair;
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

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper,
                               CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
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
     * @param personFilter restrict the query to employees matching this filter
     * @param pageable the pagination information
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

        Page<Employee> page;
        if (company.isPresent()) {
            // USER with access to only one company
            page = getEmployees(personFilter, pageable, company.get());
        } else {
            // ADMIN with full access to all companies
            page = getEmployees(personFilter, pageable);
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

    private Page<Employee> getEmployees(PersonFilter personFilter, Pageable pageable) {
        Page<Employee> page;
        if (personFilter.getDateOfBirth() != null) {
            if (personFilter.hasNames()) {
                final EmployeeFilterPair pair = personFilter.buildQueryValueSingleName();
                if (personFilter.hasExactNames()) {
                    if (personFilter.getExactNames().size() > 1) {
                        page = employeeRepository.findAllBySurnameAndGivenNameAndDateOfBirth(
                            personFilter.getExactNames().get(0), personFilter.getExactNames().get(1), personFilter.getDateOfBirth(), pageable);
                    } else {
                        page = employeeRepository.findAllBySurnameAndDateOfBirth(
                            personFilter.getExactNames().get(0), personFilter.getDateOfBirth(), pageable);
                    }
                } else {
                    page = employeeRepository.findAllByKeyPairLike(
                        personFilter.isPhonetic() ? EmployeeNameFilterKey.SP.toString() : EmployeeNameFilterKey.SN.toString(),
                        personFilter.getWeakMatchingNames().get(0), pageable);
                }
            } else {
                page = employeeRepository.findAll(pageable);
            }
        } else {
            if (personFilter.hasNames()) {
                final EmployeeFilterPair pair = personFilter.buildQueryValueSingleName();
                if (personFilter.hasExactNames()) {
                    if (personFilter.getExactNames().size() > 1) {
                        page = employeeRepository.findAllBySurnameAndGivenName(
                            personFilter.getExactNames().get(0), personFilter.getExactNames().get(1), pageable);
                    } else {
                        page = employeeRepository.findAllBySurname(personFilter.getExactNames().get(0), pageable);
                    }
                } else {
                    page = employeeRepository.findAllByKeyPairLike(
                        personFilter.isPhonetic() ? EmployeeNameFilterKey.SP.toString() : EmployeeNameFilterKey.SN.toString(),
                        personFilter.getWeakMatchingNames().get(0), pageable);
                }
            } else {
                page = employeeRepository.findAll(pageable);
            }
        }
        return page;
    }

    private Page<Employee> getEmployees(PersonFilter personFilter, Pageable pageable, Company company) {
        Page<Employee> page;
        if (personFilter.getDateOfBirth() != null) {
            if (personFilter.hasNames()) {
                final EmployeeFilterPair pair = personFilter.buildQueryValueSingleName();
                if (personFilter.hasExactNames()) {
                    if (personFilter.getExactNames().size() > 1) {
                        log.debug(String.format("findAllByCompanyAndSurnameAndGivenNameAndDateOfBirth %s %s %s %s",
                            company.getExternalId(), personFilter.getExactNames().get(0), personFilter.getExactNames().get(1), personFilter.getDateOfBirth()));
                        page = employeeRepository.findAllByCompanyAndSurnameAndGivenNameAndDateOfBirth(
                            company, personFilter.getExactNames().get(0), personFilter.getExactNames().get(1), personFilter.getDateOfBirth(), pageable);
                    } else {
                        log.debug(String.format("findAllByCompanyAndSurnameAndDateOfBirth %s %s %s",
                            company.getExternalId(), personFilter.getExactNames().get(0), personFilter.getDateOfBirth()));
                        page = employeeRepository.findAllByCompanyAndSurnameAndDateOfBirth(company, personFilter.getExactNames().get(0), personFilter.getDateOfBirth(), pageable);
                    }
                } else {
                    log.debug(String.format("findAllByCompanyAndKeyPairLike %s %s %s",
                        company.getExternalId(), personFilter.getWeakMatchingNames().get(0), personFilter.getDateOfBirth()));
                    page = employeeRepository.findAllByCompanyAndDateOfBirthAndKeyPairLike(
                        company, personFilter.getDateOfBirth(),
                        personFilter.isPhonetic() ? EmployeeNameFilterKey.SP.toString() : EmployeeNameFilterKey.SN.toString(),
                        personFilter.getWeakMatchingNames().get(0), pageable);
                }
            } else {
                log.debug(String.format("findAllByCompanyAndDateOfBirth %s %s", company.getExternalId(), personFilter.getDateOfBirth()));
                page = employeeRepository.findAllByCompanyAndDateOfBirth(company, personFilter.getDateOfBirth(), pageable);
            }
        } else {
            if (personFilter.hasNames()) {
                final EmployeeFilterPair pair = personFilter.buildQueryValueSingleName();
                if (personFilter.hasExactNames()) {
                    if (personFilter.getExactNames().size() > 1) {
                        log.debug(String.format("findAllByCompanyAndSurnameAndGivenName %s %s %s",
                            company.getExternalId(), personFilter.getExactNames().get(0), personFilter.getExactNames().get(1)));
                        page = employeeRepository.findAllByCompanyAndSurnameAndGivenName(
                            company, personFilter.getExactNames().get(0), personFilter.getExactNames().get(1), pageable);
                    } else {
                        log.debug(String.format("findAllByCompanyAndSurname %s %s",
                            company.getExternalId(), personFilter.getExactNames().get(0)));
                        page = employeeRepository.findAllByCompanyAndSurname(company, personFilter.getExactNames().get(0), pageable);
                    }
                } else {
                    log.debug(String.format("findAllByCompanyAndKeyPairLike %s %s",
                        company.getExternalId(), personFilter.getWeakMatchingNames().get(0)));
                    page = employeeRepository.findAllByCompanyAndKeyPairLike(
                        company, personFilter.isPhonetic() ? EmployeeNameFilterKey.SP.toString() : EmployeeNameFilterKey.SN.toString(),
                        personFilter.getWeakMatchingNames().get(0), pageable);
                }
            } else {
                log.debug(String.format("findAllByCompany %s", company.getExternalId()));
                page = employeeRepository.findAllByCompany(company, pageable);
            }
        }
        return page;
    }
}
