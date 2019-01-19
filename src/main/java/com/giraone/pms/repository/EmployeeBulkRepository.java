package com.giraone.pms.repository;

import com.giraone.pms.domain.Employee;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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

    @Transactional
    public int save(List<Employee> employees) {
        employees.forEach(employee -> {
            entityManager.persist(employee);
        });
        return employees.size();
    }
}
