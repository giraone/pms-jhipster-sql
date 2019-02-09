package com.giraone.pms.repository;

import com.giraone.pms.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for the Company entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query(value = "select distinct company from Company company left join fetch company.users",
        countQuery = "select count(distinct company) from Company company")
    Page<Company> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct company from Company company left join fetch company.users")
    List<Company> findAllWithEagerRelationships();

    @Query("select company from Company company left join fetch company.users where company.id =:id")
    Optional<Company> findOneWithEagerRelationships(@Param("id") Long id);

    Optional<Company> findOneByExternalId(String externalId);

    Optional<Company> findOneByIdAndUsersLogin(long id, String login);

    Optional<Company> findOneByExternalIdAndUsersLogin(String externalId, String login);

    @Query("select distinct c from Company c join c.users u where u.login = :login")
    Page<Company> findCompaniesOfUser(@Param("login") String login, Pageable pageable);
}
