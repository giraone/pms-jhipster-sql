package com.giraone.pms.repository;

import com.giraone.pms.domain.EmployeeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Spring Data repository for the EmployeeName entity.
 */
@SuppressWarnings("unused")
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface EmployeeNameRepository extends JpaRepository<EmployeeName, Long> {

    @Query("select en from EmployeeName en where en.id.ownerId = ?1 and en.id.nameKey = ?2")
    List<EmployeeName> findAllByOwnerIdAndNameKey(long ownerId, String nameKey);

    @Modifying
    @Transactional
    @Query("delete from EmployeeName en where en.id.ownerId = ?1")
    void deleteByOwner(long ownerId);

    @Modifying
    @Transactional
    @Query("delete from EmployeeName en where en.id.ownerId IN ?1")
    void deleteByOwners(List<Long> owners);
}

