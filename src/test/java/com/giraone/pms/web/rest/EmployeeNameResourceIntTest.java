package com.giraone.pms.web.rest;

import com.giraone.pms.PmssqlApp;

import com.giraone.pms.domain.EmployeeName;
import com.giraone.pms.repository.EmployeeNameRepository;
import com.giraone.pms.service.EmployeeNameService;
import com.giraone.pms.service.dto.EmployeeNameDTO;
import com.giraone.pms.service.mapper.EmployeeNameMapper;
import com.giraone.pms.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;


import static com.giraone.pms.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the EmployeeNameResource REST controller.
 *
 * @see EmployeeNameResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PmssqlApp.class)
public class EmployeeNameResourceIntTest {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    @Autowired
    private EmployeeNameRepository employeeNameRepository;

    @Autowired
    private EmployeeNameMapper employeeNameMapper;

    @Autowired
    private EmployeeNameService employeeNameService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restEmployeeNameMockMvc;

    private EmployeeName employeeName;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EmployeeNameResource employeeNameResource = new EmployeeNameResource(employeeNameService);
        this.restEmployeeNameMockMvc = MockMvcBuilders.standaloneSetup(employeeNameResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EmployeeName createEntity(EntityManager em) {
        EmployeeName employeeName = new EmployeeName()
            .key(DEFAULT_KEY)
            .value(DEFAULT_VALUE);
        return employeeName;
    }

    @Before
    public void initTest() {
        employeeName = createEntity(em);
    }

    @Test
    @Transactional
    public void createEmployeeName() throws Exception {
        int databaseSizeBeforeCreate = employeeNameRepository.findAll().size();

        // Create the EmployeeName
        EmployeeNameDTO employeeNameDTO = employeeNameMapper.toDto(employeeName);
        restEmployeeNameMockMvc.perform(post("/api/employee-names")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeNameDTO)))
            .andExpect(status().isCreated());

        // Validate the EmployeeName in the database
        List<EmployeeName> employeeNameList = employeeNameRepository.findAll();
        assertThat(employeeNameList).hasSize(databaseSizeBeforeCreate + 1);
        EmployeeName testEmployeeName = employeeNameList.get(employeeNameList.size() - 1);
        assertThat(testEmployeeName.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testEmployeeName.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createEmployeeNameWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = employeeNameRepository.findAll().size();

        // Create the EmployeeName with an existing ID
        employeeName.setId(1L);
        EmployeeNameDTO employeeNameDTO = employeeNameMapper.toDto(employeeName);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeeNameMockMvc.perform(post("/api/employee-names")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeNameDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EmployeeName in the database
        List<EmployeeName> employeeNameList = employeeNameRepository.findAll();
        assertThat(employeeNameList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeeNameRepository.findAll().size();
        // set the field null
        employeeName.setKey(null);

        // Create the EmployeeName, which fails.
        EmployeeNameDTO employeeNameDTO = employeeNameMapper.toDto(employeeName);

        restEmployeeNameMockMvc.perform(post("/api/employee-names")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeNameDTO)))
            .andExpect(status().isBadRequest());

        List<EmployeeName> employeeNameList = employeeNameRepository.findAll();
        assertThat(employeeNameList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeeNameRepository.findAll().size();
        // set the field null
        employeeName.setValue(null);

        // Create the EmployeeName, which fails.
        EmployeeNameDTO employeeNameDTO = employeeNameMapper.toDto(employeeName);

        restEmployeeNameMockMvc.perform(post("/api/employee-names")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeNameDTO)))
            .andExpect(status().isBadRequest());

