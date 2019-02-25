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

        expectSingleExactMatchingNameWithoutComma("\"Li\"", "li");
        expectSingleExactMatchingNameWithoutComma("\"Müller\"", "müller");
        expectSingleExactMatchingNameWithoutComma(" \"Müller\" ", "müller");
    }

    @Test
    public void buildFromInput_singleWeakMatchingNameOnly() {

        expectSingleWeakMatchingNameWithoutComma("Li", "li");
        expectSingleWeakMatchingNameWithoutComma("Müller", "mueler");
        expectSingleWeakMatchingNameWithoutComma(" Müller ", "mueler");
    }

    @Test
    public void buildFromInput_twoExactNamesWithComma() {

        expectTwoExactMatchingNameWithComma("\"Li\", \"Wu\"", "li", "wu");
        expectTwoExactMatchingNameWithComma("\"Müller\", \"René\"", "müller", "rené");
    }

    @Test
    public void buildFromInput_twoWeakNamesWithComma() {

        expectTwoWeakMatchingNameWithComma("Li,Wu", "li", "wu");
        expectTwoWeakMatchingNameWithComma("Li, Wu", "li", "wu");
        expectTwoWeakMatchingNameWithComma("Li,  Wu", "li", "wu");
        expectTwoWeakMatchingNameWithComma("  Li,  Wu  ", "li", "wu");
        expectTwoWeakMatchingNameWithComma("Müller, René", "mueler", "rene");
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

        expectDateAndSingleNameWithoutComma("Müller 31.12.1975", "mueler", LocalDate.of(1975, Month.DECEMBER, 31));
        expectDateAndSingleNameWithoutComma("Müller 31.12.75", "mueler", LocalDate.of(1975, Month.DECEMBER, 31));
        expectDateAndSingleNameWithoutComma("Müller 31.12.05", "mueler", LocalDate.of(2005, Month.DECEMBER, 31));
    }

    @Test
    public void buildFromInput_SurnamesAndGivenNamesWithComma() {

        expectSuramesAndGivenNamesWithComma("Müller-Wagner, René", new String[]{"mueler", "wagner"}, new String[]{"rene"});
        expectSuramesAndGivenNamesWithComma("Wagner-Müller, René", new String[]{"mueler", "wagner"}, new String[]{"rene"});
        expectSuramesAndGivenNamesWithComma("Müller-Wagner, Karl-Heinz", new String[]{"mueler", "wagner"}, new String[]{"karl", "heinz"});
        expectSuramesAndGivenNamesWithComma("Wagner-Müller, Heinz-Karl", new String[]{"mueler", "wagner"}, new String[]{"karl", "heinz"});
    }

    @Test
    public void buildFromInput_dateAndTwoSurnames() {

        expectDateAndTwoSuramesWithoutComma("Müller-Wagner 31.12.1975", new String[]{"mueler", "wagner"}, LocalDate.of(1975, Month.DECEMBER, 31));
        expectDateAndTwoSuramesWithoutComma("Wagner-Müller 31.12.1975", new String[]{"mueler", "wagner"}, LocalDate.of(1975, Month.DECEMBER, 31));
    }

    //------------------------------------------------------------------------------------------------------------------

    private void expectSingleExactMatchingNameWithoutComma(String input, String expected) {

        EmployeeNameFilter expectedFilter = new EmployeeNameFilter(
            EmployeeNameFilter.CompareOperation.LIKE, EmployeeNameFilter.CompareOperation.EQUALS,
            "L", expected);
        expectSingleMatchingName(input, expectedFilter, false);
    }

    private void expectSingleWeakMatchingNameWithoutComma(String input, String expected) {

        EmployeeNameFilter expectedFilter = new EmployeeNameFilter(
            EmployeeNameFilter.CompareOperation.LIKE, EmployeeNameFilter.CompareOperation.LIKE,
            "N", expected);
        expectSingleMatchingName(input, expectedFilter, false);
    }

    private void expectTwoExactMatchingNameWithComma(String input, String expectedSurname, String expectedGivenName) {

        PersonFilter personFilter = new PersonFilter(input, false);
        assertThat(personFilter.getDateOfBirth()).isNull();
        assertThat(personFilter.getNames().size()).isEqualTo(2);

        EmployeeNameFilter expectedFilter1 = new EmployeeNameFilter(
            EmployeeNameFilter.CompareOperation.EQUALS, EmployeeNameFilter.CompareOperation.EQUALS,
            EmployeeNameFilterKey.LS.name(), expectedSurname);
        EmployeeNameFilter expectedFilter2 = new EmployeeNameFilter(
            EmployeeNameFilter.CompareOperation.EQUALS, EmployeeNameFilter.CompareOperation.EQUALS,
            EmployeeNameFilterKey.LG.name(), expectedGivenName);
        assertThat(personFilter.getNames()).contains(expectedFilter1);
        assertThat(personFilter.getNames()).contains(expectedFilter2);
    }

    private void expectTwoWeakMatchingNameWithComma(String input, String expectedSurname, String expectedGivenName) {

        PersonFilter personFilter = new PersonFilter(input, false);
        assertThat(personFilter.getDateOfBirth()).isNull();
        assertThat(personFilter.getNames().size()).isEqualTo(2);

        EmployeeNameFilter expectedFilter1 = new EmployeeNameFilter(
            EmployeeNameFilter.CompareOperation.EQUALS, EmployeeNameFilter.CompareOperation.LIKE,
            EmployeeNameFilterKey.NS.name(), expectedSurname);
        EmployeeNameFilter expectedFilter2 = new EmployeeNameFilter(
            EmployeeNameFilter.CompareOperation.EQUALS, EmployeeNameFilter.CompareOperation.LIKE,
            EmployeeNameFilterKey.NG.name(), expectedGivenName);
        assertThat(personFilter.getNames()).contains(expectedFilter1);
        assertThat(personFilter.getNames()).contains(expectedFilter2);
    }

    private void expectSuramesAndGivenNamesWithComma(String input, String[] expectedSurnames, String[] expectedGivenNames) {

        PersonFilter personFilter = new PersonFilter(input, false);
        assertThat(personFilter.getDateOfBirth()).isNull();
        assertThat(personFilter.getNames().size()).isEqualTo(expectedSurnames.length + expectedGivenNames.length);

        EmployeeNameFilter[] expectedFilters1 = Arrays.asList(expectedSurnames).stream()
            .map(s -> new EmployeeNameFilter(
                EmployeeNameFilter.CompareOperation.EQUALS, EmployeeNameFilter.CompareOperation.LIKE,
                EmployeeNameFilterKey.NS.name(), s))
            .collect(Collectors.toList()).toArray(new EmployeeNameFilter[0]);
        EmployeeNameFilter[] expectedFilters2 = Arrays.asList(expectedGivenNames).stream()
            .map(s -> new EmployeeNameFilter(
                EmployeeNameFilter.CompareOperation.EQUALS, EmployeeNameFilter.CompareOperation.LIKE,
                EmployeeNameFilterKey.NG.name(), s))
            .collect(Collectors.toList()).toArray(new EmployeeNameFilter[0]);
        assertThat(personFilter.getNames()).contains(expectedFilters1);
        assertThat(personFilter.getNames()).contains(expectedFilters2);
    }

    private void expectSinglePhoneticMatchingName(String input, String expected) {

        EmployeeNameFilter expectedFilter = new EmployeeNameFilter(
            EmployeeNameFilter.CompareOperation.EQUALS, EmployeeNameFilter.CompareOperation.LIKE,
            EmployeeNameFilterKey.PS.name(), expected);
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

    private void expectDateAndSingleNameWithoutComma(String input, String expected, LocalDate expectedDate) {

        EmployeeNameFilter expectedFilter = new EmployeeNameFilter(
            EmployeeNameFilter.CompareOperation.LIKE, EmployeeNameFilter.CompareOperation.LIKE,
            "N", expected);
        expectDateAndSingleNameWithoutComma(input, expectedFilter, expectedDate);
    }

    private void expectDateAndSingleNameWithoutComma(String input, EmployeeNameFilter expected, LocalDate expectedDate) {

        PersonFilter personFilter = new PersonFilter(input);
        assertThat(personFilter.getDateOfBirth()).isEqualTo(expectedDate);
        assertThat(personFilter.getNames().size()).isEqualTo(1);
        assertThat(personFilter.getNames()).contains(expected);
    }

    private void expectDateAndTwoSuramesWithoutComma(String input, String[] expectedNames, LocalDate expectedDate) {

        EmployeeNameFilter[] expectedFilters = Arrays.asList(expectedNames).stream()
            .map(s -> new EmployeeNameFilter(
                EmployeeNameFilter.CompareOperation.LIKE, EmployeeNameFilter.CompareOperation.LIKE,
                "N", s))
            .collect(Collectors.toList()).toArray(new EmployeeNameFilter[0]);
        expectDateAndTwoSuramesWithoutComma(input, expectedFilters, expectedDate);
    }

    private void expectDateAndTwoSuramesWithoutComma(String input, EmployeeNameFilter[] expectedNames, LocalDate expectedDate) {

        PersonFilter personFilter = new PersonFilter(input);
        assertThat(personFilter.getDateOfBirth()).isEqualTo(expectedDate);
        assertThat(personFilter.getNames().size()).isEqualTo(2);
        assertThat(personFilter.getNames().get(0)).isIn((Object[]) expectedNames);
        assertThat(personFilter.getNames().get(1)).isIn((Object[]) expectedNames);
    }
}
