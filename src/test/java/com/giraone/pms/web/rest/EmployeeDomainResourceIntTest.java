package com.giraone.pms.web.rest;

import com.giraone.pms.PmssqlApp;
import com.giraone.pms.domain.enumeration.GenderType;
import com.giraone.pms.service.CompanyService;
import com.giraone.pms.service.EmployeeDomainService;
import com.giraone.pms.service.UserService;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.Month;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the EmployeeDomainResource REST controller.
 *
 * @see EmployeeDomainResource
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = PmssqlApp.class)
public class EmployeeDomainResourceIntTest {

    @Autowired
    private EmployeeDomainService employeeDomainService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext wac;

    private CompanyDTO company;
    private MockMvc restMockEmployeeDomainResource;

    @Before
    public void setupCompanyAndUsers() {

        CompanyDTO company = new CompanyDTO();
        company.setName("Test-Company");
        company.setExternalId("test1");
        this.company = companyService.save(company);

        /*
        UserDTO admin = new UserDTO();
        admin.setLogin("admin");
        userService.createUser(admin);

        UserDTO user = new UserDTO();
        user.setLogin("user");
        userService.createUser(user);

        companyService.addUserToCompany(this.company.getExternalId(), user.getLogin());
        */

        companyService.addUserToCompany(this.company.getExternalId(), "user");
    }

    @Before
    public void setupMocks() {

        MockitoAnnotations.initMocks(this);
        // (S) final EmployeeDomainResource employeeDomainResource = new EmployeeDomainResource(employeeDomainService, authorizationService);
        this.restMockEmployeeDomainResource = MockMvcBuilders
            .webAppContextSetup(wac)
            // (S) .standaloneSetup(employeeDomainResource)
            .apply(springSecurity())
            .alwaysDo(print())
            .build();
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void getAllEmployeesAsAdmin() throws Exception {

        // arrange
        EmployeeDTO employee = employeeDomainService.save(getEmployeeSample());

        // act
        ResultActions resultActions = restMockEmployeeDomainResource
            .perform(get("/domain-api/employees?sort=id,desc"));

        // assert
        resultActions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(employee.getSurname())))
            .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(employee.getDateOfBirth().toString())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(employee.getGender().name())))
            .andExpect(jsonPath("$.[*].companyId").value(hasItem(employee.getCompanyId().intValue())));
    }

    @Ignore
    @Test
    @Transactional
    @WithMockUser(username = "user", roles = "USER")
    public void getAllEmployeesAsUser() throws Exception {

        // arrange
        EmployeeDTO employee = employeeDomainService.save(getEmployeeSample());

        // act
        ResultActions resultActions = restMockEmployeeDomainResource
            .perform(get("/domain-api/employees?sort=id,desc"));

        // assert
        resultActions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(employee.getSurname())))
            .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(employee.getDateOfBirth().toString())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(employee.getGender().name())))
            .andExpect(jsonPath("$.[*].companyId").value(hasItem(employee.getCompanyId().intValue())));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void getEmployeeAsAdmin() throws Exception {

        // arrange
        EmployeeDTO employee = employeeDomainService.save(getEmployeeSample());

        // act
        ResultActions resultActions = restMockEmployeeDomainResource
            .perform(get("/domain-api/employees/" + employee.getId()));

        // assert
        resultActions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(employee.getId().intValue()))
            .andExpect(jsonPath("$.surname").value(employee.getSurname()))
            .andExpect(jsonPath("$.dateOfBirth").value(employee.getDateOfBirth().toString()))
            .andExpect(jsonPath("$.gender").value(employee.getGender().name()))
            .andExpect(jsonPath("$.companyId").value(employee.getCompanyId()));
    }

    @Ignore
    @Test
    @Transactional
    @WithMockUser(username = "user", roles = "USER")
    public void getEmployeeAsUser() throws Exception {

        // arrange
        EmployeeDTO employee = employeeDomainService.save(getEmployeeSample());

        // act
        ResultActions resultActions = restMockEmployeeDomainResource
            .perform(get("/domain-api/employees/" + employee.getId()));

        // assert
        resultActions.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(employee.getId().intValue()))
            .andExpect(jsonPath("$.surname").value(employee.getSurname()))
            .andExpect(jsonPath("$.dateOfBirth").value(employee.getDateOfBirth().toString()))
            .andExpect(jsonPath("$.gender").value(employee.getGender().toString()))
            .andExpect(jsonPath("$.companyId").value(employee.getCompanyId()));
    }

    //------------------------------------------------------------------------------------------------------------------

    private EmployeeDTO getEmployeeSample() {
        return getEmployeeSample("Schmitt");
    }

    private EmployeeDTO getEmployeeSample(String surname) {
        EmployeeDTO employee = new EmployeeDTO();
        employee.setCompanyId(this.company.getId());
        employee.setSurname(surname);
        employee.setGivenName("Thomas");
        employee.setDateOfBirth(LocalDate.of(1970, Month.JANUARY, 1));
        employee.setGender(GenderType.MALE);
        employee.setCity("München");
        employee.setPostalCode("98765");
        employee.setStreetAddress("Am Wegesrand 123");
        return employee;
    }
}
