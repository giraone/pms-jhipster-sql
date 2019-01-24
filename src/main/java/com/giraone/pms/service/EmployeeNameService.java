package com.giraone.pms.service;

import com.giraone.pms.domain.EmployeeName;

import java.util.List;

/**
 * Service Interface for managing EmployeeName.
 */
public interface EmployeeNameService {

    /**
     * Get all the employeeNames.
     *
     * @return the list of entities
     */
    List<EmployeeName> findAll();
}
