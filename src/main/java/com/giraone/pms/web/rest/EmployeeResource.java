package com.giraone.pms.web.rest;

import com.giraone.pms.domain.filter.PersonFilter;
import com.giraone.pms.service.AuthorizationService;
import com.giraone.pms.service.CompanyService;
import com.giraone.pms.service.EmployeeService;
import com.giraone.pms.service.dto.CompanyBasicInfoDTO;
import com.giraone.pms.service.dto.CompanyDTO;
import com.giraone.pms.service.dto.EmployeeDTO;
import com.giraone.pms.web.rest.errors.BadRequestAlertException;
import com.giraone.pms.web.rest.util.HeaderUtil;
import com.giraone.pms.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Employee.
 */
@RestController
@RequestMapping("/api")
public class EmployeeResource {

    private final Logger log = LoggerFactory.getLogger(EmployeeResource.class);

    private static final String ENTITY_NAME = "employee";

    private final EmployeeService employeeService;
    private final CompanyService companyService;
    private final AuthorizationService authorizationService;

    public EmployeeResource(EmployeeService employeeService, CompanyService companyService,
                            AuthorizationService authorizationService) {
        this.employeeService = employeeService;
        this.companyService = companyService;
        this.authorizationService = authorizationService;
    }

    /**
     * POST  /employees : Create a new employee.
     *
     * @param employeeDTO the employeeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new employeeDTO, or with status 400 (Bad Request) if the employee has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/employees")
    @Timed
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) throws URISyntaxException {
        log.debug("REST request to save Employee : {}", employeeDTO);
        if (employeeDTO.getId() != null) {
            throw new BadRequestAlertException("A new employee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EmployeeDTO result = employeeService.save(employeeDTO);
        return ResponseEntity.created(new URI("/api/employees/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /employees : Updates an existing employee.
     *
     * @param employeeDTO the employeeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated employeeDTO,
     * or with status 400 (Bad Request) if the employeeDTO is not valid,
     * or with status 500 (Internal Server Error) if the employeeDTO couldn't be updated
     */
    @PutMapping("/employees")
    @Timed
    public ResponseEntity<EmployeeDTO> updateEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        log.debug("REST request to update Employee : {}", employeeDTO);
        if (employeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EmployeeDTO result = employeeService.save(employeeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, employeeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /employees : get all the employees.
     *
     * @param companyExternalId restrict the query to employees of this company
     * @param filter            restrict the output to employees matching this free form filter
     * @param pageable          the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of employees in body
     * or status 404 (NOT FOUND), if the companyExternalId is invalid.
     */
    /*
    @PreAuthorize("(hasRole(\"" + AuthoritiesConstants.USER + "\") and "
        + "authorizationService.check(#companyExternalId, authentication.principal.username))"
        + "or hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\") or hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    */
    @GetMapping("/employees")
    @Timed
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(
        @RequestParam(required = false) String companyExternalId,
        @RequestParam(required = false) String filter,
        Pageable pageable) {

        boolean isAdmin = authorizationService.isAdmin();
        long timer = System.currentTimeMillis();
        log.debug("REST request to query employees isAdmin={}, companyExternalId={}, filter={}, pageable={}",
            isAdmin, companyExternalId, filter, pageable);


        if (!isAdmin) {
            Optional<String> userLogin = authorizationService.getCurrentUserLogin();
            if (!userLogin.isPresent()) {
                throw new AccessDeniedException("Cannot obtain user login!");
            }
            if (companyExternalId == null) {
                log.debug("Attempt by user {} to query without companyExternalId!", userLogin.get());
                List<CompanyDTO> companies = employeeService.getAllCompaniesOfEmployee(userLogin.get());
                if (companies.isEmpty()) {
                    return ResponseEntity.ok().body(new ArrayList<>());
                } else {
                    companyExternalId = companies.get(0).getExternalId();
                }
            }

            if (!this.authorizationService.check(companyExternalId)) {
                log.warn("Attempt by user {} to query company {} without access rights!",
                    userLogin.get(), companyExternalId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        Page<EmployeeDTO> page;
        if (isAdmin && companyExternalId == null) {
            page = employeeService.findAll(pageable);
        } else {
            final PersonFilter personFilter = new PersonFilter(filter);
            Optional<Page<EmployeeDTO>> result = employeeService.findAllByFilter(companyExternalId, personFilter, pageable);
            if (!result.isPresent()) {
                log.debug("- companyExternalId {} is invalid!", companyExternalId);
                return ResponseEntity.notFound().build();
            }
            page = result.get();
        }

        log.debug("- size={}, totalElements={}", page.getContent().size(), page.getTotalElements());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/employees");
        // The timer is used to prevent, that earlier requests, which took longer are processed after
        // a faster later request was already processed by the client
        headers.add("X-Timer", Long.toString(timer));
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /employees/:id : get the "id" employee.
     *
     * @param id the id of the employeeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the employeeDTO, or with status 404 (Not Found)
     */
    //@PreAuthorize("hasRole(\"" + AuthoritiesConstants.USER + "\") or hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    //@PostAuthorize("returnObject.body == null || authorizationService.check(#returnObject.body.companyId, authentication.principal.username)")
    @GetMapping("/employees/{id}")
    @Timed
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long id) {

        boolean isAdmin = authorizationService.isAdmin();
        log.debug("REST request to get Employee : isAdmin={} id={}", isAdmin, id);

        Optional<EmployeeDTO> employeeDTO = employeeService.findOne(id);

        return ResponseUtil.wrapOrNotFound(employeeDTO);
    }

    @GetMapping("/companies-of-employee")
    @Timed
    public ResponseEntity<List<CompanyBasicInfoDTO>> getAllCompaniesOfEmployee() {

        boolean isAdmin = authorizationService.isAdmin();
        log.debug("REST request to query companies of employee isAdmin={}", isAdmin);
        final String userLogin = authorizationService.getCurrentUserLogin().orElseThrow(
            () -> new AccessDeniedException("Current user login not found!"));
        log.debug("- by user {}", userLogin);

        List<CompanyBasicInfoDTO> result = companyService.findAllOfUserWithBasicInfosOnly();
        return ResponseEntity.ok().body(result);
    }

    /**
     * DELETE  /employees/:id : delete the "id" employee.
     *
     * @param id the id of the employeeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/employees/{id}")
    @Timed
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {

        boolean isAdmin = authorizationService.isAdmin();
        log.debug("REST request to delete Employee : isAdmin={} id={}", isAdmin, id);
        employeeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
