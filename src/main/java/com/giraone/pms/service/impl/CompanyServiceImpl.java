package com.giraone.pms.service.impl;

import com.giraone.pms.domain.User;
import com.giraone.pms.service.CompanyService;
import com.giraone.pms.domain.Company;
import com.giraone.pms.repository.CompanyRepository;
import com.giraone.pms.service.UserService;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.mapper.CompanyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserService userService;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper, UserService userService) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.userService = userService;
    }

    /**
     * Save a company.
     *
     * @param companyDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public CompanyDTO save(CompanyDTO companyDTO) {
        log.debug("Request to save Company : {}", companyDTO);

        Company company = companyMapper.toEntity(companyDTO);
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
        log.debug("Request to get all Companies");
        return companyRepository.findAll(pageable)
            .map(companyMapper::toDto);
    }

    /**
     * Get all the Company with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    @Override
    public Page<CompanyDTO> findAllWithEagerRelationships(Pageable pageable) {
        return companyRepository.findAllWithEagerRelationships(pageable).map(companyMapper::toDto);
    }


    /**
     * Get one company by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyDTO> findOne(Long id) {
        log.debug("Request to get Company : {}", id);
        return companyRepository.findOneWithEagerRelationships(id)
            .map(companyMapper::toDto);
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
        log.debug("Request to get company by externalId : {}", externalId);
        return companyRepository.findOneByExternalId(externalId)
            .map(companyMapper::toDto);
    }

    /**
     * Delete the company by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Company : {}", id);
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
        Optional<Company> company = companyRepository.findOneByExternalId(companyExternalId);
        if (!company.isPresent()) {
            return false;
        }
        Optional<User> user = userService.getUserWithAuthoritiesByLogin(userLogin);
        if (!user.isPresent()) {
            return false;
        }
        Set<User> users = company.get().getUsers();
        if (!users.contains(user)) {
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
    public boolean isUserInCompany(long companyId, String userLogin) {

        final Optional<Company> company = companyRepository.findOneByIdAndUsersLogin(companyId, userLogin);
        return company.isPresent();
    }
}
