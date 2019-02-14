package com.giraone.pms.repository;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Spring Data repository for the Company entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query(value = "select distinct c from Company c inner join c.users u where u.id = :userId",
        countQuery = "select count(distinct c) from Company c inner join c.users u where u.id = :userId")
    Page<Company> findCompaniesOfUserByUserId(@Param("userId") long userId, Pageable pageable);

    @Query(value = "select distinct c from Company c inner join c.users u where u.login = :login",
        countQuery = "select count(distinct c) from Company c inner join c.users u where u.login = :login")
    Page<Company> findCompaniesOfUserByLogin(@Param("login") String login, Pageable pageable);

    // Only for admins
    @Query(value = "select distinct c from Company c left join fetch c.users",
        countQuery = "select count(distinct c) from Company c")
    Page<Company> findAllWithEagerRelationships(Pageable pageable);

    @Query("select c from Company c left join fetch c.users where c.id =:id")
    Optional<Company> findOneWithEagerRelationships(@Param("id") Long id);

    Optional<Company> findOneByExternalId(String externalId);

    Optional<Company> findOneByIdAndUsersLogin(long id, String login);

    Optional<Company> findOneByExternalIdAndUsersLogin(String externalId, String login);

    @Query(value = "select distinct u from User u inner join u.companies c where c.id = :companyId",
        countQuery = "select count(distinct u) from User u inner join u.companies c where c.id = :companyId")
    Page<User> findUsersOfCompanyByCompanyId(@Param("companyId") long companyId, Pageable pageable);

    @Query(value = "select distinct u from User u inner join u.companies c where c.externalId = :companyExternalId",
        countQuery = "select count(distinct u) from User u inner join u.companies c where c.externalId = :companyExternalId")
    Page<User> findUsersOfCompanyByCompanyExternalId(@Param("companyExternalId") String companyExternalId, Pageable pageable);
}
