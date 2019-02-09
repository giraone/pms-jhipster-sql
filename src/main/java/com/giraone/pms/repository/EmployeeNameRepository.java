package com.giraone.pms.repository;

import com.giraone.pms.domain.EmployeeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Spring Data repository for the {@link EmployeeName} entity.
 * This repository is used internally.
 */
@SuppressWarnings("unused")
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface EmployeeNameRepository extends JpaRepository<EmployeeName, Long> {
}

