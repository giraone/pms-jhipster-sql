package com.giraone.pms.repository;

import com.giraone.pms.domain.Employee;
import com.giraone.pms.domain.EmployeeName;
import com.giraone.pms.domain.User;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.UserDTO;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;

@Repository
public class EmployeeBulkRepository extends SimpleJpaRepository<Employee, String> {

    private EntityManager entityManager;
    private CompanyRepository companyRepository;

    public EmployeeBulkRepository(EntityManager entityManager, CompanyRepository companyRepository) {
        super(Employee.class, entityManager);
        this.entityManager = entityManager;
        this.companyRepository = companyRepository;
    }

    public int saveAllEmployees(List<Employee> employees) {
        employees.forEach(employee -> {
            entityManager.persist(employee);
        });
        return employees.size();
    }

    public int saveAllEmployeeNames(List<EmployeeName> employeeNames) {
        employeeNames.forEach(employeeName -> {
            entityManager.persist(employeeName);
        });
        return employeeNames.size();
    }
}
