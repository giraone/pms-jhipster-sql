package com.giraone.pms.service.impl;

import com.giraone.pms.domain.*;
import com.giraone.pms.domain.enumeration.EmployeeNameFilterKey;
import com.giraone.pms.domain.enumeration.StringSearchMode;
import com.giraone.pms.domain.filter.EmployeeFilter;
import com.giraone.pms.domain.filter.EmployeeFilterPair;
import com.giraone.pms.repository.CompanyRepository;
import com.giraone.pms.repository.EmployeeNameRepository;
import com.giraone.pms.repository.EmployeeRepository;
import com.giraone.pms.service.EmployeeDomainService;
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

import java.util.*;

/**
 * Service Implementation for managing Employee (domain version).
 */
@Service
@SuppressWarnings("unused")
public class EmployeeDomainServiceImpl implements EmployeeDomainService {

    private final Logger log = LoggerFactory.getLogger(EmployeeDomainServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final EmployeeNameRepository employeeNameRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeMapper employeeMapper;
    private final CompanyMapper companyMapper;
    private final NameNormalizeService nameNormalizeService;

    public EmployeeDomainServiceImpl(
        EmployeeRepository employeeRepository,
        EmployeeNameRepository employeeNameRepository,
        CompanyRepository companyRepository,
        EmployeeMapper employeeMapper,
        CompanyMapper companyMapper,
        NameNormalizeService nameNormalizeService) {
        this.employeeRepository = employeeRepository;
        this.employeeNameRepository = employeeNameRepository;
        this.companyRepository = companyRepository;
        this.employeeMapper = employeeMapper;
        this.companyMapper = companyMapper;
        this.nameNormalizeService = nameNormalizeService;
    }

    /**
     * Query the employees of a company.
     *
     * @param companyExternalId restrict the query to employees of this company
     * @param employeeFilter    restrict the query to employees matching this filter
     * @param pageable          the pagination information
     * @return the list of entities or an empty optional, if the company was invalid
     */
    @Timed
    @Transactional(readOnly = true)
    public Optional<Page<EmployeeDTO>> findAll(Optional<String> companyExternalId, EmployeeFilter employeeFilter, Pageable pageable) {

        log.debug("Service request to query employees companyExternalId={}, employeeFilter={}", companyExternalId, employeeFilter);

        Optional<Company> company;
        if (companyExternalId.isPresent()) {
            company = companyRepository.findOneByExternalId(companyExternalId.get());
            if (!company.isPresent()) {
                return Optional.empty();
            }
        } else {
            company = Optional.empty();
        }

        Page<Employee> page;
        if (company.isPresent()) {
            // USER with access to only one company
            page = getEmployees(employeeFilter, pageable, company);
        } else {
            // ADMIN with full access to all companies
            page = getEmployees(employeeFilter, pageable);
        }

        return Optional.of(page.map(employeeMapper::toDto));
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
     * Get a list of companies to which a user has access
     * @param userLogin login of the user
     * @return the list of companies
     */
    @Transactional(readOnly = true)
    public List<CompanyDTO> getAllCompaniesOfEmployee(String userLogin) {

        log.debug("getAllCompaniesOfEmployee userLogin={}", userLogin);
        return this.companyRepository.findCompaniesOfUser(userLogin, Pageable.unpaged()).map(companyMapper::toDto).getContent();
    }

    /**
     * Save a employee.
     *
     * @param employeeDTO the entity to save
     * @return the persisted entity
     */
    @Transactional
    public EmployeeDTO save(EmployeeDTO employeeDTO) {
        log.debug("Request to save Employee : {}", employeeDTO);

        Employee employee = employeeMapper.toEntity(employeeDTO);
        employee = employeeRepository.save(employee);

        // the redundant names for optimized querying (normalized, phonetic)
        List<EmployeeName> employeeNames = this.buildNames(employee);
        employeeNameRepository.saveAll(employeeNames);

        return employeeMapper.toDto(employee);
    }


    /**
     * Build the list of redundant EmployeeNames for an Employee entity
     * @param employee the employee entity
     * @return list of EmployeeName entities
     */
    public List<EmployeeName> buildNames(Employee employee) {

        final List<EmployeeName> names = new ArrayList<>();
        final Set<EmployeeFilterPair> namesOfEmployee = this.buildName(employee);
        for (EmployeeFilterPair name : namesOfEmployee) {
            final EmployeeName employeeName = new EmployeeName();
            // This is a weird solution, because JPA does not handle tables without one primary key very well
            final EmployeeNameCompoundKey employeeNameCompoundKey = new EmployeeNameCompoundKey();
            employeeNameCompoundKey.setOwnerId(employee.getId());
            employeeNameCompoundKey.setNameKey(name.getKey());
            employeeNameCompoundKey.setNameValue(name.getValue());
            employeeName.setId(employeeNameCompoundKey);
            names.add(employeeName);
        }
        return names;
    }

    //------------------------------------------------------------------------------------------------------------------

    private Page<Employee> getEmployees(EmployeeFilter employeeFilter, Pageable pageable) {
        Page<Employee> page;
        if (employeeFilter.getSurname().isPresent()) {
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

    private Page<Employee> getEmployees(EmployeeFilter employeeFilter, Pageable pageable, Optional<Company> company) {
        Page<Employee> page;
        if (employeeFilter.getSurname().isPresent()) {
            final EmployeeFilterPair pair = employeeFilter.buildQueryValue(nameNormalizeService);
            if (pair.getKey() == null) {
                final String likeSuffix = employeeFilter.getSurnameSearchMode() == StringSearchMode.PREFIX ? "%" : "";
                page = employeeRepository.findAllByCompanyAndSurnameLike(
                    company.get(), pair.getValue() + likeSuffix, pageable);
            } else {
                page = employeeRepository.findAllByCompanyAndKeyPairLike(
                    company.get(), pair.getKey(), pair.getValue(), pageable);
            }
        } else {
            page = employeeRepository.findAllByCompany(company.get(), pageable);
        }
        return page;
    }

    private Set<EmployeeFilterPair> buildName(Employee employee) {
        final Set<EmployeeFilterPair> ret = new HashSet<>();
        final String originalName = employee.getSurname();
        final String normalizedName = nameNormalizeService.normalize(originalName);
        ret.add(new EmployeeFilterPair(EmployeeNameFilterKey.SL.toString(), normalizedName));
        final List<String> names = nameNormalizeService.split(normalizedName);
        for (String name : names) {
            ret.add(new EmployeeFilterPair(EmployeeNameFilterKey.SN.toString(), nameNormalizeService.reduceSimplePhonetic(name)));
            ret.add(new EmployeeFilterPair(EmployeeNameFilterKey.SP.toString(), nameNormalizeService.phonetic(name)));
        }
        return ret;
    }
}
