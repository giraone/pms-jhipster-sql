package com.giraone.pms.domain;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * Normalized names of the employee
 */
@ApiModel(description = "Normalized names of the employee")
@Entity
@Table(name = "employee_name")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EmployeeName implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @NotNull
    @Column(name = "name_key", nullable = false)
    private String nameKey;

    @NotNull
    @Column(name = "name_value", nullable = false)
    private String nameValue;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public EmployeeName ownerId(Long ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getNameKey() {
        return nameKey;
    }

    public EmployeeName nameKey(String nameKey) {
        this.nameKey = nameKey;
        return this;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    public String getNameValue() {
        return nameValue;
    }

    public EmployeeName nameValue(String nameValue) {
        this.nameValue = nameValue;
        return this;
    }

    public void setNameValue(String nameValue) {
        this.nameValue = nameValue;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmployeeName employeeName = (EmployeeName) o;
        if (employeeName.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), employeeName.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EmployeeName{" +
            "id=" + getId() +
            ", ownerId=" + getOwnerId() +
            ", nameKey='" + getNameKey() + "'" +
            ", nameValue='" + getNameValue() + "'" +
            "}";
    }
}
