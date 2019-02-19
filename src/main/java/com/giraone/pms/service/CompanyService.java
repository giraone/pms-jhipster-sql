package com.giraone.pms.service;

import com.giraone.pms.domain.User;
import com.giraone.pms.service.dto.CompanyBasicInfoDTO;
import com.giraone.pms.service.dto.CompanyDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Company.
 */
public interface CompanyService {

    /**
     * Save a company.
     *
     * @param companyDTO the entity to save
     * @return the persisted entity
     */
    CompanyDTO save(CompanyDTO companyDTO);

    /**
     * Get all the companies.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CompanyDTO> findAll(Pageable pageable);

    /**
     * Get all the companies of a user with minimal information
     * @return the list of companies the user has access to
     */
    List<CompanyBasicInfoDTO> findAllOfUserWithBasicInfosOnly();

    /**
     * Get all the Company with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    Page<CompanyDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" company.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<CompanyDTO> findOne(long id);

    /**
     * Get the "externalId" company.
     *
     * @param externalId the externalId of the entity
     * @return the entity
     */
    Optional<CompanyDTO> findOneByExternalId(String externalId);

    /**
     * Delete the "id" company.
     *
     * @param id the id of the entity
     */
    void delete(long id);

    /**
     * Assign an user to a company.
     *
     * @param companyExternalId the externalId of the company
     * @param userLogin         the user to be added
     * @return true, if the user was added or already contained, false on any error
     */
    boolean addUserToCompany(String companyExternalId, String userLogin);

    /**
     * Check, whether user is in a company
     *
     * @param companyExternalId the externalId of the company
     * @param userLogin         the user lign to be checked
     * @return true, if the user is assigned to the company
     */
    boolean isUserInCompany(String companyExternalId, String userLogin);

    /**
     * Check, whether user is in a company
     *
     * @param companyId the id of the company
     * @param userLogin the user lign to be checked
     * @return true, if the user is assigned to the company
     */
    boolean isUserInCompany(long companyId, String userLogin);

    /**
     * Return all users of a company
     *
     * @param companyId the id of the company
     * @param pageable the pagination information
     * @return true, if the user is assigned to the company
     */
    Page<User> findAllUserInCompany(long companyId, Pageable pageable);

    /**
     * Return all users of a company
     *
     * @param @param companyExternalId the externalId of the company
     * @param pageable the pagination information
     * @return true, if the user is assigned to the company
     */
    Page<User> findAllUserInCompany(String companyExternalId, Pageable pageable);
}
