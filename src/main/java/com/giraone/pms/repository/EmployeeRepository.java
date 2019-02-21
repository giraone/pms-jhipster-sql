package com.giraone.pms.repository;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import io.micrometer.core.annotation.Timed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


/**
 * Spring Data repository for the {@link Employee} entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface EmployeeRepository
    extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom<Employee>, JpaSpecificationExecutor<Employee> {

    // --- QUERIES WITH COMPANY ----------------------------------------------------------------------------------------

    @Timed
    Page<Employee> findAllByCompany(Company company, Pageable pageable);

    @Timed
    @Query("SELECT distinct e FROM Employee e, EmployeeName en" +
        " WHERE e = en.id.owner" +
        " AND e.company = :company" +
        " AND en.id.nameKey = :nameKey AND en.id.nameValue LIKE :nameValue")
    Page<Employee> findAllByCompanyAndKeyPairLike(
        @Param("company") Company company,
        @Param("nameKey") String key,
        @Param("nameValue") String value,
        Pageable pageable);

    @Timed
    @Query("SELECT distinct e FROM Employee e, EmployeeName en" +
        " WHERE e = en.id.owner" +
        " AND e.company = :company" +
        " AND e.dateOfBirth = :dateOfBirth" +
        " AND en.id.nameKey = :nameKey AND en.id.nameValue LIKE :nameValue")
    Page<Employee> findAllByCompanyAndDateOfBirthAndKeyPairLike(
        @Param("company") Company company,
        @Param("dateOfBirth") LocalDate dateOfBirth,
        @Param("nameKey") String key,
        @Param("nameValue") String value,
        Pageable pageable);

    @Timed
    @Query("SELECT distinct e FROM Employee e, EmployeeName en" +
        " WHERE e = en.id.owner" +
        " AND e.company = :company" +
        " AND e.dateOfBirth = :dateOfBirth")
    Page<Employee> findAllByCompanyAndDateOfBirth(
        @Param("company") Company company, @Param("dateOfBirth") LocalDate dateOfBirth, Pageable pageable);

    @Timed
    @Query("SELECT distinct e FROM Employee e, EmployeeName en" +
        " WHERE e = en.id.owner" +
        " AND e.company = :company" +
        " AND en.id.nameKey = 'LS' AND en.id.nameValue LIKE :surname")
    Page<Employee> findAllByCompanyAndSurname(
        @Param("company") Company company, @Param("surname") String surname, Pageable pageable);

    @Timed
    @Query("SELECT distinct e FROM Employee e, EmployeeName en" +
        " WHERE e = en.id.owner" +
        " AND e.company = :company" +
        " AND e.dateOfBirth = :dateOfBirth" +
        " AND en.id.nameKey = 'LS' AND en.id.nameValue LIKE :surname")
    Page<Employee> findAllByCompanyAndSurnameAndDateOfBirth(
        @Param("company") Company company, @Param("surname") String surname, @Param("dateOfBirth") LocalDate dateOfBirth, Pageable pageable);

    @Timed
    @Query("SELECT distinct e FROM Employee e, EmployeeName en1, EmployeeName en2" +
        " WHERE e = en1.id.owner AND e = en2.id.owner" +
        " AND e.company = :company" +
        " AND en1.id.nameKey = 'LS' AND en1.id.nameValue LIKE :surname" +
        " AND en2.id.nameKey = 'LG' AND en2.id.nameValue LIKE :givenName")
    Page<Employee> findAllByCompanyAndSurnameAndGivenName(
        @Param("company") Company company, @Param("surname") String surname, @Param("givenName") String givenName, Pageable pageable);

    @Timed
    @Query("SELECT distinct e FROM Employee e, EmployeeName en1, EmployeeName en2" +
        " WHERE e = en1.id.owner AND e = en2.id.owner" +
        " AND e.company = :company" +
        " AND e.dateOfBirth = :dateOfBirth" +
        " AND en1.id.nameKey = 'LS' AND en1.id.nameValue LIKE :surname" +
        " AND en2.id.nameKey = 'LG' AND en2.id.nameValue LIKE :givenName")
    Page<Employee> findAllByCompanyAndSurnameAndGivenNameAndDateOfBirth(
        @Param("company") Company company, @Param("surname") String surname, @Param("givenName") String givenName,
        @Param("dateOfBirth") LocalDate dateOfBirth, Pageable pageable);

    // --- NAME INDEX  -------------------------------------------------------------------------------------------------

    /**
     * Additional method to perform re-indexing on an employee list
     *
     * @param employeeList the employee list to be re-indexed (the entities itself is untouched)
     * @param skipDeletion if true it is assumed, that there was no index yet and the delete action of existing
     *                     indixes is not performed. This is a performance optimization.
     * @return the number of re-indexed employees
     */
    int reIndex(List<Employee> employeeList, boolean skipDeletion);

    @Modifying
    @Transactional
    @Query("delete from EmployeeName en where en.id.owner.id = ?1")
    void deleteByOwner(long ownerId);

    @Modifying
    @Transactional
    @Query("delete from EmployeeName en where en.id.owner.id IN ?1")
    void deleteByOwners(List<Long> owners);
}
