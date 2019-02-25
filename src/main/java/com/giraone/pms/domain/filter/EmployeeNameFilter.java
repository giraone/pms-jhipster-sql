package com.giraone.pms.domain.filter;

import java.util.Objects;

public class EmployeeNameFilter {

    private CompareOperation keyCompareOperation;
    private CompareOperation valueCompareOperation;
    private String key;
    private String value;

    public EmployeeNameFilter(CompareOperation keyCompareOperation, CompareOperation valueCompareOperation,
                              String key, String value) {
        this.keyCompareOperation = keyCompareOperation;
        this.valueCompareOperation = valueCompareOperation;
        this.key = key;
        this.value = value;
    }

    public CompareOperation getKeyCompareOperation() {
        return keyCompareOperation;
    }

    public CompareOperation getValueCompareOperation() {
        return valueCompareOperation;
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
        return keyCompareOperation == that.keyCompareOperation &&
            valueCompareOperation == that.valueCompareOperation &&
            Objects.equals(key, that.key) &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyCompareOperation, valueCompareOperation, key, value);
    }

    @Override
    public String toString() {
        return "EmployeeNameFilter{" +
            "keyCompareOperation=" + keyCompareOperation +
            ", valueCompareOperation=" + valueCompareOperation +
            ", key='" + key + '\'' +
            ", value='" + value + '\'' +
            '}';
    }

    public enum CompareOperation {

        EQUALS, LIKE
    }
}
