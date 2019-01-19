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
     * Get the "id" company.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<CompanyDTO> findOne(Long id);

    /**
     * Get the company by Id.
     *
     * @param name the name of the entity
     * @return the entity
     */
    Optional<CompanyDTO> findOneByName(String name);

    /**
     * Delete the "id" company.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
