package com.giraone.pms.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.giraone.pms.service.EmployeeNameService;
import com.giraone.pms.web.rest.errors.BadRequestAlertException;
import com.giraone.pms.web.rest.util.HeaderUtil;
import com.giraone.pms.service.dto.EmployeeNameDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * @param employeeNameDTO the employeeNameDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new employeeNameDTO, or with status 400 (Bad Request) if the employeeName has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/employee-names")
    @Timed
    public ResponseEntity<EmployeeNameDTO> createEmployeeName(@Valid @RequestBody EmployeeNameDTO employeeNameDTO) throws URISyntaxException {
        log.debug("REST request to save EmployeeName : {}", employeeNameDTO);
        if (employeeNameDTO.getId() != null) {
            throw new BadRequestAlertException("A new employeeName cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EmployeeNameDTO result = employeeNameService.save(employeeNameDTO);
        return ResponseEntity.created(new URI("/api/employee-names/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /employee-names : Updates an existing employeeName.
     *
     * @param employeeNameDTO the employeeNameDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated employeeNameDTO,
     * or with status 400 (Bad Request) if the employeeNameDTO is not valid,
     * or with status 500 (Internal Server Error) if the employeeNameDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/employee-names")
    @Timed
    public ResponseEntity<EmployeeNameDTO> updateEmployeeName(@Valid @RequestBody EmployeeNameDTO employeeNameDTO) throws URISyntaxException {
        log.debug("REST request to update EmployeeName : {}", employeeNameDTO);
        if (employeeNameDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EmployeeNameDTO result = employeeNameService.save(employeeNameDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, employeeNameDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /employee-names : get all the employeeNames.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of employeeNames in body
     */
    @GetMapping("/employee-names")
    @Timed
    public List<EmployeeNameDTO> getAllEmployeeNames() {
        log.debug("REST request to get all EmployeeNames");
        return employeeNameService.findAll();
    }

    /**
     * GET  /employee-names/:id : get the "id" employeeName.
     *
     * @param id the id of the employeeNameDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the employeeNameDTO, or with status 404 (Not Found)
     */
    @GetMapping("/employee-names/{id}")
    @Timed
    public ResponseEntity<EmployeeNameDTO> getEmployeeName(@PathVariable Long id) {
        log.debug("REST request to get EmployeeName : {}", id);
        Optional<EmployeeNameDTO> employeeNameDTO = employeeNameService.findOne(id);
        return ResponseUtil.wrapOrNotFound(employeeNameDTO);
    }

    /**
     * DELETE  /employee-names/:id : delete the "id" employeeName.
     *
     * @param id the id of the employeeNameDTO to delete
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
