package com.giraone.pms.domain;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Normalized names of the employee
 */
@Embeddable
public class EmployeeNameCompoundKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "owner_id", nullable = false)
    private long ownerId;

    @NotNull
    @Column(name = "name_key", nullable = false)
    private String nameKey;

    @NotNull
    @Column(name = "name_value", nullable = false)
    private String nameValue;

    public EmployeeNameCompoundKey() {
    }

    public EmployeeNameCompoundKey(long ownerId, @NotNull String nameKey, @NotNull String nameValue) {
        this.ownerId = ownerId;
        this.nameKey = nameKey;
        this.nameValue = nameValue;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
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
        if (!(o instanceof EmployeeNameCompoundKey)) return false;
        EmployeeNameCompoundKey that = (EmployeeNameCompoundKey) o;
        return
            Objects.equals(getOwnerId(), that.getOwnerId()) &&
                Objects.equals(getNameKey(), that.getNameKey()) &&
                Objects.equals(getNameValue(), that.getNameValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwnerId(), getNameKey(), getNameValue());
    }
}
