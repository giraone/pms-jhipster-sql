package com.giraone.pms.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giraone.pms.domain.Employee;
import com.giraone.pms.security.AuthoritiesConstants;
import com.giraone.pms.service.EmployeeBulkService;
import com.giraone.pms.service.dto.EmployeeBulkDTO;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing employee bulk imports
 */
@RestController
@RequestMapping("/bulk-api")
public class EmployeeBulkResource {

    private final Logger log = LoggerFactory.getLogger(EmployeeBulkResource.class);

    private static final String ENTITY_NAME = "employee";

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static TypeReference<List<Employee>> typeRef = new TypeReference<List<Employee>>(){};

    private final EmployeeBulkService employeeBulkService;

    public EmployeeBulkResource(EmployeeBulkService employeeBulkImportRepository) {
        this.employeeBulkService = employeeBulkImportRepository;
    }


    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    @PutMapping("/employee-list")
    @Timed
    public ResponseEntity<Integer> insert(@RequestBody List<EmployeeBulkDTO> employees)  {

        log.info("EmployeeBulkResource.insert employees.size={}", employees.size());
        int count = employeeBulkService.save(employees);
        return ResponseEntity.ok()
            .body(count);
    }

    @PreAuthorize("hasRole(\"" + AuthoritiesConstants.ADMIN + "\")")
    @GetMapping("/re-index")
    @Timed
    public ResponseEntity<Integer> reIndex(@RequestParam(required = false, defaultValue = "false") boolean clear)  {

        log.info("EmployeeBulkResource.reIndex");
        int count = employeeBulkService.reIndex(clear);
        return ResponseEntity.ok()
            .body(count);
    }

    /*
    @RequestMapping(value = "/employee-bulk", method = RequestMethod.PUT,
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> import(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException
    {

        long contentLength = request.getContentLengthLong();
        InputStream in;
        try
        {
            in = request.getInputStream();
        }
        catch (IOException e)
        {
            log.error("Cannot read input stream!", e);
            throw new BadRequestAlertException("Cannot read input stream! " + e.getMessage(), ENTITY_NAME, "IO");
            return ResponseEntity.badRequest().body(new ErrorVM("Internal server error. " + e.getMessage()));
        }
        try {
            objectMapper.readValue(in, typeRef);
        }
        catch (Exception e) {

        }
        finally {
            IOUtils.closeQuietly();
        }
        return employeeBulkImportRepository.import(contentLength, in);
    }
    */
}
