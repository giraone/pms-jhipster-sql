package com.giraone.pms.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.giraone.pms.domain.EmployeeName;
import com.giraone.pms.service.EmployeeNameService;
import com.giraone.pms.web.rest.errors.BadRequestAlertException;
import com.giraone.pms.web.rest.util.HeaderUtil;
import com.giraone.pms.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing EmployeeName.
 */
@RestController
@RequestMapping("/api")
public class EmployeeNameResource {

    private final Logger log = LoggerFactory.getLogger(EmployeeNameResource.class);

    private static final String ENTITY_NAME = "employeeName";

    private final EmployeeNameService employeeNameService;

    public EmployeeNameResource(EmployeeNameService employeeNameService) {
        this.employeeNameService = employeeNameService;
    }

    /**
     * POST  /employee-names : Create a new employeeName.
     *
     * @param employeeName the employeeName to create
     * @return the ResponseEntity with status 201 (Created) and with body the new employeeName, or with status 400 (Bad Request) if the employeeName has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/employee-names")
    @Timed
    public ResponseEntity<EmployeeName> createEmployeeName(@Valid @RequestBody EmployeeName employeeName) throws URISyntaxException {
        log.debug("REST request to save EmployeeName : {}", employeeName);
        if (employeeName.getId() != null) {
            throw new BadRequestAlertException("A new employeeName cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EmployeeName result = employeeNameService.save(employeeName);
        return ResponseEntity.created(new URI("/api/employee-names/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /employee-names : Updates an existing employeeName.
     *
     * @param employeeName the employeeName to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated employeeName,
     * or with status 400 (Bad Request) if the employeeName is not valid,
     * or with status 500 (Internal Server Error) if the employeeName couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/employee-names")
    @Timed
    public ResponseEntity<EmployeeName> updateEmployeeName(@Valid @RequestBody EmployeeName employeeName) throws URISyntaxException {
        log.debug("REST request to update EmployeeName : {}", employeeName);
        if (employeeName.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EmployeeName result = employeeNameService.save(employeeName);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, employeeName.getId().toString()))
            .body(result);
    }

    /**
     * GET  /employee-names : get all the employeeNames.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of employeeNames in body
     */
    @GetMapping("/employee-names")
    @Timed
    public ResponseEntity<List<EmployeeName>> getAllEmployeeNames(Pageable pageable) {
        log.debug("REST request to get a page of EmployeeNames");
        Page<EmployeeName> page = employeeNameService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/employee-names");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /employee-names/:id : get the "id" employeeName.
     *
     * @param id the id of the employeeName to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the employeeName, or with status 404 (Not Found)
     */
    @GetMapping("/employee-names/{id}")
    @Timed
    public ResponseEntity<EmployeeName> getEmployeeName(@PathVariable Long id) {
        log.debug("REST request to get EmployeeName : {}", id);
        Optional<EmployeeName> employeeName = employeeNameService.findOne(id);
        return ResponseUtil.wrapOrNotFound(employeeName);
    }

    /**
     * DELETE  /employee-names/:id : delete the "id" employeeName.
     *
     * @param id the id of the employeeName to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/employee-names/{id}")
    @Timed
    public ResponseEntity<Void> deleteEmployeeName(@PathVariable Long id) {
        log.debug("REST request to delete EmployeeName : {}", id);
        employeeNameService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
