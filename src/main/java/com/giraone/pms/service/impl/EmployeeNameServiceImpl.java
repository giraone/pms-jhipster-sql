package com.giraone.pms.service.impl;

import com.giraone.pms.service.EmployeeNameService;
import com.giraone.pms.domain.EmployeeName;
import com.giraone.pms.repository.EmployeeNameRepository;
import com.giraone.pms.service.dto.EmployeeNameDTO;
import com.giraone.pms.service.mapper.EmployeeNameMapper;
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

    private final EmployeeNameMapper employeeNameMapper;

    public EmployeeNameServiceImpl(EmployeeNameRepository employeeNameRepository, EmployeeNameMapper employeeNameMapper) {
        this.employeeNameRepository = employeeNameRepository;
        this.employeeNameMapper = employeeNameMapper;
    }

    /**
     * Save a employeeName.
     *
     * @param employeeNameDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public EmployeeNameDTO save(EmployeeNameDTO employeeNameDTO) {
        log.debug("Request to save EmployeeName : {}", employeeNameDTO);

        EmployeeName employeeName = employeeNameMapper.toEntity(employeeNameDTO);
        employeeName = employeeNameRepository.save(employeeName);
        return employeeNameMapper.toDto(employeeName);
    }

    /**
     * Get all the employeeNames.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeNameDTO> findAll(Pageable pageable) {
        log.debug("Request to get all EmployeeNames");
        return employeeNameRepository.findAll(pageable)
            .map(employeeNameMapper::toDto);
    }


    /**
     * Get one employeeName by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeNameDTO> findOne(Long id) {
        log.debug("Request to get EmployeeName : {}", id);
        return employeeNameRepository.findById(id)
            .map(employeeNameMapper::toDto);
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
