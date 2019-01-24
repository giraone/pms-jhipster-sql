package com.giraone.pms.service.impl;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import com.giraone.pms.domain.filter.EmployeeFilter;
import com.giraone.pms.domain.filter.EmployeeFilterPair;
import com.giraone.pms.repository.CompanyRepository;
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

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing Employee (domain version).
 */
@Service
@Transactional
public class EmployeeDomainServiceImpl implements EmployeeDomainService {

    private final Logger log = LoggerFactory.getLogger(EmployeeDomainServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeMapper employeeMapper;
    private final CompanyMapper companyMapper;
    private final NameNormalizeService nameNormalizeService;

    public EmployeeDomainServiceImpl(
        EmployeeRepository employeeRepository,
        CompanyRepository companyRepository,
        EmployeeMapper employeeMapper,
        CompanyMapper companyMapper,
        NameNormalizeService nameNormalizeService) {
        this.employeeRepository = employeeRepository;
        this.companyRepository = companyRepository;
        this.employeeMapper = employeeMapper;
        this.companyMapper = companyMapper;
        this.nameNormalizeService = nameNormalizeService;
    }

    /**
     * Query the employees of a company.
     *
     * @param companyExternalId restrict the query to employees of this company
     * @param employeeFilter restrict the query to employees matching this filter
     * @param pageable the pagination information
     * @return the list of entities or an empty optional, if the company was invalid
     */
    @Timed
    public Optional<Page<EmployeeDTO>> findAll(Optional<String> companyExternalId, EmployeeFilter employeeFilter, Pageable pageable) {

        log.debug("Service request to query employees companyExternalId={}, employeeFilter={}", companyExternalId, employeeFilter);

        Optional<Company> company;
        if (!companyExternalId.isPresent()) {
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
            if (employeeFilter.getSurname().isPresent()) {
                final EmployeeFilterPair pair = employeeFilter.buildQueryValue(nameNormalizeService);
                page = employeeRepository.findAllByCompanyAndKeyPairLike(
                        company.get(), pair.getKey(), pair.getValue(), pageable);
            } else {
                page = employeeRepository.findAllByCompany(company.get(), pageable);
            }
        } else {
            // ADMIN with full access to all companies
            if (employeeFilter.getSurname().isPresent()) {
                final EmployeeFilterPair pair = employeeFilter.buildQueryValue(nameNormalizeService);
                page = employeeRepository.findAllByKeyPairLike(
                    pair.getKey(), pair.getValue(), pageable);
            } else {
                page = employeeRepository.findAll(pageable);
            }
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

    public List<CompanyDTO> getAllCompaniesOfEmployee(String userLogin) {

        log.debug("getAllCompaniesOfEmployee userLogin={}", userLogin);
        return this.companyRepository.findCompaniesOfUser(userLogin, Pageable.unpaged()).map(companyMapper::toDto).getContent();
    }
}
