package com.giraone.pms.service.impl;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import com.giraone.pms.domain.User;
import com.giraone.pms.repository.EmployeeRepository;
import com.giraone.pms.security.AuthoritiesConstants;
import com.giraone.pms.service.*;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeBulkDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import com.giraone.pms.service.dto.UserDTO;
import com.giraone.pms.service.mapper.CompanyMapper;
import com.giraone.pms.service.mapper.EmployeeBulkMapper;
import com.giraone.pms.service.mapper.EmployeeMapper;
import com.giraone.pms.service.mapper.UserMapper;
import io.micrometer.core.annotation.Timed;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
// Hint: @Transactional is done on the repository level!
@SuppressWarnings("unused")
public class EmployeesBulkServiceImpl implements EmployeeBulkService {

    private final Logger log = LoggerFactory.getLogger(EmployeesBulkServiceImpl.class);

    private static final boolean CREATE_INITIAL_USER_FOR_COMPANY = true;
    private static final boolean WITH_METAPHONE = false;

    private static final Set<String> INITIAL_USER_AUTHORITIES = new HashSet<>();

    static {
        INITIAL_USER_AUTHORITIES.add(AuthoritiesConstants.USER);
    }

    private final DoubleMetaphone doubleMetaphone = new DoubleMetaphone();

    private final EmployeeRepository employeeRepository;
    private final EmployeeBulkMapper employeeBulkMapper;
    private final EmployeeMapper employeeMapper;
    private final EmployeeService employeeService;
    private final CompanyService companyService;
    private final CompanyMapper companyMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    public EmployeesBulkServiceImpl(EmployeeRepository employeeRepository,
                                    EmployeeBulkMapper employeeBulkMapper,
                                    EmployeeMapper employeeMapper,
                                    EmployeeService employeeService,
                                    CompanyService companyService,
                                    CompanyMapper companyMapper,
                                    UserService userService,
                                    UserMapper userMapper
    ) {
        this.employeeRepository = employeeRepository;
        this.employeeBulkMapper = employeeBulkMapper;
        this.employeeMapper = employeeMapper;
        this.employeeService = employeeService;
        this.companyService = companyService;
        this.companyMapper = companyMapper;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Save a list of employees in one transaction and create companies dynamically.
     *
     * @param employeeDTOList the entity list to save
     * @return the number of saved employees
     */
    @Timed
    @Transactional
    public int save(List<EmployeeBulkDTO> employeeDTOList) {

        final List<Employee> employees = this.employeeBulkMapper.toEntity(employeeDTOList);
        employees.forEach(employee -> {
            // Store the company, if it doesn't yet exist
            final String externalId = employee.getCompany().getExternalId();
            Optional<CompanyDTO> optionalCompany = this.companyService.findOneByExternalId(externalId);
            if (optionalCompany.isPresent()) {
                final Company company = this.companyMapper.toEntity(optionalCompany.get());
                employee.setCompany(company);
            } else {
                // company does not yet exist, so insert company.
                // TODO: currently we use address of first employee as company address
                CompanyDTO companyDTO = new CompanyDTO();
                companyDTO.setExternalId(externalId);
                companyDTO.setName(employee.getSurname() + " GmbH");
                companyDTO.setPostalCode(employee.getPostalCode());
                companyDTO.setCity(employee.getCity());
                companyDTO.setStreetAddress(employee.getStreetAddress());
                companyDTO = this.companyService.save(companyDTO);
                employee.setCompany(this.companyMapper.toEntity(companyDTO));
                // ... and create an initial user for this company
                if (CREATE_INITIAL_USER_FOR_COMPANY) {
                    UserDTO userDTO = new UserDTO();
                    final String userId = String.format("user-%08d", companyDTO.getId());
                    userDTO.setFirstName("Admin");
                    userDTO.setLastName(externalId);
                    userDTO.setLogin(userId);
                    userDTO.setAuthorities(INITIAL_USER_AUTHORITIES);
                    User user = this.userService.createUserWithPresetPassword(userDTO, userId);
                    userDTO = this.userMapper.userToUserDTO(user);
                    // ... and add this initial users to the companies user list
                    final HashSet<UserDTO> users = new HashSet<>();
                    users.add(userDTO);
                    companyDTO.setUsers(users);
                    this.companyService.save(companyDTO);
                }
            }
        });
        return this.employeeRepository.saveAllCustom(employees).size();
    }

    @Timed
    // NO @Transactional !!!
    public int reIndex(boolean clearFirst) {
        final int pageSize = 1000;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<EmployeeDTO> pages;
        int ret = 0;
        do {
            final long start = System.currentTimeMillis();
            pages = this.employeeService.findAllByFilter(null, null, pageable).get();

            ret += reIndex(pages.getNumber(), pages.getContent().stream(), clearFirst);
            pageable = pageable.next();
            final long end = System.currentTimeMillis();
            log.info("Page {} of {} in {} msec", pages.getNumber(), pages.getTotalPages(), end - start);
        } while (pages.hasNext());
        return ret;
    }

    @Timed
    @Transactional
    // public because otherwise @Transactional is not working
    public int reIndex(int pageIndex, Stream<EmployeeDTO> employeeStream, boolean clearFirst) {
        log.info("EmployeesBulkServiceImpl.reIndex {}", pageIndex);
        List<Employee> employeeList = employeeStream.map(
            employeeDTO -> employeeMapper.toEntity(employeeDTO)).collect(Collectors.toList());
        int ret = this.employeeRepository.reIndex(employeeList, !clearFirst);
        log.info("EmployeesBulkServiceImpl.reIndex: insert for {} names", ret);
        return ret;
    }
}
