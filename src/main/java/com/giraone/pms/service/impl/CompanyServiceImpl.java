package com.giraone.pms.service.impl;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.User;
import com.giraone.pms.repository.CompanyRepository;
import com.giraone.pms.service.AuthorizationService;
import com.giraone.pms.service.CompanyService;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.mapper.CompanyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service Implementation for managing Company.
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final AuthorizationService authorizationService;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper,
                              AuthorizationService authorizationService) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.authorizationService = authorizationService;
    }

    /**
     * Save a company.
     *
     * @param companyDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public CompanyDTO save(CompanyDTO companyDTO) {

        final boolean isAdmin = authorizationService.isAdmin();
        log.debug("Request to save Company admin={} company={}", isAdmin, companyDTO);
        Company company = companyMapper.toEntity(companyDTO);
        if (company.getUsers() == null) {
            company.setUsers(new HashSet<>());
        }
        if (!isAdmin && !authorizationService.check(company)) {
            Optional<User> currentUser = authorizationService.getCurrentUser();
            if (!currentUser.isPresent()) {
                throw new AccessDeniedException(
                    String.format("SECURITY-WARNING: Anonymous user tries to save company %s!", company.getExternalId()));
            }
            log.debug("Adding user {} to new company {}", currentUser.get().getLogin(), company.getExternalId());
            company.getUsers().add(currentUser.get());
        }
        company = companyRepository.save(company);
        return companyMapper.toDto(company);
    }

    /**
     * Get all the companies.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CompanyDTO> findAll(Pageable pageable) {

        final boolean isAdmin = authorizationService.isAdmin();
        log.debug("Request to get all Companies admin={}", isAdmin);
        Page<Company> page;
        if (isAdmin) {
            page = companyRepository.findAll(pageable);
        } else {
            page = companyRepository.findCompaniesOfUserByUserId(authorizationService.getCurrentUserId(), pageable);
        }
        return page.map(companyMapper::toDto);
    }

    /**
     * Get all the Company with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    @Override
    public Page<CompanyDTO> findAllWithEagerRelationships(Pageable pageable) {

        final boolean isAdmin = authorizationService.isAdmin();
        log.debug("Request to get all Companies with eager relationships admin={}", isAdmin);
        Page<Company> page;
        if (isAdmin) {
            page = companyRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = companyRepository.findCompaniesOfUserByUserId(authorizationService.getCurrentUserId(), pageable);
        }
        return page.map(companyMapper::toDto);
    }


    /**
     * Get one company by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyDTO> findOne(long id) {

        final boolean isAdmin = authorizationService.isAdmin();
        log.debug("Request to get company admin={} id={}", isAdmin, id);
        Optional<Company> company = companyRepository.findOneWithEagerRelationships(id);
        if (!company.isPresent()) {
            return Optional.empty();
        }
        if (!isAdmin && !authorizationService.check(company.get())) {
            throw new AccessDeniedException(
                String.format("SECURITY-WARNING: User with id=%d tries to access company %s without access rights!",
                    authorizationService.getCurrentUserId(), company.get().getExternalId()));
        }
        return company.map(companyMapper::toDto);
    }

    /**
     * Get the "externalId" company.
     *
     * @param externalId the externalId of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyDTO> findOneByExternalId(String externalId) {

        final boolean isAdmin = authorizationService.isAdmin();
        log.debug("Request to get company by externalId admin={} externalId={}", externalId);

        Optional<Company> company = companyRepository.findOneByExternalId(externalId);
        if (!company.isPresent()) {
            return Optional.empty();
        }
        if (!isAdmin && !authorizationService.check(company.get())) {
            throw new AccessDeniedException(
                String.format("SECURITY-WARNING: User with id=%d tries to access company externalId=%s without access rights!",
                    authorizationService.getCurrentUserId(), externalId));
        }
        return company.map(companyMapper::toDto);
    }

    /**
     * Delete the company by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(long id) {

        final boolean isAdmin = authorizationService.isAdmin();
        log.debug("Request to delete Company : admin={} id={}", id);
        Optional<Company> company = companyRepository.findOneWithEagerRelationships(id);
        if (!company.isPresent()) {
            log.warn("Deletion of company id=%d requested, which is not stored!", id);
            return;
        }
        if (!isAdmin && !authorizationService.check(company.get())) {
            throw new AccessDeniedException(
                String.format("SECURITY-WARNING: User with id=%d tries to delete company externalId=%s without access rights!",
                    authorizationService.getCurrentUserId(), company.get().getExternalId()));
        }
        companyRepository.deleteById(id);
    }

    /**
     * Assign an user to a company.
     *
     * @param companyExternalId the externalId of the company
     * @param userLogin         the user to be added
     * @return true, if the user was added or already contained, false on any error
     */
    @Override
    public boolean addUserToCompany(String companyExternalId, String userLogin) {

        final boolean isAdmin = authorizationService.isAdmin();
        log.debug("Request to addUserToCompany : admin={} companyExternalId={} userLogin={}", isAdmin, companyExternalId, userLogin);

        Optional<Company> company = companyRepository.findOneByExternalId(companyExternalId);
        if (!company.isPresent()) {
            return false;
        }
        Optional<User> user = authorizationService.getCurrentUser();
        if (!user.isPresent()) {
            return false;
        }

        // Only users, that are already part of a company can add other users to the company
        if (!isAdmin && !authorizationService.check(company.get())) {
            throw new AccessDeniedException(
                String.format("SECURITY-WARNING: User with id=%d tries to addUserToCompany externalId=%s without access rights!",
                    authorizationService.getCurrentUserId(), company.get().getExternalId()));
        }

        Set<User> users = company.get().getUsers();
        if (!users.contains(user.get())) {
            users.add(user.get());
            companyRepository.save(company.get());
        }
        return true;
    }

    /**
     * Check, whether user is in a company
     *
     * @param companyExternalId the externalId of the company
     * @param userLogin         the user lign to be checked
     * @return true, if the user is assigned to the company
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isUserInCompany(String companyExternalId, String userLogin) {

        final Optional<Company> company = companyRepository.findOneByExternalIdAndUsersLogin(companyExternalId, userLogin);
        return company.isPresent();
    }

    /**
     * Check, whether user is in a company
     *
     * @param companyId the id of the company
     * @param userLogin the user lign to be checked
     * @return true, if the user is assigned to the company
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isUserInCompany(long companyId, String userLogin) {

        final Optional<Company> company = companyRepository.findOneByIdAndUsersLogin(companyId, userLogin);
        return company.isPresent();
    }

    /**
     * Return all users of a company
     *
     * @param companyId the id of the company
     * @return true, if the user is assigned to the company
     */
    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllUserInCompany(long companyId, Pageable pageable) {
        return companyRepository.findUsersOfCompanyByCompanyId(companyId, pageable);
    }

    /**
     * Return all users of a company
     *
     * @param companyExternalId the externalId of the company
     * @return true, if the user is assigned to the company
     */
    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllUserInCompany(String companyExternalId, Pageable pageable) {
        return companyRepository.findUsersOfCompanyByCompanyExternalId(companyExternalId, pageable);
    }
}
