package com.giraone.pms.service.impl;

import com.giraone.pms.service.AuthorizationService;
import com.giraone.pms.service.CompanyService;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.web.rest.EmployeeDomainResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeDomainResource.class);

    private final CompanyService companyService;

    public AuthorizationServiceImpl(
        CompanyService companyService) {
        this.companyService = companyService;
    }

    public boolean check(String companyExternalId, String principal) {
        // companyExternalId=l-00000060, principal=user-00001304
        log.debug("AuthorizationService.check : companyExternalId={}, principal={}", companyExternalId, principal);
        Optional<CompanyDTO> company = this.companyService.findOneByExternalId(companyExternalId);
        return company.isPresent() && String.format("user-%08d",company.get().getId()).equals(principal);
    }

    public boolean check(long companyId, String principal) {
        // companyExternalId=l-00000060, principal=user-00001304
        log.debug("AuthorizationService.check : companyId={}, principal={}", companyId, principal);
        return String.format("user-%08d", companyId).equals(principal);
    }
}
