package com.giraone.pms.domain;

import io.swagger.annotations.ApiModel;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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

    @ManyToOne(optional = false)
    @NotNull
    private Company company;

    // Needed for JPA
    public EmployeeName() {
    }

    public EmployeeName(@NotNull Employee owner, @NotNull String nameKey, @NotNull String nameValue) {
        this.id = new EmployeeNameCompoundKey(owner, nameKey, nameValue);
        this.company = owner.getCompany();
    }

    public EmployeeName(@NotNull EmployeeNameCompoundKey id) {
        this.id = id;
    }

    public EmployeeNameCompoundKey getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    @Override
    public String toString() {
        return "EmployeeName{" +
            "id=" + id +
            ", company_id=" + (company != null ? company.getId() : "null") +
            '}';
    }
}
