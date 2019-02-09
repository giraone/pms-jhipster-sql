package com.giraone.pms.repository;

import com.giraone.pms.domain.EmployeeName;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the EmployeeName entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeNameRepository extends JpaRepository<EmployeeName, Long> {

}
