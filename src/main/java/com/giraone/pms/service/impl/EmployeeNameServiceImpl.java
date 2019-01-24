package com.giraone.pms.service.impl;

import com.giraone.pms.domain.EmployeeName;
import com.giraone.pms.repository.EmployeeNameRepository;
import com.giraone.pms.service.EmployeeNameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing EmployeeName.
 */
@Service
@Transactional
public class EmployeeNameServiceImpl implements EmployeeNameService {

    private final Logger log = LoggerFactory.getLogger(EmployeeNameServiceImpl.class);

    private final EmployeeNameRepository employeeNameRepository;

    public EmployeeNameServiceImpl(EmployeeNameRepository employeeNameRepository) {
        this.employeeNameRepository = employeeNameRepository;
    }

    /**
     * Save a employeeName.
     *
     * @param employeeName the entity to save
     * @return the persisted entity
     */
    @Override
    public EmployeeName save(EmployeeName employeeName) {
        log.debug("Request to save EmployeeName : {}", employeeName);

        return employeeNameRepository.save(employeeName);
    }

    /**
     * Get all the employeeNames.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeName> findAll(Pageable pageable) {
        log.debug("Request to get all EmployeeNames");
        return employeeNameRepository.findAll(pageable);
    }


    /**
     * Get one employeeName by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeName> findOne(Long id) {
        log.debug("Request to get EmployeeName : {}", id);
        return employeeNameRepository.findById(id);
    }


    /**
     * Delete the employeeName by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EmployeeName : {}", id);
        employeeNameRepository.deleteById(id);
     }
}
