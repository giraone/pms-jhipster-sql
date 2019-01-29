package com.giraone.pms.repository;

import com.codahale.metrics.annotation.Timed;
import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // --- WITH COMPANY ------------------------------------------------------------------------------------------------

    @Timed
    Page<Employee> findAllByCompany(Company company, Pageable pageable);

    @Timed
    @Query("SELECT distinct e FROM Employee e, EmployeeName en" +
        " WHERE e.id = en.id.ownerId" +
        " AND e.company = :company" +
        " AND en.id.nameKey = :nameKey AND en.id.nameValue LIKE :nameValue")
    Page<Employee> findAllByCompanyAndKeyPairLike(
        @Param("company") Company company,
        @Param("nameKey") String key,
        @Param("nameValue") String value,
        Pageable pageable);

    @Timed
    @Query("SELECT e FROM Employee e" +
        " WHERE e.company = :company" +
        " AND e.surname LIKE :surname")
    Page<Employee> findAllByCompanyAndSurnameLike(
        @Param("company") Company company, @Param("surname") String surname, Pageable pageable);


    // --- WITHOUT COMPANY ---------------------------------------------------------------------------------------------

    @Timed
    @Query("SELECT distinct e FROM Employee e, EmployeeName en" +
        " WHERE e.id = en.id.ownerId" +
        " AND en.id.nameKey = :nameKey AND en.id.nameValue LIKE :nameValue")
    Page<Employee> findAllByKeyPairLike(
        @Param("nameKey") String key,
        @Param("nameValue") String value,
        Pageable pageable);

    @Timed
    @Query("SELECT e FROM Employee e" +
        " WHERE e.surname LIKE :surname")
    Page<Employee> findAllBySurnameLike(
        @Param("surname") String surname, Pageable pageable);

}
