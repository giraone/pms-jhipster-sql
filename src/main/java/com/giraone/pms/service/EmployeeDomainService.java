package com.giraone.pms.service;

import com.giraone.pms.domain.filter.EmployeeFilter;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Employee (domain version).
 */
public interface EmployeeDomainService {

    /**
     * Query the employees of a company.
     *
     * @param companyExternalId restrict the query to employees of this company
     * @param employeeFilter restrict the query to employees matching this filter
     * @param pageable the pagination information
     * @return the list of entities or an empty optional, if the company was invalid
     */
    Optional<Page<EmployeeDTO>> findAll(Optional<String> companyExternalId, EmployeeFilter employeeFilter, Pageable pageable);

   /**
     * Get the "id" employee.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<EmployeeDTO> findOne(Long id);

    /**
     * Get a list of companies to which a user has access
     * @param userLogin login of the user
     * @return the list of companies
     */
    List<CompanyDTO> getAllCompaniesOfEmployee(String userLogin);

    /**
     * Save a employee.
     *
     * @param employeeDTO the entity to save
     * @return the persisted entity
     */
    EmployeeDTO save(EmployeeDTO employeeDTO);
}
