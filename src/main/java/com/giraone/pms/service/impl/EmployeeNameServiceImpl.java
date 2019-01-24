package com.giraone.pms.service.impl;

import com.giraone.pms.domain.EmployeeName;
import com.giraone.pms.repository.EmployeeNameRepository;
import com.giraone.pms.service.EmployeeNameService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing EmployeeName.
 */
@Service
@Transactional
public class EmployeeNameServiceImpl implements EmployeeNameService {

    private final Logger log = LoggerFactory.getLogger(EmployeeNameServiceImpl.class);

    private final EmployeeNameRepository employeeNameRepository;


    public EmployeeNameServiceImpl(EmployeeNameRepository employeeNameRepository) {
        this.employeeNameRepository = employeeNameRepository;
    }

    /**
     * Get all the employeeNames.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<EmployeeName> findAll() {
        log.debug("Request to get all EmployeeNames");
        return employeeNameRepository.findAll().stream()
            .collect(Collectors.toCollection(LinkedList::new));
    }
}
