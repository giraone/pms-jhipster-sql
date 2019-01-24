package com.giraone.pms.service;

import com.giraone.pms.domain.EmployeeName;
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
    EmployeeName save(EmployeeName employeeNameDTO);

    /**
     * Get all the employeeNames.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<EmployeeName> findAll(Pageable pageable);


    /**
     * Get the "id" employeeName.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<EmployeeName> findOne(Long id);

    /**
     * Delete the "id" employeeName.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
