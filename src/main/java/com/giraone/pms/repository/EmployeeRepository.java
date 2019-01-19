package com.giraone.pms.repository;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;


/**
 * Spring Data  repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findAllByCompany(Company company, Pageable pageable);

    @Query("SELECT v from Employee v WHERE v.company = ?1 AND v.surname LIKE ?2 ORDER BY v.id ASC")
    Page<Employee> findAllByCompanyAndSurname(Company company, String surname, Pageable pageable);
    //Stream<Employee> findAllByCompanyAndSurname(Company company, String surname, Pageable pageable);
}
