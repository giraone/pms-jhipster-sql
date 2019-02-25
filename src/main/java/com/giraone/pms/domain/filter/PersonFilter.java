package com.giraone.pms.domain.filter;

import com.giraone.pms.domain.enumeration.EmployeeNameFilterKey;
import com.giraone.pms.service.NameNormalizeService;
import com.giraone.pms.service.impl.NameNormalizeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Person filter for query
 * 1. If a date (e.g. dd.mm.yy) is present, the query is made on the date of birth an the date is stripped from the input
 * 2. it is checked, whether a comma is present.
 * 2a. If comma is not present, the query is made on the employee_name table without a key, so surnames and givenNames can match
 * 2b. If comma is present, the input is split in two parts A und B
 * 2b1. If A und B contain at least one character, the query is an AND join between a surname (part A) and a givenName (part B)
 * 2b2. If A contains at least one character and not B, the query is a query on the surname only
 * 2b3. If B contains at least one character and not A, the query is a query on the givenName only
 * 2b4. If A and B are empty, there is no filter on the surname or givenName
 */
public class PersonFilter {

    private static final Logger log = LoggerFactory.getLogger(PersonFilter.class);

    @Autowired
    NameNormalizeService nameNormalizeService = new NameNormalizeServiceImpl();

    private static final Pattern DATE_PATTERN = Pattern.compile("([0-3]?[0-9])[./]([0-1]?[0-9])[./]((19[0-9]{2})|(20[0-9]{2})|([0-9]{2}))");
    private static final Pattern EXACT_NAME_PATTERN = Pattern.compile("(\"[^\"]*\")");


    private List<EmployeeNameFilter> names = new ArrayList<>();
    private boolean phonetic = false;
    private LocalDate dateOfBirth;
    private NameSearchMode nameSearchMode = NameSearchMode.NO_NAME;

    public PersonFilter(String input) {
        this.buildFromInput(input);
    }

    public PersonFilter(String input, boolean phonetic) {
        this.phonetic = phonetic;
        this.buildFromInput(input);
    }

    public boolean hasNames() {
        return !names.isEmpty();
    }

