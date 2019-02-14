package com.giraone.pms.service;

import com.giraone.pms.PmssqlApp;
import com.giraone.pms.domain.enumeration.GenderType;
import com.giraone.pms.domain.filter.PersonFilter;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Test class for the {@link EmployeeService} which tests the extensions, that are not
 * basic CRUD operations from JHipster,
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PmssqlApp.class)
@Transactional
@ActiveProfiles("test")
@WithMockUser(username = "admin", roles={"ADMIN"})
public class EmployeeServiceExtensionsIntTest {

    private static final String COMPANY_ID = "test1";

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void whenSavingEntity_checkThatSavedAttributesAreReturned() {

        // arrange
        EmployeeDTO employee = getEmployeeSample();

        // act
        EmployeeDTO savedEmployee = employeeService.save(employee);

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
    public void whenSavingEntity_checkThatFindByIdWorks() {

        // arrange
        final EmployeeDTO employee = employeeService.save(getEmployeeSample());

        // act
        Optional<EmployeeDTO> savedEmployee = employeeService.findOne(employee.getId());

        // assert
        assertTrue(savedEmployee.isPresent());
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
    public void findAllByFilter_checkThatNullFilterWorks() {

        // arrange

        // act
        Pageable pageable = PageRequest.of(0, 10);
        Optional<Page<EmployeeDTO>> page = employeeService.findAllByFilter(null, null, pageable);

        // assert
        assertTrue(page.isPresent());
    }

    @Test
    public void findAllByFilter_checkThatCompanyFilterWorks() {

        // arrange
        employeeService.save(getEmployeeSample());

        // act
        Pageable pageable = PageRequest.of(0, 10);
        Optional<Page<EmployeeDTO>> page = employeeService.findAllByFilter(COMPANY_ID, null, pageable);

        // assert
        assertTrue(page.isPresent());
        assertThat(page.get().getContent().get(0).getCompanyId()).isEqualTo(COMPANY_ID);
    }

    @Test
    public void findAllByFilter_checkThatPagingWorks() {

        // arrange
        CompanyDTO company = saveCompanySample();
        for (int i = 0; i < 20; i++) {
            employeeService.save(getEmployeeSample(company));
        }

        // act
        PersonFilter personFilter = new PersonFilter("");
        Pageable pageable = PageRequest.of(0, 10);
        Optional<Page<EmployeeDTO>> page = employeeService.findAllByFilter(company.getExternalId(), personFilter, pageable);

        // assert
        assertTrue(page.isPresent());
        assertThat(page.get().getContent().size()).isEqualTo(10);
        assertThat(page.get().getTotalElements()).isEqualTo(20);
        assertThat(page.get().getTotalPages()).isEqualTo(2);
    }

    @Test
    public void whenSavingEntity_checkThatItIsFoundByDateOfBirth_WITHOUT_COMPANY() {
        storeThenQueryThenCheckDate(false, new LocalDate[]{ LocalDate.of(1977, Month.DECEMBER, 1)},
            "01.12.1977", LocalDate.of(1977, Month.DECEMBER, 1));
    }

    @Test
    public void whenSavingEntity_checkThatItIsFoundByDateOfBirth_WITH_COMPANY() {
        storeThenQueryThenCheckDate(true, new LocalDate[]{ LocalDate.of(1977, Month.DECEMBER, 1)},
            "01.12.1977", LocalDate.of(1977, Month.DECEMBER, 1));
    }

    @Test
    public void whenSavingEntity_checkThatItIsFoundBySurname_WITHOUT_COMPANY_REDUCED() {
        storeThenQueryThenCheckMatch(false, new String[]{"Schmidt", "Schmitt", "Schmand", "Schand"},
            "Schm", new String[]{"Schmidt", "Schmitt", "Schmand"});
    }

    @Test
    public void whenSavingEntity_checkThatItIsFoundBySurname_WITH_COMPANY_REDUCED() {
        storeThenQueryThenCheckMatch(true, new String[]{"Schmidt", "Schmitt", "Schmand", "Schand"},
            "Schm", new String[]{"Schmidt", "Schmitt", "Schmand"});
    }

    @Test
    public void whenSavingEntity_checkThatItIsFoundBySurname_WITHOUT_COMPANY_PHONETIC() {
        storeThenQueryThenCheckMatch(false, new String[]{"Scholten", "Schulten", "Gulten"},
            "Scholten", true, new String[]{"Scholten", "Schulten"});
    }

    @Test
    public void whenSavingEntity_checkThatItIsFoundBySurname_WITH_COMPANY_PHONETIC() {
        storeThenQueryThenCheckMatch(true, new String[]{"Scholten", "Schulten", "Gulten"},
            "Scholten", true, new String[]{"Scholten", "Schulten"});
    }

    @Test
    public void whenSavingEntityWithCombinedName_checkThatItIsFound_WITHOUT_COMPANY() {
        storeThenQueryThenCheckMatch(false, new String[]{"Schmidt-Huber", "Huber-Schmidt", "Huber", "Schmidt"},
            "Huber", new String[]{"Schmidt-Huber", "Huber-Schmidt", "Huber"});
    }

    @Test
    public void whenSavingEntityWithCombinedName_checkThatItIsFound_WITH_COMPANY() {
        storeThenQueryThenCheckMatch(true, new String[]{"Schmidt-Huber", "Huber-Schmidt", "Huber", "Schmidt"},
            "Schmidt", new String[]{"Schmidt-Huber", "Huber-Schmidt", "Schmidt"});
    }

    @Test
    public void whenSavingEntityWithCombinedName_checkThatItIsFound_WITHOUT_COMPANY_2() {
        storeThenQueryThenCheckMatch(false, new String[]{"Schmidt-Mayer", "Maier-Schmidt", "Maier", "Schmidt"},
            "Meier", new String[]{"Schmidt-Mayer", "Maier-Schmidt", "Maier"});
    }

    @Test
    public void whenSavingEntityWithCombinedName_checkThatItIsFound_WITH_COMPANY_2() {
        storeThenQueryThenCheckMatch(true, new String[]{"Schmidt-Mayer", "Mayer-Schmidt", "Mayer", "Schmied"},
            "Schmitt", new String[]{"Schmidt-Mayer", "Mayer-Schmidt", "Schmied"});
    }

    @Test
    public void whenSavingEntityWithSpecialCharacters_checkThatItIsFound() {
        storeThenQueryThenCheckMatch(true, new String[]{"Schmidt-Mayer"},
            "\"Schmidt-Mayer\"", new String[]{"Schmidt-Mayer"});
        storeThenQueryThenCheckMatch(true, new String[]{"Wagner-Mayer"},
            "\"Wagner-Mayer\"", new String[]{"Wagner-Mayer"});
        storeThenQueryThenCheckMatch(true, new String[]{"von der Tann"},
            "\"von der Tann\"", new String[]{"von der Tann"});
        storeThenQueryThenCheckMatch(true, new String[]{"von der Weide- Zaun"},
            "\"von der Weide-Zaun\"", new String[]{"von der Weide-Zaun"});
    }

    //------------------------------------------------------------------------------------------------------------------

    private void storeThenQueryThenCheckDate(boolean withCompany, LocalDate[] dates,
                                              String filter, LocalDate matchingDate) {

        // arrange
        CompanyDTO company = new CompanyDTO();
        company.setName("Test-Company");
        company.setExternalId(COMPANY_ID);
        company = companyService.save(company);

        for (LocalDate dateOfBirth : dates) {
            EmployeeDTO employee = new EmployeeDTO();
            employee.setCompanyId(company.getId());
            employee.setSurname("Date " + dateOfBirth);
            employee.setDateOfBirth(dateOfBirth);
            employeeService.save(employee);
        }

        // act
        String companyExternalId = withCompany ? company.getExternalId() : null;
        PersonFilter personFilter = new PersonFilter(filter, false);
        Pageable pageable = PageRequest.of(0, 10);
        Optional<Page<EmployeeDTO>> result = employeeService.findAllByFilter(
            companyExternalId, personFilter, pageable);

        // assert
        assertTrue(result.isPresent());
        assertThat(result.get().getTotalElements()).isGreaterThan(0);
        assertThat(result.get().getTotalPages()).isGreaterThan(0);

        System.out.println(String.format("%d employees found in query", result.get().getContent().size()));
        result.get().getContent().forEach(
            matchingEmployee -> System.out.println(" - " + matchingEmployee));
        result.get().getContent().forEach(
            matchingEmployee -> assertThat(matchingEmployee.getDateOfBirth()).isEqualTo(matchingDate));
    }

    private void storeThenQueryThenCheckMatch(boolean withCompany, String[] storedSurnames,
                                              String filter, String[] matchingSurnames) {
        storeThenQueryThenCheckMatch(withCompany, storedSurnames, filter, false, matchingSurnames);
    }

    private void storeThenQueryThenCheckMatch(boolean withCompany, String[] storedSurnames,
                                              String filter, boolean phonetic, String[] matchingSurnames) {

        // arrange
        CompanyDTO company = new CompanyDTO();
        company.setName("Test-Company");
        company.setExternalId(COMPANY_ID);
        company = companyService.save(company);
        for (String surname : storedSurnames) {
            EmployeeDTO employee = new EmployeeDTO();
            employee.setCompanyId(company.getId());
            employee.setSurname(surname);
            employeeService.save(employee);
        }

        // act
        String companyExternalId = withCompany ? company.getExternalId() : null;
        PersonFilter personFilter = new PersonFilter(filter, phonetic);
        Pageable pageable = PageRequest.of(0, 10);
        Optional<Page<EmployeeDTO>> result = employeeService.findAllByFilter(
            companyExternalId, personFilter, pageable);

        // assert
        assertTrue(result.isPresent());
        assertThat(result.get().getTotalElements()).isGreaterThan(0);
        assertThat(result.get().getTotalPages()).isGreaterThan(0);

        System.out.println(String.format("%d employees found in query", result.get().getContent().size()));
        result.get().getContent().forEach(
            matchingEmployee -> System.out.println(" - " + matchingEmployee));
        assertThat(result.get().getContent().size()).isEqualTo(matchingSurnames.length);
        result.get().getContent().forEach(
            matchingEmployee -> assertThat(matchingEmployee.getSurname()).isIn(Arrays.asList(matchingSurnames)));
    }

    private CompanyDTO saveCompanySample() {

        CompanyDTO company = new CompanyDTO();
        company.setName("Test-Company");
        company.setExternalId(COMPANY_ID);
        return companyService.save(company);
    }

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

    private EmployeeDTO getEmployeeSample() {
        CompanyDTO company = saveCompanySample();
        return getEmployeeSample(company);
    }
}
