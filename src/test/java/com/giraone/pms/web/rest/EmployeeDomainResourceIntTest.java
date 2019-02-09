package com.giraone.pms.web.rest;

import com.giraone.pms.PmssqlApp;
import com.giraone.pms.domain.enumeration.GenderType;
import com.giraone.pms.service.CompanyService;
import com.giraone.pms.service.EmployeeService;
import com.giraone.pms.service.UserService;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import com.giraone.pms.service.dto.UserDTO;
import org.junit.Before;
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
import static org.junit.Assert.assertTrue;
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

    private static final String TEST_USER = "test-user-1";
    private static final String TEST_COMPANY = "test-company-1";

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc restMockEmployeeDomainResource;

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
        CompanyDTO company = setupCompanyAndUser();
        EmployeeDTO employee = employeeService.save(getEmployeeSample(company));

        // act
        ResultActions resultActions = restMockEmployeeDomainResource
            .perform(
                get("/domain-api/employees")
                    .param("sort", "id,desc"));

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
    @WithMockUser(username = TEST_USER, roles = "USER")
    public void getAllEmployeesAsUser() throws Exception {

        // arrange
        CompanyDTO company = setupCompanyAndUser();
        EmployeeDTO employee = employeeService.save(getEmployeeSample(company));

        // act
        ResultActions resultActions = restMockEmployeeDomainResource
            .perform(
                get("/domain-api/employees")
                    .param("companyExternalId", company.getExternalId())
                    .param("sort", "id,desc"));

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
        CompanyDTO company = setupCompanyAndUser();
        EmployeeDTO employee = employeeService.save(getEmployeeSample(company));

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

    @Test
    @Transactional
    @WithMockUser(username = TEST_USER, roles = "USER")
    public void getEmployeeAsUser() throws Exception {

        // arrange
        CompanyDTO company = setupCompanyAndUser();
        EmployeeDTO employee = employeeService.save(getEmployeeSample(company));

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

    private EmployeeDTO getEmployeeSample(CompanyDTO company) {
        EmployeeDTO employee = new EmployeeDTO();
        employee.setCompanyId(company.getId());
        employee.setSurname("Schmitt");
        employee.setGivenName("Thomas");
        employee.setDateOfBirth(LocalDate.of(1970, Month.JANUARY, 1));
        employee.setGender(GenderType.MALE);
        employee.setCity("München");
        employee.setPostalCode("98765");
        employee.setStreetAddress("Am Wegesrand 123");
        return employee;
    }

    private CompanyDTO setupCompanyAndUser() {

        UserDTO user = new UserDTO();
        user.setLogin(TEST_USER);
        userService.createUser(user);

        CompanyDTO company = new CompanyDTO();
        company.setName(TEST_COMPANY);
        company.setExternalId(TEST_COMPANY);
        company = companyService.save(company);

        boolean ok = companyService.addUserToCompany(company.getExternalId(), user.getLogin());
        assertTrue(ok);
        return company;
    }
}
