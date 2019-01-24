package com.giraone.pms.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the EmployeeName entity.
 */
public class EmployeeNameDTO implements Serializable {

    @NotNull
    private Long ownerId;

    @NotNull
    private String nameKey;

    @NotNull
    private String nameValue;

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
        if (this == o) return true;
        if (!(o instanceof EmployeeNameDTO)) return false;
        EmployeeNameDTO that = (EmployeeNameDTO) o;
        return
            Objects.equals(getOwnerId(), that.getOwnerId()) &&
                Objects.equals(getNameKey(), that.getNameKey()) &&
                Objects.equals(getNameValue(), that.getNameValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwnerId(), getNameKey(), getNameValue());
    }

    @Override
    public String toString() {
        return "EmployeeNameDTO{" +
            ", ownerId=" + getOwnerId() +
            ", nameKey='" + getNameKey() + "'" +
            ", nameValue='" + getNameValue() + "'" +
            "}";
    }
}