    public List<EmployeeNameFilter> getNames() {
        return names;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    private void buildFromInput(String input) {

        if (input == null) {
            return;
        }
        input = input.toLowerCase();
        input = this.extractDateOfBirth(input);
        NameParts nameParts = this.checkComma(input);
        nameParts = this.extractExactNames(nameParts);
        this.extractWeakNames(nameParts);
    }

    private NameParts checkComma(String input) {

        NameParts ret = new NameParts();
        int commaPosition;
        if ((commaPosition = input.indexOf(',')) != -1) {
            final String part1 = input.substring(0, commaPosition).trim();
            final String part2 = commaPosition < input.length() ? input.substring(commaPosition + 1).trim() : "";
            if (part1.length() > 0) {
                if (part2.length() > 0) {
                    this.nameSearchMode = NameSearchMode.BOTH_WITH_AND;
                    ret.surname = part1;
                    ret.givenName = part2;
                } else {
                    this.nameSearchMode = NameSearchMode.ONLY_SURNAME;
                    ret.surname = part1;
                }
            } else {
                if (part2.length() > 0) {
                    this.nameSearchMode = NameSearchMode.ONLY_GIVEN_NAME;
                    ret.givenName = part2;
                } else {
                    this.nameSearchMode = NameSearchMode.NO_NAME;
                }
            }
        } else {
            this.nameSearchMode = NameSearchMode.BOTH_WITH_OR;
            ret.both = input;
        }
        log.debug(String.format("checkComma: nameSearchMode=%s, %s", this.nameSearchMode, ret));
        return ret;
    }

    private String extractDateOfBirth(String input) {

        final Matcher matcher = DATE_PATTERN.matcher(input);

        // is there sth. like a date in the input?
        if (matcher.find()) {
            log.debug(String.format("Date found in \"%s\"", input));
            // extract day, month, year
            final String dayString = matcher.group(1).replaceFirst("^0", "");
            if ("".equals(dayString)) { return input; } // day was 0.XX.XXXX
            final String monthString = matcher.group(2).replaceFirst("^0", "");
            if ("".equals(monthString)) { return input; } // day was 0.XX.XXXX
            final String yearString = matcher.group(3);
            int year = Integer.parseInt(yearString);
            if (year < 19) {
                year+=2000;
            } else if (year < 100) {
                year+=1900;
            }
            //log.debug(String.format("Extracted d=\"%s\" m=\"%s\" y=\"%s\"", dayString, monthString, yearString));
            // now combine the rest without the match
            if (matcher.start() == 0) {
                if (matcher.end() < input.length()) {
                    input = input.substring(matcher.end());
                } else {
                    input = "";
                }
            } else if (matcher.end() == input.length()) {
                input = input.substring(0, matcher.start() - 1);
            } else {
                input = input.substring(0, matcher.start() - 1) + input.substring(matcher.end());
            }
            this.dateOfBirth = LocalDate.of(year, Integer.parseInt(monthString), Integer.parseInt(dayString));
            log.debug(String.format("Date was %s - rest is \"%s\"", this.dateOfBirth.toString(), input));
        }

        return input;
    }

    private NameParts extractExactNames(NameParts nameParts) {

        switch (this.nameSearchMode) {
            case BOTH_WITH_AND:
                nameParts.surname = this.extractExactNames(nameParts.surname, EmployeeNameFilter.CompareOperation.EQUALS, EmployeeNameFilterKey.LS.name());
                nameParts.givenName = this.extractExactNames(nameParts.givenName, EmployeeNameFilter.CompareOperation.EQUALS, EmployeeNameFilterKey.LG.name());
                break;
            case ONLY_SURNAME:
                nameParts.surname = this.extractExactNames(nameParts.surname, EmployeeNameFilter.CompareOperation.EQUALS, EmployeeNameFilterKey.LS.name());
                break;
            case ONLY_GIVEN_NAME:
                nameParts.givenName = this.extractExactNames(nameParts.givenName, EmployeeNameFilter.CompareOperation.EQUALS, EmployeeNameFilterKey.LG.name());
                break;
            case BOTH_WITH_OR:
                nameParts.both = this.extractExactNames(nameParts.both, EmployeeNameFilter.CompareOperation.LIKE, "L");
                break;
        }

        return nameParts;
    }

    private void extractWeakNames(NameParts nameParts) {

        switch (this.nameSearchMode) {
            case BOTH_WITH_AND:
                this.extractWeakNames(nameParts.surname, EmployeeNameFilter.CompareOperation.EQUALS, phonetic ? EmployeeNameFilterKey.PS.name() : EmployeeNameFilterKey.NS.name());
                this.extractWeakNames(nameParts.givenName, EmployeeNameFilter.CompareOperation.EQUALS, phonetic ? EmployeeNameFilterKey.PG.name() : EmployeeNameFilterKey.NG.name());
                break;
            case ONLY_SURNAME:
                this.extractWeakNames(nameParts.surname, EmployeeNameFilter.CompareOperation.EQUALS, phonetic ? EmployeeNameFilterKey.PS.name() : EmployeeNameFilterKey.NS.name());
                break;
            case ONLY_GIVEN_NAME:
                this.extractWeakNames(nameParts.givenName, EmployeeNameFilter.CompareOperation.EQUALS, phonetic ? EmployeeNameFilterKey.PG.name() : EmployeeNameFilterKey.NG.name());
                break;
            case BOTH_WITH_OR:
                this.extractWeakNames(nameParts.both, EmployeeNameFilter.CompareOperation.LIKE, phonetic ? "P" : "N");
                break;
        }

        int countS = 0;
        int countG = 0;
        for (EmployeeNameFilter e : this.names) {
            if (e.getKey().endsWith("S")) countS++;
            if (e.getKey().endsWith("G")) countG++;
        }
        if (countS > 0) {
            if (countG > 0) {
                this.nameSearchMode = NameSearchMode.BOTH_WITH_AND;
            } else {
                this.nameSearchMode = NameSearchMode.ONLY_SURNAME;
            }
        } else {
            if (countG > 0) {
                this.nameSearchMode = NameSearchMode.ONLY_GIVEN_NAME;
            } else {
                this.nameSearchMode = NameSearchMode.NO_NAME;
            }
        }
    }

    private String extractExactNames(String input, EmployeeNameFilter.CompareOperation keyCompareOperation, String filterKey) {

        log.debug(String.format("extractExactNames for %s in \"%s\"", filterKey, input));
        final Matcher matcher = EXACT_NAME_PATTERN.matcher(input);
        // is there sth. like an exact name in the input?
        while (matcher.find()) {
            log.debug(String.format("Exact name found in \"%s\" for %s", input, filterKey));
            final String exactName = input.substring(matcher.start() + 1, matcher.end() - 1);
            this.names.add(new EmployeeNameFilter(keyCompareOperation, EmployeeNameFilter.CompareOperation.EQUALS, filterKey, exactName));
            // now combine the rest without the match
            if (matcher.start() == 0) {
                if (matcher.end() < input.length()) {
                    input = input.substring(matcher.end());
                } else {
                    input = "";
                }
            } else if (matcher.end() == input.length()) {
                input = input.substring(0, matcher.start() - 1);
            } else {
                input = input.substring(0, matcher.start() - 1) + input.substring(matcher.end());
            }
        }
        return input;
    }

    private int extractWeakNames(String input, EmployeeNameFilter.CompareOperation keyCompareOperation, String filterKey) {

        log.debug(String.format("extractWeakNames for %s in \"%s\"", filterKey, input));
        //Optional<Integer> minLength = this.names.stream().map(e -> e.getValue().length()).max(Integer::compareTo);
        final List<EmployeeNameFilter> nameList = this.nameNormalizeService.split(input, 1)
            .stream()
            .map(name -> this.nameNormalizeService.normalize(name))
            .map(name -> this.phonetic ? this.nameNormalizeService.phonetic(name) : this.nameNormalizeService.reduceSimplePhonetic(name))
            .map(name -> new EmployeeNameFilter(keyCompareOperation, EmployeeNameFilter.CompareOperation.LIKE, filterKey, name))
            .collect(Collectors.toList());
        if (nameList.size() > 0) {
            log.debug(String.format("Weak name list found for %s with %d elements: %s", filterKey, nameList.size(), nameList.toString()));
        }
        this.names.addAll(nameList);
        return nameList.size();
    }

    @Override
    public String toString() {
        return "PersonFilter{" +
            "names=" + names +
            ", phonetic=" + phonetic +
            ", dateOfBirth=" + dateOfBirth +
            '}';
    }

    public enum NameSearchMode {

        NO_NAME, BOTH_WITH_OR, BOTH_WITH_AND, ONLY_SURNAME, ONLY_GIVEN_NAME
    }

    private class NameParts {

        String both;
        String surname;
        String givenName;

        @Override
        public String toString() {
            return "NameParts{" +
                "both='" + both + '\'' +
                ", surname='" + surname + '\'' +
                ", givenName='" + givenName + '\'' +
                '}';
        }
    }
}
