package com.giraone.pms.domain.filter;

import com.giraone.pms.domain.enumeration.EmployeeNameFilterKey;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonFilterTest {


    @Test
    public void buildFromInput_empty() {

        {
            PersonFilter personFilter = new PersonFilter("");
            assertThat(personFilter.getDateOfBirth()).isNull();
            assertThat(personFilter.getNames().size()).isEqualTo(0);
        }

        {
            PersonFilter personFilter = new PersonFilter(" ");
            assertThat(personFilter.getDateOfBirth()).isNull();
            assertThat(personFilter.getNames().size()).isEqualTo(0);
        }

        {
            PersonFilter personFilter = new PersonFilter(" 12");
            assertThat(personFilter.getDateOfBirth()).isNull();
            assertThat(personFilter.getNames().size()).isEqualTo(0);
        }

        {
            PersonFilter personFilter = new PersonFilter(" 12 13 1X ");
            assertThat(personFilter.getDateOfBirth()).isNull();
            assertThat(personFilter.getNames().size()).isEqualTo(0);
        }
    }

    @Test
    public void buildFromInput_singleExactNameOnly() {

        expectSingleExactMatchingName("\"Li\"", "li");
        expectSingleExactMatchingName("\"Müller\"", "müller");
        expectSingleExactMatchingName(" \"Müller\" ", "müller");
        expectSingleExactMatchingName(" \"Müller\" 1", "müller");
        expectSingleExactMatchingName(" \"Müller\" X1 1X ", "müller");
    }

    @Test
    public void buildFromInput_singleWeakMatchingNameOnly() {

        expectSingleWeakMatchingName("Li", "li%");
        expectSingleWeakMatchingName("Müller", "mueler%");
        expectSingleWeakMatchingName(" Müller ", "mueler%");
        expectSingleWeakMatchingName(" Müller 1", "mueler%");
        expectSingleWeakMatchingName(" Müller X1 1X ", "mueler%");
    }

    @Test
    public void buildFromInput_dateOnly() {

        expectDate("31.12.1975", LocalDate.of(1975, Month.DECEMBER, 31));
        expectDate(" 31.12.1975 ", LocalDate.of(1975, Month.DECEMBER, 31));

        expectDate("1.12.1975", LocalDate.of(1975, Month.DECEMBER, 1));
        expectDate("01.12.1975", LocalDate.of(1975, Month.DECEMBER, 1));

        expectDate("1.1.1975", LocalDate.of(1975, Month.JANUARY, 1));
        expectDate("01.1.1975", LocalDate.of(1975, Month.JANUARY, 1));
        expectDate("01.01.1975", LocalDate.of(1975, Month.JANUARY, 1));

        expectDate("01.12.2000", LocalDate.of(2000, Month.DECEMBER, 1));
    }

    @Test
    public void buildFromInput_dateAndSingleName() {

        expectDateAndSingleName("Müller 31.12.1975", "mueler%", LocalDate.of(1975, Month.DECEMBER, 31));
        expectDateAndSingleName("Müller, 31.12.1975", "mueler%", LocalDate.of(1975, Month.DECEMBER, 31));
    }

    @Test
    public void buildFromInput_dateAndTwoSurnames() {

        expectDateAndTwoNames("Müller-Wagner 31.12.1975", new String[]{"mueler%", "wagner%"}, LocalDate.of(1975, Month.DECEMBER, 31));
        expectDateAndTwoNames("Wagner-Müller 31.12.1975", new String[]{"mueler%", "wagner%"}, LocalDate.of(1975, Month.DECEMBER, 31));
        expectDateAndTwoNames("Wagner-Müller, 31.12.1975", new String[]{"mueler%", "wagner%"}, LocalDate.of(1975, Month.DECEMBER, 31));
    }

    //------------------------------------------------------------------------------------------------------------------

    private void expectSingleExactMatchingName(String input, String expected) {

        EmployeeNameFilter expectedFilter = new EmployeeNameFilter(EmployeeNameFilterKey.SL.name(), expected);
        expectSingleMatchingName(input, expectedFilter, false);
    }

    private void expectSingleWeakMatchingName(String input, String expected) {

        EmployeeNameFilter expectedFilter = new EmployeeNameFilter(EmployeeNameFilterKey.SN.name(), expected);
        expectSingleMatchingName(input, expectedFilter, false);
    }

    private void expectSinglePhoneticMatchingName(String input, String expected) {

        EmployeeNameFilter expectedFilter = new EmployeeNameFilter(EmployeeNameFilterKey.SP.name(), expected);
        expectSingleMatchingName(input, expectedFilter, true);
    }

    private void expectSingleMatchingName(String input, EmployeeNameFilter expected, boolean phonetic) {

        PersonFilter personFilter = new PersonFilter(input, phonetic);
        assertThat(personFilter.getDateOfBirth()).isNull();
        assertThat(personFilter.getNames().size()).isEqualTo(1);
        assertThat(personFilter.getNames()).contains(expected);
    }

    private void expectDate(String input, LocalDate expected) {

        PersonFilter personFilter = new PersonFilter(input);
        assertThat(personFilter.getDateOfBirth()).isEqualTo(expected);
        assertThat(personFilter.getNames().size()).isEqualTo(0);
    }

    private void expectDateAndSingleName(String input, String expected, LocalDate expectedDate) {

        EmployeeNameFilter expectedFilter = new EmployeeNameFilter(EmployeeNameFilterKey.SN.name(), expected);
        expectDateAndSingleName(input, expectedFilter, expectedDate);
    }

    private void expectDateAndSingleName(String input, EmployeeNameFilter expected, LocalDate expectedDate) {

        PersonFilter personFilter = new PersonFilter(input);
        assertThat(personFilter.getDateOfBirth()).isEqualTo(expectedDate);
        assertThat(personFilter.getNames().size()).isEqualTo(1);
        assertThat(personFilter.getNames()).contains(expected);
    }

    private void expectDateAndTwoNames(String input, String[] expectedNames, LocalDate expectedDate) {

        EmployeeNameFilter[] expectedFilters = Arrays.asList(expectedNames).stream()
            .map(s -> new EmployeeNameFilter(EmployeeNameFilterKey.SN.name(), s))
            .collect(Collectors.toList()).toArray(new EmployeeNameFilter[0]);
        expectDateAndTwoNames(input, expectedFilters, expectedDate);
    }

    private void expectDateAndTwoNames(String input, EmployeeNameFilter[] expectedNames, LocalDate expectedDate) {

        PersonFilter personFilter = new PersonFilter(input);
        assertThat(personFilter.getDateOfBirth()).isEqualTo(expectedDate);
        assertThat(personFilter.getNames().size()).isEqualTo(2);
        assertThat(personFilter.getNames().get(0)).isIn((Object[]) expectedNames);
        assertThat(personFilter.getNames().get(1)).isIn((Object[]) expectedNames);
    }
}
