package com.giraone.pms.domain.filter;

import com.giraone.pms.domain.enumeration.EmployeeNameFilterKey;
import com.giraone.pms.domain.enumeration.StringSearchMode;
import com.giraone.pms.service.NameNormalizeService;

import java.time.LocalDate;
import java.util.Optional;

public class EmployeeFilter {

    private Optional<String> surname;
    private StringSearchMode surnameSearchMode;
    private Optional<LocalDate> dateOfBirth;

    public EmployeeFilter() {
    }

    public EmployeeFilter(Optional<String> surname, StringSearchMode surnameSearchMode, Optional<LocalDate> dateOfBirth) {
        this.surname = surname;
        this.surnameSearchMode = surnameSearchMode;
        this.dateOfBirth = dateOfBirth;
    }

    public Optional<String> getSurname() {
        return surname;
    }

    public void setSurname(Optional<String> surname) {
        this.surname = surname;
    }

    public StringSearchMode getSurnameSearchMode() {
        return surnameSearchMode;
    }

    public void setSurnameSearchMode(StringSearchMode surnameSearchMode) {
        this.surnameSearchMode = surnameSearchMode;
    }

    public Optional<LocalDate> getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Optional<LocalDate> dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public EmployeeFilterPair buildQueryValue(NameNormalizeService nameNormalizeService) {

        if (!surname.isPresent()) {
            return null;
        }
        String input = this.surname.get();
        switch (this.surnameSearchMode) {
            case NORMALIZED:
                return new EmployeeFilterPair(EmployeeNameFilterKey.SN.toString(), nameNormalizeService.normalizeSingleName(input));
            case PREFIX_NORMALIZED:
                return new EmployeeFilterPair(EmployeeNameFilterKey.SN.toString(), nameNormalizeService.normalizeSingleName(input) + "%");
            case PHONETIC:
                return new EmployeeFilterPair(EmployeeNameFilterKey.SP.toString(), nameNormalizeService.normalizePhoneticSingleName(input));
            default:
                return new EmployeeFilterPair(null, input);
        }
    }

    @Override
    public String toString() {
        return "EmployeeFilter{" +
            "surname='" + surname + '\'' +
            ", surnameSearchMode=" + surnameSearchMode +
            ", dateOfBirth=" + dateOfBirth +
            '}';
    }
}
