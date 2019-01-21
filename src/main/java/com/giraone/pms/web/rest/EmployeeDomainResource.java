package com.giraone.pms.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.giraone.pms.security.AuthoritiesConstants;
import com.giraone.pms.security.SecurityUtils;
import com.giraone.pms.service.AuthorizationService;
import com.giraone.pms.service.EmployeeDomainService;
import com.giraone.pms.service.dto.EmployeeDTO;
import com.giraone.pms.web.rest.errors.InternalServerErrorException;
import com.giraone.pms.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final AuthorizationService authorizationService;

    public EmployeeDomainResource(EmployeeDomainService employeeDomainService,
                                  AuthorizationService authorizationService) {
        this.employeeDomainService = employeeDomainService;
        this.authorizationService = authorizationService;
    }

    /**
     * GET  /employees : get all the employees.
     *
     * @param companyExternalId restrict the query to employees of this company
     * @param surnamePrefix restrict the query to employees with a surname matching this prefix
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of employees in body
     * or status 404 (NOT FOUND), if the companyExternalId is invalid.
     */
    /*
    @PreAuthorize("(hasRole(\"" + AuthoritiesConstants.USER + "\") and "
        + "authorizationService.check(#companyExternalId, authentication.principal.username))"
        + "or hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    */
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\") or hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    @GetMapping("/employees")
    @Timed
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(
        @RequestParam String companyExternalId, @RequestParam String surnamePrefix, Pageable pageable) {

        log.debug("REST request to query employees companyExternalId={}, surnamePrefix={}", companyExternalId, surnamePrefix);

        final String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
        log.debug("- by user {}", userLogin);

        if (!this.authorizationService.check(companyExternalId, userLogin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Page<EmployeeDTO>> result = employeeDomainService.findAll(companyExternalId, surnamePrefix, pageable);
        if (!result.isPresent()) {
            log.debug("- companyExternalId {} is invalid!", companyExternalId);
            return ResponseEntity.notFound().build();
        }
        Page<EmployeeDTO> page = result.get();
        log.debug("- size={}, totalElements={}", page.getContent().size(), page.getTotalElements());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/domain-api/employees");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /employees/:id : get the "id" employee.
     *
     * @param id the id of the employeeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the employeeDTO, or with status 404 (Not Found)
     */
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\") or hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    //@PostAuthorize("returnObject.body == null || authorizationService.check(#returnObject.body.companyId, authentication.principal.username)")
    @GetMapping("/employees/{id}")
    @Timed
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long id) {

        log.debug("REST request to get Employee : {}", id);
        final String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new InternalServerErrorException("Current user login not found"));
        log.debug("- by user {}", userLogin);

        Optional<EmployeeDTO> employeeDTO = employeeDomainService.findOne(id);
        if (employeeDTO.isPresent() && !this.authorizationService.check(employeeDTO.get().getCompanyId(), userLogin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ResponseEntity<EmployeeDTO> ret = ResponseUtil.wrapOrNotFound(employeeDTO);
        return ret;
    }
}
