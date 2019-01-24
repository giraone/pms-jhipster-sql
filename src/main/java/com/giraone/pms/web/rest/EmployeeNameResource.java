package com.giraone.pms.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.giraone.pms.domain.EmployeeName;
import com.giraone.pms.service.EmployeeNameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing EmployeeName.
 */
@RestController
@RequestMapping("/api")
public class EmployeeNameResource {

    private final Logger log = LoggerFactory.getLogger(EmployeeNameResource.class);

    private final EmployeeNameService employeeNameService;

    public EmployeeNameResource(EmployeeNameService employeeNameService) {
        this.employeeNameService = employeeNameService;
    }

    /**
     * GET  /employee-names : get all the employeeNames.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of employeeNames in body
     */
    @GetMapping("/employee-names")
    @Timed
    public List<EmployeeName> getAllEmployeeNames() {
        log.debug("REST request to get all EmployeeNames");
        return employeeNameService.findAll();
    }

}
