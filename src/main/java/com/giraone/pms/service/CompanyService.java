package com.giraone.pms.service;

import com.giraone.pms.service.dto.CompanyDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    Optional<CompanyDTO> findOne(Long id);

    /**
     * Get the "externalId" company.
     *
     * @param externalId the externalId of the entity
     * @return the entity
     */
    Optional<CompanyDTO> findOneByExternalId(String externalId);

    /**
     * Save a employee.
     *
     * @param companyExternalId the externalId of the company
     * @param userLogin the user to be added
     * @return true, if the user was added or already contained, false on any error
     */
    boolean addUserToCompany(String companyExternalId, String userLogin);

    /**
     * Delete the "id" company.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
