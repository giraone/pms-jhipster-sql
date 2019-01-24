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
    public Optional<Page<EmployeeDTO>> findAll(Optional<String> companyExternalId, EmployeeFilter employeeFilter, Pageable pageable);

   /**
     * Get the "id" employee.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<EmployeeDTO> findOne(Long id);
    
    List<CompanyDTO> getAllCompaniesOfEmployee(String userLogin);
}
