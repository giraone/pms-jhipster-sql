package com.giraone.pms.domain.filter;

public class EmployeeNameKeyValue {

    private String key;
    private String value;

    public EmployeeNameKeyValue(String key, String value) {
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
        return "EmployeeNameKeyValue{" +
            "key='" + key + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}
