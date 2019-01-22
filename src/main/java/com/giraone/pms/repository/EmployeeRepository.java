package com.giraone.pms.repository;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findAllByCompany(Company company, Pageable pageable);

    @Query("SELECT v from Employee v WHERE v.company = ?1 AND v.surname LIKE ?2")
    Page<Employee> findAllByCompanyAndSurname(Company company, String surname, Pageable pageable);

    @Query("SELECT v from Employee v WHERE v.surname LIKE ?1")
    Page<Employee> findAllBySurname(String surname, Pageable pageable);
}
