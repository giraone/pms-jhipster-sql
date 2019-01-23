package com.giraone.pms.repository;

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

    Page<Employee> findAllByCompany(Company company, Pageable pageable);

    @Query("SELECT e FROM Employee e JOIN EmployeeName en WHERE e.company = :company AND en.value LIKE :normalizedName")
    Page<Employee> findAllByCompanyAndNormalizedName(@Param("company") Company company, @Param("normalizedName") String normalizedName, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.company = :company AND e.surname LIKE :surname")
    Page<Employee> findAllByCompanyAndSurname(@Param("company") Company company, @Param("surname") String surname, Pageable pageable);

    //@Query("SELECT e FROM Employee e WHERE e IN (SELECT en FROM EmployeeName en WHERE en.value LIKE :normalizedName)")
    @Query("SELECT e FROM Employee e, EmployeeName en WHERE en.owner = e AND en.value LIKE :normalizedName")
    Page<Employee> findAllByNormalizedName(@Param("normalizedName") String normalizedName, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE e.surname LIKE :surname")
    Page<Employee> findAllBySurname(@Param("surname") String surname, Pageable pageable);
}

/*
select *
from employee e
where e.id in (select en.owner_id from employee_name en where en.jhi_value like 'th'
order by e.id asc
limit 10
 */
