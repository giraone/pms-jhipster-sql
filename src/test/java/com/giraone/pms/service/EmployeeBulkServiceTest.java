package com.giraone.pms.service;

import com.giraone.pms.PmssqlApp;
import com.giraone.pms.domain.User;
import com.giraone.pms.domain.filter.PersonFilter;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeBulkDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the {@link EmployeeBulkService}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PmssqlApp.class)
@Transactional
@WithMockUser(username = "admin", roles={"ADMIN"})
public class EmployeeBulkServiceTest {

    private static final String TEST_COMPANY_EXTERNAL_ID = "test-0001";
    private static final String TEST_EMPLOYEE_SURNAME_PREFIX = "Test";

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeBulkService employeeBulkService;

    @Before
    public void init() {
    }

    @Test
    @Transactional
    public void whenSavingEntities_checkResult() {

        // arrange
        int count = 10;
        List<EmployeeBulkDTO> employees = getEmployeeSamples(count);

        // act
        int nrOfSavedEmployee = employeeBulkService.save(employees);

        // assert
        assertThat(nrOfSavedEmployee).isEqualTo(count);

        // does the company exist?
        Optional<CompanyDTO> company = companyService.findOneByExternalId(TEST_COMPANY_EXTERNAL_ID);
        assertTrue(company.isPresent());
        assertThat(company.get().getExternalId()).isEqualTo(TEST_COMPANY_EXTERNAL_ID);

        // does the company has an initial user?
        Page<User> users = this.companyService.findAllUserInCompany(TEST_COMPANY_EXTERNAL_ID, PageRequest.of(0, 10));
        assertThat(users.getTotalElements()).isEqualTo(1);

        // can the employees be found by a filter query?
        PersonFilter personFilter = new PersonFilter(TEST_EMPLOYEE_SURNAME_PREFIX, false);
        Pageable pageable = PageRequest.of(0, 10);
        Optional<Page<EmployeeDTO>> result = employeeService.findAllByFilter(
            TEST_COMPANY_EXTERNAL_ID, personFilter, pageable);
        assertTrue(result.isPresent());
        assertThat(result.get().getTotalElements()).isEqualTo(10);
    }

    //------------------------------------------------------------------------------------------------------------------

    private EmployeeBulkDTO getEmployeeSample(int i) {
        EmployeeBulkDTO employee = new EmployeeBulkDTO();
        employee.setCompanyId(TEST_COMPANY_EXTERNAL_ID);
        employee.setSurname(String.format("%s%08d", TEST_EMPLOYEE_SURNAME_PREFIX, i));
        employee.setGivenName("Peter");
        employee.setDateOfBirth("19780913");
        employee.setGender("M");
        employee.setCity("München");
        employee.setPostalCode("98765");
        employee.setStreetAddress("Am Wegesrand 123");
        return employee;
    }

    private List<EmployeeBulkDTO> getEmployeeSamples(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(this::getEmployeeSample).collect(Collectors.toList());
    }
}
