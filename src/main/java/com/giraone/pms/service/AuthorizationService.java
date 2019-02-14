package com.giraone.pms.service;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.User;

import java.util.Optional;

public interface AuthorizationService {

    /**
     * Check, that the current user is member of a company
     * @param company The company entity
     * @return true, if user is member, false otherwise
     */
    boolean check(Company company);

    /**
     * Check, that the current user is member of a company
     * @param companyExternalId The company's external ID
     * @return true, if user is member, false otherwise
     */
    boolean check(String companyExternalId);

    /**
     * Check, that the current user is member of a company
     * @param companyId The company's internal ID
     * @return true, if user is member, false otherwise
     */
    boolean check(long companyId);

    /**
     * Check, that a user is member of a company
     * @param companyExternalId The company's external ID
     * @param principal the user login of the user
     * @return true, if user is member, false otherwise
     */
    boolean check(String companyExternalId, String principal);

    /**
     * Check, that a user is member of a company
     * @param companyId The company's internal ID
     * @param principal the user login of the user
     * @return true, if user is member, false otherwise
     */
    boolean check(long companyId, String principal);

    /**
     * Check, if the current user is an administrator (ROLE_ADMIN)
     * @return true, if admin, false otherwise
     */
    boolean isAdmin();

    long getCurrentUserId();

    Optional<String> getCurrentUserLogin();

    Optional<User> getCurrentUser();
}
