package com.giraone.pms.repository;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import io.micrometer.core.annotation.Timed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Spring Data repository for the {@link Employee} entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom<Employee> {

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
    @Query("SELECT e FROM Employee e" +
        " WHERE e.company = :company" +
        " AND e.surname LIKE :surname")
    Page<Employee> findAllByCompanyAndSurnameLike(
        @Param("company") Company company, @Param("surname") String surname, Pageable pageable);


    // --- QUERIES WITHOUT COMPANY -------------------------------------------------------------------------------------

    @Timed
    @Query("SELECT distinct e FROM Employee e, EmployeeName en" +
        " WHERE e = en.id.owner" +
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
