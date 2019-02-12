package com.giraone.pms.repository;

import com.giraone.pms.domain.Company;
import com.giraone.pms.domain.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class CompanyRepositoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void clearContent() {

        companyRepository.deleteAll();
        testEntityManager.flush();
    }

    @Test
    public void saveNewCompany_succeeds() {

        // arrange
        Company company = new Company();
        company.setName("Test-Company");
        company.setExternalId("test1");

        // act
        companyRepository.save(company);

        // assert
        assertThat(companyRepository.count()).isEqualTo(1);
    }

    @Ignore // TODO
    @Test
    public void save_existingCompany_throwsException() {

        // arrange
        Company company1 = this.saveCompany("test1");

        expectedException.expect(DataIntegrityViolationException.class);
        expectedException.expectMessage("could not execute statement; SQL [n/a]; constraint");

        // act
        Company company2 = this.saveCompany("test1"); // same external ID, where there is a unique constraint

        companyRepository.save(company2);

        // assert
    }

    @Test
    public void savedCompany_isFoundByExternalId() {

        // arrange
        Company company = saveCompany("test1");

        // act
        Optional<Company> result = companyRepository.findOneByExternalId("test1");

        // assert
        assertTrue(result.isPresent());
        assertThat(result.get().getId()).isEqualTo(company.getId());
        assertThat(result.get().getExternalId()).isEqualTo(company.getExternalId());
    }

    @Test
    public void savedCompany_isFoundByExternalIdAndUsersLogin() {

        // arrange
        Company company = saveCompanyWithUser();

        // act
        Optional<Company> result = companyRepository.findOneByExternalIdAndUsersLogin("test1", "user");

        // assert
        assertTrue(result.isPresent());
        assertThat(result.get().getId()).isEqualTo(company.getId());
        assertThat(result.get().getExternalId()).isEqualTo(company.getExternalId());
    }

    @Test
    public void savedCompany_isFoundByIdAndUsersLogin() {

        // arrange
        Company company = saveCompanyWithUser();

        // act
        Optional<Company> result = companyRepository.findOneByIdAndUsersLogin(company.getId(), "user");

        // assert
        assertTrue(result.isPresent());
        assertThat(result.get().getId()).isEqualTo(company.getId());
        assertThat(result.get().getExternalId()).isEqualTo(company.getExternalId());
    }

    @Test
    public void savedCompany_findUsersByCompanyId() {

        // arrange
        Company company = saveCompanyWithUser();

        // act
        Page<User> result = companyRepository.findUsersOfCompanyByCompanyId(company.getId(), PageRequest.of(0, 10));

        // assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getLogin()).isEqualTo("user");
    }

    @Test
    public void savedCompany_findUsersByCompanyExternalId() {

        // arrange
        Company company = saveCompanyWithUser();

        // act
        Page<User> result = companyRepository.findUsersOfCompanyByCompanyExternalId(company.getExternalId(), PageRequest.of(0, 10));

        // assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getLogin()).isEqualTo("user");
    }

    //------------------------------------------------------------------------------------------------------------------

    private Company saveCompany(String externalId) {
        Company company = new Company();
        company.setName("Test-Company " + externalId);
        company.setExternalId(externalId);
        company = companyRepository.save(company);
        return company;
    }

    private Company saveCompanyWithUser() {
        Company company = new Company();
        company.setName("Test-Company");
        company.setExternalId("test1");
        Set<User> users = new HashSet<>();
        Optional<User> user = userRepository.findOneByLogin("user");
        assertTrue(user.isPresent());
        users.add(user.get());
        company.setUsers(users);
        company = companyRepository.save(company);
        return company;
    }
}
