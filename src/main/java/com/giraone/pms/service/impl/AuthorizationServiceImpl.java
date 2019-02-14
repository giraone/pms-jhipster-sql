package com.giraone.pms.service.impl;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.User;
import com.giraone.pms.repository.CompanyRepository;
import com.giraone.pms.security.AuthoritiesConstants;
import com.giraone.pms.security.SecurityUtils;
import com.giraone.pms.service.AuthorizationService;
import com.giraone.pms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

    private final CompanyRepository companyRepository;
    private final UserService userService;

    public AuthorizationServiceImpl(CompanyRepository companyRepository, UserService userService) {
        this.companyRepository = companyRepository;
        this.userService = userService;
    }

    public boolean check(Company company) {
        final Optional<String> currentUserLogin = getCurrentUserLogin();
        return currentUserLogin.filter(s -> company.getUsers().stream().anyMatch(u -> u.getLogin().equals(s))).isPresent();
    }

    /**
     * Check, that the current user is member of a company
     * @param companyExternalId The company's  external ID
     * @return true, if user is member, false otherwise
     */
    public boolean check(String companyExternalId) {
        final Optional<String> currentUserLogin = getCurrentUserLogin();
        return currentUserLogin.filter(s -> this.check(companyExternalId, s)).isPresent();
    }

    /**
     * Check, that the current user is member of a company
     * @param companyId The company's internal ID
     * @return true, if user is member, false otherwise
     */
    public boolean check(long companyId) {
        final Optional<String> currentUserLogin = getCurrentUserLogin();
        return currentUserLogin.filter(s -> this.check(companyId, s)).isPresent();
    }

    /**
     * Check, that a user is member of a company
     * @param companyExternalId The company's  external ID
     * @param principal the user login of the user
     * @return true, if user is member, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean check(String companyExternalId, String principal) {

        final boolean ret = this.companyRepository.findOneByExternalIdAndUsersLogin(companyExternalId, principal).isPresent();
        log.debug("AuthorizationService.check : companyExternalId={}, principal={} = {}", companyExternalId, principal);
        return ret;
    }

    /**
     * Check, that a user is member of a company
     * @param companyId The company's internal ID
     * @param principal the user login of the user
     * @return true, if user is member, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean check(long companyId, String principal) {

        final boolean ret = this.companyRepository.findOneByIdAndUsersLogin(companyId, principal).isPresent();
        log.debug("AuthorizationService.check : companyId={}, principal={} = {}", companyId, principal);
        return ret;
    }

    /**
     * Check, if the current user is an administrator (ROLE_ADMIN)
     * @return true, if admin, false otherwise
     */
    public boolean isAdmin() {
        return SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN);
    }

    public long getCurrentUserId() {
        final Optional<User> currentUser = getCurrentUser();
        if (currentUser.isPresent()) {
            return currentUser.get().getId();
        } else {
            return -1L; // Anonymous
        }
    }

    public Optional<String> getCurrentUserLogin() {
        return SecurityUtils.getCurrentUserLogin();
    }

    public Optional<User> getCurrentUser() {
        final Optional<String> currentUserLogin = getCurrentUserLogin();
        if (!currentUserLogin.isPresent()) {
            return Optional.empty();
        }
        return userService.getUserWithAuthoritiesByLogin(currentUserLogin.get());
    }
}
