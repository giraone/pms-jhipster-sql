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
            case LOWERCASE:
                return new EmployeeFilterPair(EmployeeNameFilterKey.SL.toString(),
                    nameNormalizeService.normalize(input));
            case PREFIX_LOWERCASE:
                return new EmployeeFilterPair(EmployeeNameFilterKey.SL.toString(),
                    nameNormalizeService.normalize(input) + "%");
            case REDUCED:
                return new EmployeeFilterPair(EmployeeNameFilterKey.SN.toString(),
                    nameNormalizeService.reduceSimplePhonetic(nameNormalizeService.normalize(input)));
            case PREFIX_REDUCED:
                return new EmployeeFilterPair(EmployeeNameFilterKey.SN.toString(),
                    nameNormalizeService.reduceSimplePhonetic(nameNormalizeService.normalize(input)) + "%");
            case PHONETIC:
                return new EmployeeFilterPair(EmployeeNameFilterKey.SP.toString(),
                    nameNormalizeService.phonetic(nameNormalizeService.normalize(input)));
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
