package com.giraone.pms.service.mapper;

import com.giraone.pms.domain.Company;


public class EmployeeMappingHelper {

    @MapCompanyIdToCompany
    public static Company mapCompanyIdToCompany(String companyId) {
        Company ret = new Company();
        ret.setName(companyId);
        return ret;
    }

    @MapCompanyToCompanyId
    public String mapCompanyToCompanyId(Company company) {
        return company.getName();
    }
}
