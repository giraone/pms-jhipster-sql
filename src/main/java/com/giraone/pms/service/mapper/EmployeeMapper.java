package com.giraone.pms.service.mapper;

import com.giraone.pms.domain.*;
import com.giraone.pms.service.dto.EmployeeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Employee and its DTO EmployeeDTO.
 */
@Mapper(componentModel = "spring", uses = {CompanyMapper.class})
public interface EmployeeMapper extends EntityMapper<EmployeeDTO, Employee> {

    @Mapping(source = "company.id", target = "companyId")
    EmployeeDTO toDto(Employee employee);

    @Mapping(source = "companyId", target = "company")
    Employee toEntity(EmployeeDTO employeeDTO);

    default Employee fromId(Long id) {
        if (id == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }
}
