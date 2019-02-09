package com.giraone.pms.service.impl;

import com.giraone.pms.service.AuthorizationService;
import com.giraone.pms.service.CompanyService;
import com.giraone.pms.web.rest.EmployeeDomainResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeDomainResource.class);

    private final CompanyService companyService;

    public AuthorizationServiceImpl(
        CompanyService companyService) {
        this.companyService = companyService;
    }

    public boolean check(String companyExternalId, String principal) {

        final boolean ret = this.companyService.isUserInCompany(companyExternalId, principal);
        log.debug("AuthorizationService.check : companyExternalId={}, principal={} = {}", companyExternalId, principal);
        return ret;
    }

    public boolean check(long companyId, String principal) {

        final boolean ret = this.companyService.isUserInCompany(companyId, principal);
        log.debug("AuthorizationService.check : companyId={}, principal={} = {}", companyId, principal);
        return ret;
    }
}
