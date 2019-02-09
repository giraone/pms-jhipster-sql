package com.giraone.pms.domain;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Normalized names of the employee. The whole table with owner, key, name is the primary unique key.
 * And all three values are used in queries.
 */
@Embeddable
public class EmployeeNameCompoundKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(optional = false)
    // This is a valid hibernate annotation, but we perform this cascade delete via liquibase
    // @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private Employee owner;

    @NotNull
    @Column(name = "name_key", nullable = false)
    private String nameKey;

    @NotNull
    @Column(name = "name_value", nullable = false)
    private String nameValue;

    public EmployeeNameCompoundKey() {
    }

    public EmployeeNameCompoundKey(@NotNull Employee owner, @NotNull String nameKey, @NotNull String nameValue) {
        this.owner = owner;
        this.nameKey = nameKey;
        this.nameValue = nameValue;
    }

    public Employee getOwner() {
        return owner;
    }

    public void setOwner(Employee owner) {
        this.owner = owner;
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
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeNameCompoundKey that = (EmployeeNameCompoundKey) o;
        return Objects.equals(owner, that.owner) &&
            Objects.equals(nameKey, that.nameKey) &&
            Objects.equals(nameValue, that.nameValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, nameKey, nameValue);
    }

    @Override
    public String toString() {
        return "EmployeeNameCompoundKey{" +
            "ownerId=" + owner.getId() +
            ", nameKey='" + nameKey + '\'' +
            ", nameValue='" + nameValue + '\'' +
            '}';
    }
}
