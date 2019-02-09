package com.giraone.pms.service;

public interface AuthorizationService {

    boolean check(String companyExternalId, String principal);

    boolean check(long companyId, String principal);
}
