package com.giraone.pms.domain.filter;

public class EmployeeFilterPair {

    private String key;
    private String value;

    public EmployeeFilterPair(String key, String value) {
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
    public String toString() {
        return "EmployeeFilterPair{" +
            "key='" + key + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}
