package com.giraone.pms.service.mapper;

import com.giraone.pms.domain.*;
import com.giraone.pms.service.dto.EmployeeNameDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity EmployeeName and its DTO EmployeeNameDTO.
 */
@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface EmployeeNameMapper extends EntityMapper<EmployeeNameDTO, EmployeeName> {

    @Mapping(source = "owner.id", target = "ownerId")
    EmployeeNameDTO toDto(EmployeeName employeeName);

    @Mapping(source = "ownerId", target = "owner")
    EmployeeName toEntity(EmployeeNameDTO employeeNameDTO);

    default EmployeeName fromId(Long id) {
        if (id == null) {
            return null;
        }
        EmployeeName employeeName = new EmployeeName();
        employeeName.setId(id);
        return employeeName;
    }
}
