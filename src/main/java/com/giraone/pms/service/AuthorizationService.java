package com.giraone.pms.service;

public interface AuthorizationService {

    public boolean check(String companyExternalId, String principal);

    public boolean check(long companyId, String principal);
}
