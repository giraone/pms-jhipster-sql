package com.giraone.pms.service.impl;

import java.util.List;
import java.util.Optional;

import com.giraone.pms.service.NameNormalizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.giraone.pms.domain.Company;
import com.giraone.pms.repository.CompanyRepository;
import com.giraone.pms.repository.EmployeeRepository;
import com.giraone.pms.service.EmployeeDomainService;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import com.giraone.pms.service.mapper.CompanyMapper;
import com.giraone.pms.service.mapper.EmployeeMapper;

/**
 * Service Implementation for managing Employee (domain version).
 */
@Service
@Transactional
public class EmployeeDomainServiceImpl implements EmployeeDomainService {

    private static final boolean USE_MAIN_TABLE_ONLY = false;

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
     * @param surnamePrefix     restrict the query to employees with a surname matching this prefix
     * @param pageable          the pagination information
     * @return the list of entities or an empty optional, if the company was invalid
     */
    public Optional<Page<EmployeeDTO>> findAll(String companyExternalId, String surnamePrefix, Pageable pageable) {

        log.debug("Service request to query employees companyExternalId={}, surnamePrefix={}", companyExternalId, surnamePrefix);
        Optional<Company> company = companyRepository.findOneByExternalId(companyExternalId);
        if (!company.isPresent()) {
            return Optional.empty();
        }
        if (USE_MAIN_TABLE_ONLY) {
            return Optional.of(
                employeeRepository.findAllByCompanyAndSurname(company.get(), surnamePrefix + "%", pageable)
                    .map(employeeMapper::toDto));
        } else {
            final String normalizedName = surnamePrefix.toLowerCase();
            return Optional.of(
                employeeRepository.findAllByCompanyAndNormalizedName(company.get(), normalizedName + "%", pageable)
                    .map(employeeMapper::toDto));
        }
    }

    /**
     * Query the employees of a company.
     *
     * @param surnamePrefix restrict the query to employees with a surname matching this prefix
     * @param pageable      the pagination information
     * @return the list of entities or an empty optional, if the company was invalid
     */
    public Optional<Page<EmployeeDTO>> findAll(String surnamePrefix, Pageable pageable) {

        log.debug("Service request to query employees surnamePrefix={}", surnamePrefix);
        if (USE_MAIN_TABLE_ONLY || surnamePrefix.trim().matches("[ \\-]")) {
            return Optional.of(
                employeeRepository.findAllBySurname(surnamePrefix + "%", pageable)
                    .map(employeeMapper::toDto));
        } else {
            final String normalizedName = nameNormalizeService.normalizeSingleName(surnamePrefix);
            return Optional.of(
                employeeRepository.findAllByNormalizedName(normalizedName, pageable)
                    .map(employeeMapper::toDto));
        }
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
