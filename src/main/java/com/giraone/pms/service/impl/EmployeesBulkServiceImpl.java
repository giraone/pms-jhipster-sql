package com.giraone.pms.service.impl;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import com.giraone.pms.repository.CompanyRepository;
import com.giraone.pms.repository.EmployeeBulkRepository;
import com.giraone.pms.service.dto.EmployeeBulkDTO;
import com.giraone.pms.service.mapper.EmployeeBulkMapper;
import com.giraone.pms.service.EmployeeBulkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployeesBulkServiceImpl implements EmployeeBulkService {

    private final Logger log = LoggerFactory.getLogger(EmployeesBulkServiceImpl.class);

    private final EmployeeBulkRepository employeeBulkRepository;
    private final CompanyRepository companyRepository;

    private final EmployeeBulkMapper employeeBulkMapper;

    public EmployeesBulkServiceImpl(EmployeeBulkRepository employeeBulkRepository,
                                    EmployeeBulkMapper employeeBulkMapper,
                                    CompanyRepository companyRepository) {
        this.employeeBulkRepository = employeeBulkRepository;
        this.employeeBulkMapper = employeeBulkMapper;
        this.companyRepository = companyRepository;
    }

    /**
     * Save a list of employees in one transaction and create companies dynamically.
     *
     * @param employeeDTOList the entity list to save
     * @return the number of saved employees
     */
    public int save(List<EmployeeBulkDTO> employeeDTOList) {

        final List<Employee> employees = employeeBulkMapper.toEntity(employeeDTOList);
        employees.stream().forEach(employee -> {
            Optional<Company> optionalCompany = this.companyRepository.findOneByName(employee.getCompany().getName());
            if (optionalCompany.isPresent()) {
                employee.setCompany(optionalCompany.get());
            } else {
                employee.setCompany(this.companyRepository.save(employee.getCompany()));
            }

        });
        List<Company> companies = this.companyRepository.findAll();

        return this.employeeBulkRepository.save(employees);
    }
}
