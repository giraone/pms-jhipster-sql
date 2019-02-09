package com.giraone.pms.repository;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.Employee;
import com.giraone.pms.domain.EmployeeName;
import com.giraone.pms.domain.enumeration.EmployeeNameFilterKey;
import com.giraone.pms.domain.enumeration.GenderType;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EmployeeRepositoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeNameRepository employeeNameRepository;

    private Company company;

    @Before
    public void init() {

        Company company = new Company();
        company.setName("Test-Company");
        company.setExternalId("test1");
        this.company = testEntityManager.persistAndFlush(company);
    }


    @Before
    public void clearContent() {

        employeeRepository.deleteAll();
    }

    @Test
    public void saveNewEmployee_succeeds() {

        // arrange
        Employee employee = getEmployeeSample("Schmitt");

        // act
        employeeRepository.save(employee);

        // assert
        assertThat(employeeRepository.count()).isEqualTo(1);
        assertThat(employeeNameRepository.count()).isGreaterThan(1); // checks the redundant names

        List<EmployeeName> result = employeeNameRepository.findAll();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
        result.forEach(employeeName -> {
            assertThat(employeeName.getId().getOwner().getId()).isEqualTo(employee.getId());
            assertThat(employeeName.getId().getNameKey()).isIn("SL", "SN", "SP");
            assertThat(employeeName.getId().getNameValue()).isIn("schmitt", "smit", "XMT");
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
        assertThat(result.size()).isEqualTo(5);
        result.forEach(employeeName -> {
            assertThat(employeeName.getId().getOwner().getId()).isEqualTo(employee.getId());
            assertThat(employeeName.getId().getNameKey()).isIn("SL", "SN", "SP");
            assertThat(employeeName.getId().getNameValue()).isIn("schmidt-wagner", "smit", "XMT", "wagner", "AKNR");
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
    public void findAllBySurnameLike_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllBySurnameLike(
            "Schmi%", PageRequest.of(0, 10));

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
    public void findAllByCompanyAndSurnameLike_withExistingEmployee_returnsEmployee() {

        // arrange
        Employee employee = getEmployeeSample();
        employeeRepository.save(employee);

        // act
        Page<Employee> page = employeeRepository.findAllByCompanyAndSurnameLike(
            this.company, "Schmi%", PageRequest.of(0, 10));

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

    //------------------------------------------------------------------------------------------------------------------

    private Employee getEmployeeSample() {
        return getEmployeeSample("Schmitt");
    }

    private Employee getEmployeeSample(String surname) {
        Employee employee = new Employee();
        employee.setCompany(this.company);
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
