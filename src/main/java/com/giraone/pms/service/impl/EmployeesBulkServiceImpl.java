package com.giraone.pms.service.impl;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import com.giraone.pms.repository.EmployeeBulkRepository;
import com.giraone.pms.security.AuthoritiesConstants;
import com.giraone.pms.service.CompanyService;
import com.giraone.pms.service.EmployeeBulkService;
import com.giraone.pms.service.UserService;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeBulkDTO;
import com.giraone.pms.service.dto.UserDTO;
import com.giraone.pms.service.mapper.CompanyMapper;
import com.giraone.pms.service.mapper.EmployeeBulkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class EmployeesBulkServiceImpl implements EmployeeBulkService {

    private final Logger log = LoggerFactory.getLogger(EmployeesBulkServiceImpl.class);

    private static final Set<String> LOCAL_ADMIN_AUTHORITIES = new HashSet<>();

    static {
        LOCAL_ADMIN_AUTHORITIES.add(AuthoritiesConstants.USER); // TODO: Add LOCAL_ADMIN role
    }

    private final EmployeeBulkRepository employeeBulkRepository;
    private final EmployeeBulkMapper employeeBulkMapper;
    private final CompanyService companyService;
    private final CompanyMapper companyMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    public EmployeesBulkServiceImpl(EmployeeBulkRepository employeeBulkRepository,
                                    EmployeeBulkMapper employeeBulkMapper,
                                    CompanyService companyService,
                                    CompanyMapper companyMapper,
                                    UserService userService,
                                    PasswordEncoder passwordEncoder
    ) {
        this.employeeBulkRepository = employeeBulkRepository;
        this.employeeBulkMapper = employeeBulkMapper;
        this.companyService = companyService;
        this.companyMapper = companyMapper;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Save a list of employees in one transaction and create companies dynamically.
     *
     * @param employeeDTOList the entity list to save
     * @return the number of saved employees
     */
    public int save(List<EmployeeBulkDTO> employeeDTOList) {

        final List<Employee> employees = this.employeeBulkMapper.toEntity(employeeDTOList);
        employees.stream().forEach(employee -> {
            // Store the company, if it doesn't yet exist
            final String companyName = employee.getCompany().getName();
            Optional<CompanyDTO> optionalCompany = this.companyService.findOneByName(companyName);
            if (optionalCompany.isPresent()) {
                final Company company = this.companyMapper.toEntity(optionalCompany.get());
                employee.setCompany(company);
            } else {
                // company does not yet exist, so insert company.
                // TODO: currently we use address of first employee as company address
                CompanyDTO companyDTO = new CompanyDTO();
                companyDTO.setName(companyName);
                companyDTO.setPostalCode(employee.getPostalCode());
                companyDTO.setCity(employee.getCity());
                companyDTO.setStreetAddress(employee.getStreetAddress());
                companyDTO = this.companyService.save(companyDTO);
                employee.setCompany(this.companyMapper.toEntity(companyDTO));
                // and create an initial user for this company
                final UserDTO userDTO = new UserDTO();
                final String userId = String.format("user-%08d", companyDTO.getId());
                userDTO.setFirstName("Admin");
                userDTO.setLastName(companyName);
                userDTO.setLogin(userId);
                userDTO.setAuthorities(LOCAL_ADMIN_AUTHORITIES);
                this.userService.createUserWithPresetPassword(userDTO, userId);
            }
        });
        List<Employee> result = this.employeeBulkRepository.saveAll(employees);
        return result.size();
    }
}
