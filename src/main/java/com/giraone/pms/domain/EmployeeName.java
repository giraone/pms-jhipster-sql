package com.giraone.pms.domain;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Normalized names of the employee
 */
@ApiModel(description = "Normalized names of the employee")
@Entity
@Table(name = "employee_name")
public class EmployeeName implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private EmployeeNameCompoundKey id;

    public EmployeeNameCompoundKey getId() {
        return id;
    }

    public void setId(EmployeeNameCompoundKey id) {
        this.id = id;
    }
}
