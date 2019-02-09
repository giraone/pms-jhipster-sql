package com.giraone.pms.service;

import com.giraone.pms.service.dto.EmployeeNameDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing EmployeeName.
 */
public interface EmployeeNameService {

    /**
     * Save a employeeName.
     *
     * @param employeeNameDTO the entity to save
     * @return the persisted entity
     */
    EmployeeNameDTO save(EmployeeNameDTO employeeNameDTO);

    /**
     * Get all the employeeNames.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<EmployeeNameDTO> findAll(Pageable pageable);


    /**
     * Get the "id" employeeName.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<EmployeeNameDTO> findOne(Long id);

    /**
     * Delete the "id" employeeName.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
