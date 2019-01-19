package com.giraone.pms.service.mapper;

import com.giraone.pms.domain.Company;


public class EmployeeMappingHelper {

    @MapCompanyIdToCompany
    public static Company mapCompanyIdToCompany(String companyId) {
        final Company ret = new Company();
        ret.setExternalId(companyId);
        return ret;
    }

    @MapCompanyToCompanyId
    public String mapCompanyToCompanyId(Company company) {
        return company.getExternalId();
    }
}
