package com.giraone.pms.service;

import com.giraone.pms.service.dto.EmployeeBulkDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for bulk managing Employee.
 */
public interface EmployeeBulkService {

    /**
     * Save a list of employees in one transaction.
     *
     * @param employeeDTOList the entity list to save
     * @return the number of saved employees
     */
    int save(List<EmployeeBulkDTO> employeeDTOList);

    int reIndex(boolean clearFirst);
}
