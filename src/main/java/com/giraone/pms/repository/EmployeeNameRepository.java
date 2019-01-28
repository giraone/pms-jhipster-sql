package com.giraone.pms.repository;

import com.giraone.pms.domain.EmployeeName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the EmployeeName entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeNameRepository extends JpaRepository<EmployeeName, Long> {

    @Query("select en from EmployeeName en where en.id.ownerId = ?1 and en.id.nameKey = ?2")
    List<EmployeeName> findAllByOwnerIdAndNameKey(long ownerId, String nameKey);

    @Modifying
    @Transactional
    @Query("delete from EmployeeName en where en.id.ownerId = ?1")
    void deleteByOwner(long ownerId);
}
