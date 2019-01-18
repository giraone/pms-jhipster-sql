package com.giraone.pms.repository;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class EmployeeBulkImportRepository extends SimpleJpaRepository<Employee, String> {

    private EntityManager entityManager;
    private CompanyRepository companyRepository;
    private Company company;

    public EmployeeBulkImportRepository(EntityManager entityManager, CompanyRepository companyRepository) {
        super(Employee.class, entityManager);
        this.entityManager = entityManager;
        this.companyRepository = companyRepository;
        this.company = this.companyRepository.findAll().get(0);
    }

    @Transactional
    public int save(List<Employee> employees) {
        employees.forEach(employee -> {
            employee.setCompany(company);
            entityManager.persist(employee);
        });
        return employees.size();
    }
}
