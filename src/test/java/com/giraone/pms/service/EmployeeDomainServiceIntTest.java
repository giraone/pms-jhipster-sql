package com.giraone.pms.service;

import com.giraone.pms.PmssqlApp;
import com.giraone.pms.domain.enumeration.GenderType;
import com.giraone.pms.domain.enumeration.StringSearchMode;
import com.giraone.pms.domain.filter.EmployeeFilter;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import com.giraone.pms.service.dto.EmployeeNameDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the EmployeeDomainService
 *
 * @see EmployeeDomainService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PmssqlApp.class)
@Transactional
public class EmployeeDomainServiceIntTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployeeDomainService employeeDomainService;

    @Autowired
    private EmployeeNameService employeeNameService;

    private CompanyDTO company;

    @Before
    public void init() {

        CompanyDTO company = new CompanyDTO();
        company.setName("Test-Company");
        company.setExternalId("test1");
        this.company = companyService.save(company);
    }

    @Test
    @Transactional
    public void whenSavingEntity_checkThatSavedAttributesAreReturned() {

        // arrange
        EmployeeDTO employee = getEmployeeSample();

        // act
        EmployeeDTO savedEmployee = employeeDomainService.save(employee);

        // assert
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getCompanyId()).isEqualTo(employee.getCompanyId());
        assertThat(savedEmployee.getSurname()).isEqualTo(employee.getSurname());
        assertThat(savedEmployee.getGivenName()).isEqualTo(employee.getGivenName());
        assertThat(savedEmployee.getDateOfBirth()).isEqualTo(employee.getDateOfBirth());
        assertThat(savedEmployee.getGender()).isEqualTo(employee.getGender());
        assertThat(savedEmployee.getCity()).isEqualTo(employee.getCity());
        assertThat(savedEmployee.getPostalCode()).isEqualTo(employee.getPostalCode());
        assertThat(savedEmployee.getStreetAddress()).isEqualTo(employee.getStreetAddress());
    }

    @Test
    @Transactional
    public void whenSavingEntity_checkThatFindByIdWorks() {

        // arrange
        final EmployeeDTO employee = employeeDomainService.save(getEmployeeSample());

        // act
        Optional<EmployeeDTO> savedEmployee = employeeDomainService.findOne(employee.getId());

        // assert
        assertThat(savedEmployee).isPresent();
        assertThat(savedEmployee.get().getCompanyId()).isEqualTo(employee.getCompanyId());
        assertThat(savedEmployee.get().getSurname()).isEqualTo(employee.getSurname());
        assertThat(savedEmployee.get().getGivenName()).isEqualTo(employee.getGivenName());
        assertThat(savedEmployee.get().getDateOfBirth()).isEqualTo(employee.getDateOfBirth());
        assertThat(savedEmployee.get().getGender()).isEqualTo(employee.getGender());
        assertThat(savedEmployee.get().getCity()).isEqualTo(employee.getCity());
        assertThat(savedEmployee.get().getPostalCode()).isEqualTo(employee.getPostalCode());
        assertThat(savedEmployee.get().getStreetAddress()).isEqualTo(employee.getStreetAddress());
    }

    @Test
    @Transactional
    public void whenSavingEntity_checkThatThreeNamesAreStoredForSingleName() {

        // arrange

        // act
        final EmployeeDTO employee = employeeDomainService.save(getEmployeeSample());
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmployeeNameDTO> result = employeeNameService.findAll(pageable);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getContent().size()).isEqualTo(3);
        result.getContent().forEach(keyValue -> {
            assertThat(keyValue.getOwnerId()).isEqualTo(employee.getId());
            assertThat(keyValue.getNameKey()).isIn("SL", "SN", "SP");
            assertThat(keyValue.getNameValue()).isIn("schmitt", "smit", "XMT");
        });
    }

    @Test
    @Transactional
    public void whenSavingEntity_checkThatMultipleNamesAreStoredForMultipleNames() {

        // arrange

        // act
        final EmployeeDTO employee = employeeDomainService.save(getEmployeeSample("Schmidt-Wagner"));
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmployeeNameDTO> result = employeeNameService.findAll(pageable);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getContent().size()).isEqualTo(5);
        result.getContent().forEach(keyValue -> {
            System.out.println(keyValue.getNameValue());
            assertThat(keyValue.getOwnerId()).isEqualTo(employee.getId());
            assertThat(keyValue.getNameKey()).isIn("SL", "SN", "SP");
            assertThat(keyValue.getNameValue()).isIn("schmidt-wagner", "smit", "XMT", "wagner", "AKNR");
        });
    }

    @Test
    @Transactional
    public void whenSavingEntity_checkThatItIsFoundBySurname_EXACT() {
        storeThenQueryThenCheckMatch(false, StringSearchMode.EXACT, new String[]{"Schmidt", "Schmitt"},
            "Schmidt", new String[]{"Schmidt"});
        storeThenQueryThenCheckMatch(true, StringSearchMode.EXACT, new String[]{"Schmidt", "Schmitt"},
            "Schmidt", new String[]{"Schmidt"});
    }

    @Test
    @Transactional
    public void whenSavingEntity_checkThatItIsFoundBySurname_PREFIX() {
        storeThenQueryThenCheckMatch(false, StringSearchMode.PREFIX, new String[]{"Schmidt", "Schulz", "Smith"},
            "Sch", new String[]{"Schmidt", "Schulz"});
        storeThenQueryThenCheckMatch(true, StringSearchMode.PREFIX, new String[]{"Schmidt", "Schulz", "Smith"},
            "Sch", new String[]{"Schmidt", "Schulz"});
    }

    @Test
    @Transactional
    public void whenSavingEntity_checkThatItIsFoundBySurname_LOWERCASE() {
        storeThenQueryThenCheckMatch(false, StringSearchMode.LOWERCASE, new String[]{"Schmidt", "schmidt", "schmud"},
            "Schmidt", new String[]{"Schmidt", "schmidt"});
        storeThenQueryThenCheckMatch(true, StringSearchMode.LOWERCASE, new String[]{"Schmidt", "schmidt", "schmud"},
            "Schmidt", new String[]{"Schmidt", "schmidt"});
    }

    @Test
    @Transactional
    public void whenSavingEntity_checkThatItIsFoundBySurname_PREFIX_LOWERCASE() {
        storeThenQueryThenCheckMatch(false, StringSearchMode.PREFIX_LOWERCASE, new String[]{"Schmidt", "schmidt", "schmitt", "Schand"},
            "Schmi", new String[]{"Schmidt", "schmidt", "schmitt"});
        storeThenQueryThenCheckMatch(true, StringSearchMode.PREFIX_LOWERCASE, new String[]{"Schmidt", "schmidt", "schmitt", "Schand"},
            "Schmi", new String[]{"Schmidt", "schmidt", "schmitt"});
    }

    @Test
    @Transactional
    public void whenSavingEntity_checkThatItIsFoundBySurname_NORMALIZED() {
        storeThenQueryThenCheckMatch(false, StringSearchMode.REDUCED, new String[]{"Schmidt", "Schmitt", "Schmudt"},
            "Schmidt", new String[]{"Schmidt", "Schmitt"});
        storeThenQueryThenCheckMatch(true, StringSearchMode.REDUCED, new String[]{"Schmidt", "Schmitt", "Schmudt"},
            "Schmidt", new String[]{"Schmidt", "Schmitt"});
    }

    @Test
    @Transactional
    public void whenSavingEntity_checkThatItIsFoundBySurname_PREFIX_NORMALIZED() {
        storeThenQueryThenCheckMatch(false, StringSearchMode.PREFIX_REDUCED, new String[]{"Schmidt", "Schmitt", "Schmand", "Schand"},
            "Schm", new String[]{"Schmidt", "Schmitt", "Schmand"});
        storeThenQueryThenCheckMatch(true, StringSearchMode.PREFIX_REDUCED, new String[]{"Schmidt", "Schmitt", "Schmand", "Schand"},
            "Schm", new String[]{"Schmidt", "Schmitt", "Schmand"});
    }

    @Test
    @Transactional
    public void whenSavingEntity_checkThatItIsFoundBySurname_PHONETIC() {
        storeThenQueryThenCheckMatch(false, StringSearchMode.PHONETIC, new String[]{"Scholten", "Schulten", "Gulten"},
            "Scholten", new String[]{"Scholten", "Schulten"});
        storeThenQueryThenCheckMatch(true, StringSearchMode.PHONETIC, new String[]{"Scholten", "Schulten", "Gulten"},
            "Scholten", new String[]{"Scholten", "Schulten"});
    }

    @Test
    @Transactional
    public void whenSavingEntity_checkThatItIsFoundBySurname_LOWERCASE_MULTIPLE() {
        storeThenQueryThenCheckMatch(false, StringSearchMode.LOWERCASE, new String[]{"Schmidt-Huber", "Schmidt", "Huber", "Haber"},
            "Huber", new String[]{"Schmidt-Huber", "Huber"});
        storeThenQueryThenCheckMatch(true, StringSearchMode.LOWERCASE, new String[]{"Schmidt-Huber", "Schmidt", "Huber", "Haber"},
            "Huber", new String[]{"Schmidt-Huber", "Huber"});
    }

    //------------------------------------------------------------------------------------------------------------------

    private void storeThenQueryThenCheckMatch(boolean withCompany, StringSearchMode stringSearchMode,
                                              String[] storedSurnames,
                                              String queriedSurname, String[] matchingSurnames) {

        // arrange
        for (String surname : storedSurnames) {
            EmployeeDTO employee = new EmployeeDTO();
            employee.setCompanyId(this.company.getId());
            employee.setSurname(surname);
            employeeDomainService.save(employee);
        }

        // act
        Optional<String> companyExternalId = withCompany ? Optional.of(company.getExternalId()) : Optional.empty();
        EmployeeFilter employeeFilter = new EmployeeFilter(
            Optional.of(queriedSurname), stringSearchMode, Optional.empty());
        Pageable pageable = PageRequest.of(0, 10);
        Optional<Page<EmployeeDTO>> result = employeeDomainService.findAll(companyExternalId, employeeFilter, pageable);

        // assert
        assertThat(result).isPresent();
        assertThat(result.get().getTotalElements()).isGreaterThan(0);
        assertThat(result.get().getTotalPages()).isGreaterThan(0);
        assertThat(result.get().getContent()).isNotEmpty();
        System.out.println(String.format("%d employees found in %s query", result.get().getContent().size(), stringSearchMode));
        result.get().getContent().forEach(matchingEmployee -> {
            assertThat(matchingEmployee.getSurname()).isIn(matchingSurnames);
        });
    }

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
