package com.giraone.pms.service.mapper;

import com.giraone.pms.domain.Company;
import com.giraone.pms.service.dto.CompanyBasicInfoDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity Company and its basic info CompanyBasicInfoDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CompanyBasicInfoMapper extends EntityMapper<CompanyBasicInfoDTO, Company> {
}