        List<EmployeeName> employeeNameList = employeeNameRepository.findAll();
        assertThat(employeeNameList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEmployeeNames() throws Exception {
        // Initialize the database
        employeeNameRepository.saveAndFlush(employeeName);

        // Get all the employeeNameList
        restEmployeeNameMockMvc.perform(get("/api/employee-names?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employeeName.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())));
    }
    
    @Test
    @Transactional
    public void getEmployeeName() throws Exception {
        // Initialize the database
        employeeNameRepository.saveAndFlush(employeeName);

        // Get the employeeName
        restEmployeeNameMockMvc.perform(get("/api/employee-names/{id}", employeeName.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(employeeName.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEmployeeName() throws Exception {
        // Get the employeeName
        restEmployeeNameMockMvc.perform(get("/api/employee-names/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEmployeeName() throws Exception {
        // Initialize the database
        employeeNameRepository.saveAndFlush(employeeName);

        int databaseSizeBeforeUpdate = employeeNameRepository.findAll().size();

        // Update the employeeName
        EmployeeName updatedEmployeeName = employeeNameRepository.findById(employeeName.getId()).get();
        // Disconnect from session so that the updates on updatedEmployeeName are not directly saved in db
        em.detach(updatedEmployeeName);
        updatedEmployeeName
            .key(UPDATED_KEY)
            .value(UPDATED_VALUE);
        EmployeeNameDTO employeeNameDTO = employeeNameMapper.toDto(updatedEmployeeName);

        restEmployeeNameMockMvc.perform(put("/api/employee-names")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeNameDTO)))
            .andExpect(status().isOk());

        // Validate the EmployeeName in the database
        List<EmployeeName> employeeNameList = employeeNameRepository.findAll();
        assertThat(employeeNameList).hasSize(databaseSizeBeforeUpdate);
        EmployeeName testEmployeeName = employeeNameList.get(employeeNameList.size() - 1);
        assertThat(testEmployeeName.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testEmployeeName.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void updateNonExistingEmployeeName() throws Exception {
        int databaseSizeBeforeUpdate = employeeNameRepository.findAll().size();

        // Create the EmployeeName
        EmployeeNameDTO employeeNameDTO = employeeNameMapper.toDto(employeeName);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeNameMockMvc.perform(put("/api/employee-names")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeNameDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EmployeeName in the database
        List<EmployeeName> employeeNameList = employeeNameRepository.findAll();
        assertThat(employeeNameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEmployeeName() throws Exception {
        // Initialize the database
        employeeNameRepository.saveAndFlush(employeeName);

        int databaseSizeBeforeDelete = employeeNameRepository.findAll().size();

        // Get the employeeName
        restEmployeeNameMockMvc.perform(delete("/api/employee-names/{id}", employeeName.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<EmployeeName> employeeNameList = employeeNameRepository.findAll();
        assertThat(employeeNameList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EmployeeName.class);
        EmployeeName employeeName1 = new EmployeeName();
        employeeName1.setId(1L);
        EmployeeName employeeName2 = new EmployeeName();
        employeeName2.setId(employeeName1.getId());
        assertThat(employeeName1).isEqualTo(employeeName2);
        employeeName2.setId(2L);
        assertThat(employeeName1).isNotEqualTo(employeeName2);
        employeeName1.setId(null);
        assertThat(employeeName1).isNotEqualTo(employeeName2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EmployeeNameDTO.class);
        EmployeeNameDTO employeeNameDTO1 = new EmployeeNameDTO();
        employeeNameDTO1.setId(1L);
        EmployeeNameDTO employeeNameDTO2 = new EmployeeNameDTO();
        assertThat(employeeNameDTO1).isNotEqualTo(employeeNameDTO2);
        employeeNameDTO2.setId(employeeNameDTO1.getId());
        assertThat(employeeNameDTO1).isEqualTo(employeeNameDTO2);
        employeeNameDTO2.setId(2L);
        assertThat(employeeNameDTO1).isNotEqualTo(employeeNameDTO2);
        employeeNameDTO1.setId(null);
        assertThat(employeeNameDTO1).isNotEqualTo(employeeNameDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(employeeNameMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(employeeNameMapper.fromId(null)).isNull();
    }
}
