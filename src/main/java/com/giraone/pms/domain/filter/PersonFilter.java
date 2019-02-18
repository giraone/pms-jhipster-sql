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

public class PersonFilter {

    private static final Logger log = LoggerFactory.getLogger(PersonFilter.class);

    @Autowired
    NameNormalizeService nameNormalizeService = new NameNormalizeServiceImpl();

    private static final Pattern DATE_PATTERN = Pattern.compile("([0-3]?[0-9])[./]([0-1]?[0-9])[./]((19[0-9]{2})|(20[0-9]{2})|([0-9]{2}))");
    private static final Pattern EXACT_NAME_PATTERN = Pattern.compile("(\"[^\"]*\")");


    private List<EmployeeNameFilter> names = new ArrayList<>();
    private boolean phonetic = false;
    private LocalDate dateOfBirth;


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
        input = this.extractExactNames(input);
        this.extractWeakNames(input);
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

    private String extractExactNames(String input) {

        final Matcher matcher = EXACT_NAME_PATTERN.matcher(input);
        // is there sth. like an exact name in the input?
        boolean first = true;
        while (matcher.find()) {
            log.debug(String.format("Exact name found in \"%s\"", input));
            final String exactName = input.substring(matcher.start() + 1, matcher.end() - 1);
            if (first) {
                this.names.add(new EmployeeNameFilter(EmployeeNameFilterKey.LS.name(), exactName));
                first = false;
                log.debug(String.format("Exact surname was \"%s\"", exactName));
            } else {
                this.names.add(new EmployeeNameFilter(EmployeeNameFilterKey.LG.name(), exactName));
                log.debug(String.format("Exact givenName was \"%s\"", exactName));
            }
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

    private void extractWeakNames(String input) {

        Optional<Integer> minLength = this.names.stream().map(e -> e.getValue().length()).max(Integer::compareTo);
        final boolean[] first = new boolean[] { true };
        final List<EmployeeNameFilter> nameList = this.nameNormalizeService.split(input, minLength.orElse(1))
            .stream()
            .map(name -> this.nameNormalizeService.normalize(name))
            .map(name -> this.phonetic ?
                this.nameNormalizeService.phonetic(name) : this.nameNormalizeService.reduceSimplePhonetic(name))
            .map(name -> {
                EmployeeNameFilterKey key;
                if (first[0]) {
                    key = phonetic ? EmployeeNameFilterKey.PS : EmployeeNameFilterKey.NS;
                    first[0] = false;
                } else {
                    key = phonetic ? EmployeeNameFilterKey.PG : EmployeeNameFilterKey.NG;
                }
                return new EmployeeNameFilter(key.name(), name + "%");
            })
            .collect(Collectors.toList());
        if (nameList.size() > 0) {
            log.debug(String.format("Name list found with %d elements: %s", nameList.size(), nameList.toString()));
        }
        this.names.addAll(nameList);
    }

    @Override
    public String toString() {
        return "PersonFilter{" +
            "names=" + names +
            ", phonetic=" + phonetic +
            ", dateOfBirth=" + dateOfBirth +
            '}';
    }
}
