package com.giraone.pms.service.mapper;

import com.giraone.pms.domain.*;
import com.giraone.pms.service.dto.EmployeeDTO;
import com.giraone.pms.service.dto.EmployeeNameDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity EmployeeName and its DTO EmployeeNameDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EmployeeNameMapper extends EntityMapper<EmployeeNameDTO, EmployeeName> {


    @Mapping(source = "id.owner.id", target = "ownerId")
    @Mapping(source = "id.nameKey", target = "nameKey")
    @Mapping(source = "id.nameValue", target = "nameValue")
    EmployeeNameDTO toDto(EmployeeName employeeName);

    @Mapping(source = "ownerId", target = "id.owner.id")
    @Mapping(source = "nameKey", target = "id.nameKey")
    @Mapping(source = "nameValue", target = "id.nameValue")
    EmployeeName toEntity(EmployeeNameDTO employeeDTO);
}
