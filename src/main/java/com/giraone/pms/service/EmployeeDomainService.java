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
     * @param companyExternalId restrict the query to employees of this company
     * @param surnamePrefix restrict the query to employees with a surname matching this prefix
     * @param pageable the pagination information
     * @return the list of entities or an empty optional, if the company was invalid
     */
    Optional<Page<EmployeeDTO>> findAll(String companyExternalId, String surnamePrefix, Pageable pageable);

    /**
     * Query the employees of a company.
     *
     * @param surnamePrefix restrict the query to employees with a surname matching this prefix
     * @param pageable the pagination information
     * @return the list of entities or an empty optional, if the company was invalid
     */
    Optional<Page<EmployeeDTO>> findAll(String surnamePrefix, Pageable pageable);

    /**
     * Get the "id" employee.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<EmployeeDTO> findOne(Long id);
}
