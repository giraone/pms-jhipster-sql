package com.giraone.pms.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.giraone.pms.service.EmployeeDomainService;
import com.giraone.pms.service.dto.EmployeeDTO;
import com.giraone.pms.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Employee (domain version).
 */
@RestController
@RequestMapping("/domain-api")
public class EmployeeDomainResource {

    private final Logger log = LoggerFactory.getLogger(EmployeeDomainResource.class);

    private static final String ENTITY_NAME = "employee";

    private final EmployeeDomainService employeeDomainService;

    public EmployeeDomainResource(EmployeeDomainService employeeDomainService) {
        this.employeeDomainService = employeeDomainService;
    }

    /**
     * GET  /employees : get all the employees.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of employees in body
     */
    @GetMapping("/employees")
    @Timed
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(
        @RequestParam String companyId, @RequestParam String surnamePrefix, Pageable pageable) {
        log.debug("REST request to query employees companyId={}, surnamePrefix={}", companyId, surnamePrefix);
        Page<EmployeeDTO> page = employeeDomainService.findAll(companyId, surnamePrefix, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api2/employees");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /employees/:id : get the "id" employee.
     *
     * @param id the id of the employeeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the employeeDTO, or with status 404 (Not Found)
     */
    @GetMapping("/employees/{id}")
    @Timed
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long id) {
        log.debug("REST request to get Employee : {}", id);
        Optional<EmployeeDTO> employeeDTO = employeeDomainService.findOne(id);
        return ResponseUtil.wrapOrNotFound(employeeDTO);
    }
}
