package com.giraone.pms.service.mapper;

import com.giraone.pms.domain.*;
import com.giraone.pms.service.dto.EmployeeNameDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity EmployeeName and its DTO EmployeeNameDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EmployeeNameMapper extends EntityMapper<EmployeeNameDTO, EmployeeName> {



    default EmployeeName fromId(Long id) {
        if (id == null) {
            return null;
        }
        EmployeeName employeeName = new EmployeeName();
        employeeName.setId(id);
        return employeeName;
    }
}
