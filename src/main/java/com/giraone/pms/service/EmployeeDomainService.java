package com.giraone.pms.service;

import com.giraone.pms.service.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Employee (domain version).
 */
public interface EmployeeDomainService {

    /**
     * Query the employees of a company.
     *
     * @param companyId
     * @param surnamePrefix
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<EmployeeDTO> findAll(String companyId, String surnamePrefix, Pageable pageable);


    /**
     * Get the "id" employee.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<EmployeeDTO> findOne(Long id);
}
