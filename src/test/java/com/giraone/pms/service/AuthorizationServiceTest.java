package com.giraone.pms.service;

import com.giraone.pms.PmssqlApp;
import com.giraone.pms.service.dto.CompanyDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PmssqlApp.class)
@Transactional
@ActiveProfiles("test")
@WithMockUser(username = "user")
public class AuthorizationServiceTest {

    @Autowired
    AuthorizationService authorizationService;
    @Autowired
    CompanyService companyService;

    @Test
    public void testWhether_check_companyExternalId_user_isWorking() {

        // arrange
        String companyExternalId = "l-00000060";
        String userLogin = "user";
        CompanyDTO company = new CompanyDTO();
        company.setExternalId(companyExternalId);
        companyService.save(company);
        companyService.addUserToCompany(companyExternalId, userLogin);

        // act
        boolean result = authorizationService.check(companyExternalId, userLogin);

        // assert
        assertTrue(result);
    }

    @Test
    public void testWhether_check_companyId_user_isWorking() {

        // arrange
        String companyExternalId = "l-00000060";
        String userLogin = "user";
        CompanyDTO company = new CompanyDTO();
        company.setExternalId(companyExternalId);
        company = companyService.save(company);
        companyService.addUserToCompany(companyExternalId, userLogin);

        // act
        boolean result = authorizationService.check(company.getId(), userLogin);

        // assert
        assertTrue(result);
    }

    @Test
    public void testWhether_isAdmin_isWorking_forUser() {

        // arrange

        // act
        boolean result = authorizationService.isAdmin();

        // assert
        assertFalse(result);
    }

    @Test
    @WithMockUser(username = "admin", roles={"ADMIN"})
    public void testWhether_isAdmin_isWorking_forAdmin() {

        // arrange

        // act
        boolean result = authorizationService.isAdmin();

        // assert
        assertTrue(result);
    }

    @Test
    public void testWhetherSpElIsWorkingBasically() {

        // arrange
        // letting SPEL know about the context
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("companyExternalId", "l-00000060");
        ExpressionParser parser = new SpelExpressionParser();

        // act
        // using '#' to identify a variable ( NOTE: #this, #root are reserved variables )
        Expression exp = parser.parseExpression("#companyExternalId");
        String companyExternalId = exp.getValue(context, String.class);

        // assert
        assertEquals("l-00000060", companyExternalId);
    }

    @Test
    public void testWhetherSpElMethodIsWorking() {

        // arrange
        String companyExternalId = "l-00000060";
        String userLogin = "user";
        CompanyDTO company = new CompanyDTO();
        company.setExternalId(companyExternalId);
        companyService.save(company);
        companyService.addUserToCompany(companyExternalId, userLogin);

        // act
        // letting SPEL know about the context
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("companyExternalId", companyExternalId);
        context.setVariable("authorizationService", authorizationService);
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("#authorizationService.check(#companyExternalId, 'user')");
        Boolean result = exp.getValue(context, Boolean.class);

        // assert
        assertNotNull(result);
        assertTrue(result);
    }
}
