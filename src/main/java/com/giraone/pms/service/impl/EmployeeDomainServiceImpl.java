package com.giraone.pms.service.impl;

import com.giraone.pms.domain.Company;
import com.giraone.pms.repository.CompanyRepository;
import com.giraone.pms.repository.EmployeeRepository;
import com.giraone.pms.service.EmployeeDomainService;
import com.giraone.pms.service.dto.EmployeeDTO;
import com.giraone.pms.service.mapper.EmployeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public EmployeeDomainServiceImpl(
        EmployeeRepository employeeRepository,
        CompanyRepository companyRepository,
        EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.companyRepository = companyRepository;
        this.employeeMapper = employeeMapper;
    }


    /**
     * Query the employees of a company.
     *
     * @param companyExternalId restrict the query to employees of this company
     * @param surnamePrefix restrict the query to employees with a surname matching this prefix
     * @param pageable the pagination information
     * @return the list of entities or an empty optional, if the company was invalid
     */
    public Optional<Page<EmployeeDTO>> findAll(String companyExternalId, String surnamePrefix, Pageable pageable) {
        log.debug("Service request to query employees companyExternalId={}, surnamePrefix={}", companyExternalId, surnamePrefix);
        Optional<Company> company = companyRepository.findOneByExternalId(companyExternalId);
        if (!company.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(
            employeeRepository.findAllByCompanyAndSurname(company.get(),surnamePrefix + "%", pageable)
                .map(employeeMapper::toDto));
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
}
