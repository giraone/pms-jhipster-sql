package com.giraone.pms.repository;

import com.giraone.pms.domain.Employee;
import com.giraone.pms.domain.EmployeeName;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Spring Data  repository for the EmployeeName entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeNameRepository extends JpaRepository<EmployeeName, Long> {
    @Modifying
    @Transactional
    @Query("delete from EmployeeName en where en.id.ownerId = ?1")
    void deleteByOwner(long ownerId);

    @Modifying
    @Transactional
    @Query("delete from EmployeeName en where en.id.ownerId IN ?1")
    void deleteByOwners(List<Long> owners);
}
