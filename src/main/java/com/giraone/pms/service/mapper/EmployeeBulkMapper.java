package com.giraone.pms.service.mapper;

import com.giraone.pms.domain.Employee;
import com.giraone.pms.domain.enumeration.GenderType;
import com.giraone.pms.service.dto.EmployeeBulkDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Mapper for the entity Employee and its DTO EmployeeDTO.
 */
@Mapper(componentModel = "spring", uses = {CompanyMapper.class, EmployeeMappingHelper.class})
public interface EmployeeBulkMapper extends EntityMapper<EmployeeBulkDTO, Employee> {

    @Mappings({
        @Mapping(source = "dateOfBirth", target = "dateOfBirth", dateFormat = "yyyyMMdd"),
        @Mapping(source = "company.id", target = "companyId", qualifiedBy = MapCompanyToCompanyId.class)
    })
    EmployeeBulkDTO toDto(Employee employee);

    @Mappings({
        @Mapping(source = "dateOfBirth", target = "dateOfBirth", dateFormat = "yyyyMMdd"),
        @Mapping(source = "companyId", target = "company", qualifiedBy = MapCompanyIdToCompany.class)
    })
    Employee toEntity(EmployeeBulkDTO employeeDTO);


    default GenderType map(String genderString) {
        return GenderType.fromString(genderString);
    }

    default String map(GenderType genderType) {
        if (genderType == null) {
            return "u";
        }
        return genderType.toString();
    }


    default Employee fromId(Long id) {
        if (id == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }
}
