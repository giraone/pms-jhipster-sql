package com.giraone.pms.domain.filter;

import java.util.Objects;

public class EmployeeNameFilter {

    private String key;
    private String value;

    public EmployeeNameFilter(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeNameFilter that = (EmployeeNameFilter) o;
        return Objects.equals(key, that.key) &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "EmployeeNameFilter{" +
            "key='" + key + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}
