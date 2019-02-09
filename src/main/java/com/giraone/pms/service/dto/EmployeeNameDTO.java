package com.giraone.pms.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the EmployeeName entity.
 */
public class EmployeeNameDTO implements Serializable {

    private Long id;

    @NotNull
    private Long ownerId;

    @NotNull
    private String nameKey;

    @NotNull
    private String nameValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    public String getNameValue() {
        return nameValue;
    }

    public void setNameValue(String nameValue) {
        this.nameValue = nameValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EmployeeNameDTO employeeNameDTO = (EmployeeNameDTO) o;
        if (employeeNameDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), employeeNameDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EmployeeNameDTO{" +
            "id=" + getId() +
            ", ownerId=" + getOwnerId() +
            ", nameKey='" + getNameKey() + "'" +
            ", nameValue='" + getNameValue() + "'" +
            "}";
    }
}
