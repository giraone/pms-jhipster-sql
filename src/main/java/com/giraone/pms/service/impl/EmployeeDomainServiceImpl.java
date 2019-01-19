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
     * @param companyId
     * @param surnamePrefix
     * @param pageable the pagination information
     * @return the list of entities or null, if the companyId was not found
     */
    public Page<EmployeeDTO> findAll(String companyId, String surnamePrefix, Pageable pageable) {
        log.debug("Service request to query employees companyId={}, surnamePrefix={}", companyId, surnamePrefix);
        Optional<Company> company = companyRepository.findOneByName(companyId);
        if (!company.isPresent()) {
            return null;
        }
        return employeeRepository.findAllByCompanyAndSurname(company.get(),surnamePrefix + "%", pageable)
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
}
