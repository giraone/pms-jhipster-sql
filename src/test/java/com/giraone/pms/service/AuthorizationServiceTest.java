package com.giraone.pms.service;

import com.giraone.pms.PmssqlApp;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PmssqlApp.class)
public class AuthorizationServiceTest {

    @Autowired
    AuthorizationService authorizationService;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testWhetherSpElIsWorkingBasically() {

        // letting SPEL know about the context
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("companyExternalId", "l-00000060");

        ExpressionParser parser = new SpelExpressionParser();

        // using '#' to identify a variable ( NOTE: #this, #root are reserved variables )
        Expression exp = parser.parseExpression("#companyExternalId");

        String companyExternalId = exp.getValue(context, String.class);
        assertEquals("l-00000060", companyExternalId);
    }

    @Ignore
    @Test
    public void testWhetherSpElMethodIsWorking() {

        // companyExternalId=l-00000060, principal=user-00001304

        // letting SPEL know about the context
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("companyExternalId", "l-00000060");
        context.setVariable("authorizationService", authorizationService);

        ExpressionParser parser = new SpelExpressionParser();

        Expression exp = parser.parseExpression("#authorizationService.check(#companyExternalId, 'user-00001304')");

        Boolean result = exp.getValue(context, Boolean.class);
        assertTrue(result);
    }
}
