package com.giraone.pms.service;

import com.giraone.pms.domain.filter.PersonFilter;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Employee.
 */
public interface EmployeeService {

    /**
     * Save a employee.
     *
     * @param employeeDTO the entity to save
     * @return the persisted entity
     */
    EmployeeDTO save(EmployeeDTO employeeDTO);

    /**
     * Get the "id" employee.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<EmployeeDTO> findOne(long id);

    /**
     * Delete the "id" employee.
     *
     * @param id the id of the entity
     */
    void delete(long id);

    //-- Extensions ----------------------------------------------------------------------------------------------------

    /**
     * Query the employees of a company.
     *
     * @param companyExternalId restrict the query to employees of this company, if null no restrictions are applied
     * @param personFilter restrict the query to employees matching this filter
     * @param pageable the pagination information
     * @return the list of entities or an empty optional, if the company was invalid
     */
    Optional<Page<EmployeeDTO>> findAllByFilter(String companyExternalId, PersonFilter personFilter, Pageable pageable);

    /**
     * Get a list of companies to which a user has access
     * @param userLogin login of the user
     * @return the list of companies
     */
    List<CompanyDTO> getAllCompaniesOfEmployee(String userLogin);
}
