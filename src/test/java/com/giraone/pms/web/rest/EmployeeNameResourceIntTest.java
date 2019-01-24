package com.giraone.pms.web.rest;

import com.giraone.pms.PmssqlApp;
import com.giraone.pms.repository.EmployeeNameRepository;
import com.giraone.pms.service.EmployeeNameService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test class for the EmployeeNameResource REST controller.
 *
 * @see EmployeeNameResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PmssqlApp.class)
public class EmployeeNameResourceIntTest {

    private static final Long DEFAULT_OWNER_ID = 1L;
    private static final Long UPDATED_OWNER_ID = 2L;

    private static final String DEFAULT_NAME_KEY = "AAAAAAAAAA";
    private static final String UPDATED_NAME_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_NAME_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_NAME_VALUE = "BBBBBBBBBB";

    @Autowired
    private EmployeeNameRepository employeeNameRepository;

    @Autowired
    private EmployeeNameService employeeNameService;

    // T.B.D.
}
