package com.giraone.pms.domain.filter;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonFilterTest {


    @Test
    public void buildFromInput_empty() {

        {
            PersonFilter personFilter = new PersonFilter("");
            assertThat(personFilter.getDateOfBirth()).isNull();
            assertThat(personFilter.getWeakMatchingNames().size()).isEqualTo(0);
        }

        {
            PersonFilter personFilter = new PersonFilter(" ");
            assertThat(personFilter.getDateOfBirth()).isNull();
            assertThat(personFilter.getWeakMatchingNames().size()).isEqualTo(0);
        }

        {
            PersonFilter personFilter = new PersonFilter(" 12");
            assertThat(personFilter.getDateOfBirth()).isNull();
            assertThat(personFilter.getWeakMatchingNames().size()).isEqualTo(0);
        }

        {
            PersonFilter personFilter = new PersonFilter(" 12 13 1X ");
            assertThat(personFilter.getDateOfBirth()).isNull();
            assertThat(personFilter.getWeakMatchingNames().size()).isEqualTo(0);
        }
    }

    @Test
    public void buildFromInput_singleExactNameOnly() {

        expectSingleWeakMatchingName("\"Li\"", "li");
        expectSingleWeakMatchingName("\"Müller\"", "mueller");
        expectSingleWeakMatchingName(" \"Müller\" ", "mueller");
        expectSingleWeakMatchingName(" \"Müller\" 1", "mueller");
        expectSingleWeakMatchingName(" \"Müller\" X1 1X ", "mueller");
    }

    @Test
    public void buildFromInput_singleWeakMatchingNameOnly() {

        expectSingleWeakMatchingName("Li", "li");
        expectSingleWeakMatchingName("Müller", "mueller");
        expectSingleWeakMatchingName(" Müller ", "mueller");
        expectSingleWeakMatchingName(" Müller 1", "mueller");
        expectSingleWeakMatchingName(" Müller X1 1X ", "mueller");
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

        expectDateAndSingleName("Müller 31.12.1975", "mueller", LocalDate.of(1975, Month.DECEMBER, 31));
        expectDateAndSingleName("Müller, 31.12.1975", "mueller", LocalDate.of(1975, Month.DECEMBER, 31));
    }

    @Test
    public void buildFromInput_dateAndTwoNames() {

        expectDateAndTwoNames("Müller-Wagner  31.12.1975", new String[]{"mueller", "wagner"}, LocalDate.of(1975, Month.DECEMBER, 31));
        expectDateAndTwoNames("Wagner-Müller, 31.12.1975", new String[]{"mueller", "wagner"}, LocalDate.of(1975, Month.DECEMBER, 31));
        expectDateAndTwoNames("Müller - Wagner, 31.12.1975", new String[]{"mueller", "wagner"}, LocalDate.of(1975, Month.DECEMBER, 31));
    }

    //------------------------------------------------------------------------------------------------------------------

    public void expectSingleWeakMatchingName(String input, String expected) {

        PersonFilter personFilter = new PersonFilter(input);
        assertThat(personFilter.getDateOfBirth()).isNull();
        assertThat(personFilter.getWeakMatchingNames().size()).isEqualTo(1);
        assertThat(personFilter.getWeakMatchingNames()).contains(expected);
    }

    public void expectSingleExactName(String input, String expected) {

        PersonFilter personFilter = new PersonFilter(input);
        assertThat(personFilter.getDateOfBirth()).isNull();
        assertThat(personFilter.getExactNames().size()).isEqualTo(1);
        assertThat(personFilter.getExactNames()).contains(expected);
    }

    public void expectDate(String input, LocalDate expected) {

        PersonFilter personFilter = new PersonFilter(input);
        assertThat(personFilter.getDateOfBirth()).isEqualTo(expected);
        assertThat(personFilter.getWeakMatchingNames().size()).isEqualTo(0);
    }


    public void expectDateAndSingleName(String input, String expectedName, LocalDate expectedDate) {

        PersonFilter personFilter = new PersonFilter(input);
        assertThat(personFilter.getDateOfBirth()).isEqualTo(expectedDate);
        assertThat(personFilter.getWeakMatchingNames().size()).isEqualTo(1);
        assertThat(personFilter.getWeakMatchingNames()).contains(expectedName);
    }

    public void expectDateAndTwoNames(String input, String[] expectedNames, LocalDate expectedDate) {

        PersonFilter personFilter = new PersonFilter(input);
        assertThat(personFilter.getDateOfBirth()).isEqualTo(expectedDate);
        assertThat(personFilter.getWeakMatchingNames().size()).isEqualTo(2);
        assertThat(personFilter.getWeakMatchingNames().get(0)).isIn(expectedNames);
        assertThat(personFilter.getWeakMatchingNames().get(1)).isIn(expectedNames);
    }
}
