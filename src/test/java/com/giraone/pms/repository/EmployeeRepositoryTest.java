package com.giraone.pms.repository;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import com.giraone.pms.domain.EmployeeName;
import com.giraone.pms.domain.enumeration.EmployeeNameFilterKey;
import com.giraone.pms.domain.enumeration.GenderType;
import com.giraone.pms.service.NameNormalizeService;
import com.giraone.pms.service.impl.NameNormalizeServiceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class EmployeeRepositoryTest {

    private static final String TEST_SURNAME = "Schmitt";
    private static final String TEST_GIVEN_NAME = "Thomas";
    private static final LocalDate TEST_DATE_OF_BIRTH = LocalDate.of(1972, Month.JANUARY, 1);
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EmployeeNameRepository employeeNameRepository;

    private NameNormalizeService nameNormalizeService = new NameNormalizeServiceImpl();

    private Company company;

    @Before
    public void init() {

        Company company = new Company();
        company.setName("Test-Company");
        company.setExternalId("test1");
        this.company = companyRepository.save(company);
        testEntityManager.flush();
    }


    @Before
    public void clearContent() {

        employeeRepository.deleteAll();
        companyRepository.deleteAll();
        testEntityManager.flush();
    }

    @Test
    public void saveNewEmployee_succeeds() {

        // arrange
        Employee employee = getEmployeeSample(TEST_SURNAME);

        // act
        employeeRepository.save(employee);

        // assert
        assertThat(employeeRepository.count()).isEqualTo(1);
        assertThat(employeeNameRepository.count()).isGreaterThan(1); // checks the redundant names

        List<EmployeeName> result = employeeNameRepository.findAll();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(6);
        result.forEach(employeeName -> {
            assertThat(employeeName.getId().getOwner().getId()).isEqualTo(employee.getId());
            assertThat(employeeName.getId().getNameKey()).isIn("SL", "SN", "SP", "GL", "GN", "GP");
            assertThat(employeeName.getId().getNameValue()).isIn(
                "schmitt", "smit", "XMT",
                "thomas", "tomas", "TMS");
        });
    }

    @Test
    public void whenSavingEntity_checkThatMultipleNamesAreStoredForMultipleNames() {

        // arrange
        Employee employee = getEmployeeSample("Schmidt-Wagner");

        // act
        employeeRepository.save(employee);

        // assert
        List<EmployeeName> result = employeeNameRepository.findAll();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(8);
        result.forEach(employeeName -> {
            assertThat(employeeName.getId().getOwner().getId()).isEqualTo(employee.getId());
            assertThat(employeeName.getId().getNameKey()).isIn("SL", "SN", "SP", "GL", "GN", "GP");
            assertThat(employeeName.getId().getNameValue()).isIn(
                "schmidt-wagner", "smit", "XMT", "wagner", "AKNR",
                "thomas", "tomas", "TMS"
            );
        });
    }

    @Test
    public void deleteEmployee_deletesNamesAlso() {

        // arrange
        Employee employee = getEmployeeSample();
        employee = employeeRepository.save(employee);

        // act
        employeeRepository.delete(employee);

        // assert
        assertThat(employeeRepository.count()).isEqualTo(0);
        assertThat(employeeNameRepository.count()).isEqualTo(0); // checks the redundant names
    }

    @Test
    public void deleteAllEmployees_deletesNamesAlso() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        employeeRepository.deleteAll();

        // assert
        assertThat(employeeRepository.count()).isEqualTo(0);
        assertThat(employeeNameRepository.count()).isEqualTo(0); // checks the redundant names
    }


    @Test
    public void findAllBySurname_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllBySurname(
            nameNormalizeService.normalize(TEST_SURNAME), PageRequest.of(0, 10));

        // assert
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllBySurnameAndGivenName_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllBySurnameAndGivenName(
            nameNormalizeService.normalize(TEST_SURNAME), nameNormalizeService.normalize(TEST_GIVEN_NAME),
            PageRequest.of(0, 10));

        // assert
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllBySurnameAndDateOfBirth_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllBySurnameAndDateOfBirth(
            nameNormalizeService.normalize(TEST_SURNAME), TEST_DATE_OF_BIRTH,
            PageRequest.of(0, 10));

        // assert
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllBySurnameAndGivenNameAndDateOfBirth_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllBySurnameAndGivenNameAndDateOfBirth(
            nameNormalizeService.normalize(TEST_SURNAME), nameNormalizeService.normalize(TEST_GIVEN_NAME), TEST_DATE_OF_BIRTH,
            PageRequest.of(0, 10));

        // assert
        assertThat(page.getTotalElements()).isEqualTo(1);
    }


    @Test
    public void findAllByCompany_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllByCompany(
            this.company, PageRequest.of(0, 10));

        // assert
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllByCompanyAndSurname_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllByCompanyAndSurname(
            this.company, nameNormalizeService.normalize(TEST_SURNAME), PageRequest.of(0, 10));

        // assert
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllByCompanyAndSurnameAndDateOfBirth_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllByCompanyAndSurnameAndDateOfBirth(
            this.company, nameNormalizeService.normalize(TEST_SURNAME), TEST_DATE_OF_BIRTH, PageRequest.of(0, 10));

        // assert
        assertThat(page.getTotalElements()).isEqualTo(1);
    }


    @Test
    public void findAllByKeyPairLike_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllByKeyPairLike(
            EmployeeNameFilterKey.SL.name(), "schmi%", PageRequest.of(0, 10));

        // assert
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllByDateOfBirthAndKeyPairLike_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllByDateOfBirthAndKeyPairLike(
            TEST_DATE_OF_BIRTH, EmployeeNameFilterKey.SL.name(), "schmi%", PageRequest.of(0, 10));

        // assert
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllByCompanyAndKeyPairLike_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllByCompanyAndKeyPairLike(
            this.company, EmployeeNameFilterKey.SL.name(), "schmi%", PageRequest.of(0, 10));

        // assert
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllByCompanyAndDateOfBirthAndKeyPairLike_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllByCompanyAndDateOfBirthAndKeyPairLike(
            this.company,  TEST_DATE_OF_BIRTH,
            EmployeeNameFilterKey.SL.name(), "schmi%", PageRequest.of(0, 10));

        // assert
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    //------------------------------------------------------------------------------------------------------------------

    private Employee getEmployeeSample() {
        return getEmployeeSample(TEST_SURNAME);
    }

    private Employee getEmployeeSample(String surname) {
        Employee employee = new Employee();
        employee.setCompany(this.company);
        employee.setSurname(surname);
        employee.setGivenName(TEST_GIVEN_NAME);
        employee.setDateOfBirth(TEST_DATE_OF_BIRTH);
        employee.setGender(GenderType.MALE);
        employee.setCity("München");
        employee.setPostalCode("98765");
        employee.setStreetAddress("Am Wegesrand 123");
        return employee;
    }
}
