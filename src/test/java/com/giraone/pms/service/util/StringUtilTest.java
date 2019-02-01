package com.giraone.pms.service.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class StringUtilTest {


    @Test
    public void removeNonPrintable() {

        assertThat(StringUtil.removeNonPrintable(null)).isNull();
        assertThat(StringUtil.removeNonPrintable("")).isEqualTo("");
        assertThat(StringUtil.removeNonPrintable(" ")).isEqualTo(" ");
        assertThat(StringUtil.removeNonPrintable("á-à")).isEqualTo("á-à");
        assertThat(StringUtil.removeNonPrintable("á à")).isEqualTo("á à");
        assertThat(StringUtil.removeNonPrintable("á  à")).isEqualTo("á  à");
        assertThat(StringUtil.removeNonPrintable("á-\tà")).isEqualTo("á-à");
        assertThat(StringUtil.removeNonPrintable("á\r-à")).isEqualTo("á-à");
        assertThat(StringUtil.removeNonPrintable("á-\nà")).isEqualTo("á-à");
        assertThat(StringUtil.removeNonPrintable("á\b-à")).isEqualTo("á-à");
    }

    @Test
    public void removeNonName() {

        assertThat(StringUtil.removeNonName(null)).isNull();
        assertThat(StringUtil.removeNonName("")).isEqualTo("");
        assertThat(StringUtil.removeNonName(" ")).isEqualTo(" ");

        assertThat(StringUtil.removeNonName("á-à")).isEqualTo("á-à");
        assertThat(StringUtil.removeNonName("á à")).isEqualTo("á à");
        assertThat(StringUtil.removeNonName("á  à")).isEqualTo("á  à");
        assertThat(StringUtil.removeNonName("á-\tà")).isEqualTo("á-à");
        assertThat(StringUtil.removeNonName("á\r-à")).isEqualTo("á-à");
        assertThat(StringUtil.removeNonName("á-\nà")).isEqualTo("á-à");
        assertThat(StringUtil.removeNonName("á\b-à")).isEqualTo("á-à");

        assertThat(StringUtil.removeNonName("äöüÄÖÜßáàâøØ0123456789")).isEqualTo("äöüÄÖÜßáàâøØ0123456789");
        assertThat(StringUtil.removeNonName("O'Neill")).isEqualTo("O'Neill");
        assertThat(StringUtil.removeNonName("John the \"King\"")).isEqualTo("John the \"King\"");
        assertThat(StringUtil.removeNonName("John the ”King“")).isEqualTo("John the \"King\"");
        assertThat(StringUtil.removeNonName("John the «King»")).isEqualTo("John the \"King\"");
        assertThat(StringUtil.removeNonName("O'Neill")).isEqualTo("O'Neill");
        assertThat(StringUtil.removeNonName("O`Neill")).isEqualTo("O'Neill");
    }

    @Test
    public void trimAndRemoveDoubleWhiteSpace() {

        assertThat(StringUtil.trimAndRemoveDoubleWhiteSpaces(null)).isNull();
        assertThat(StringUtil.trimAndRemoveDoubleWhiteSpaces("")).isEqualTo("");
        assertThat(StringUtil.trimAndRemoveDoubleWhiteSpaces(" ")).isEqualTo("");
        assertThat(StringUtil.trimAndRemoveDoubleWhiteSpaces(" a")).isEqualTo("a");
        assertThat(StringUtil.trimAndRemoveDoubleWhiteSpaces(" a ")).isEqualTo("a");
        assertThat(StringUtil.trimAndRemoveDoubleWhiteSpaces(" a a ")).isEqualTo("a a");
        assertThat(StringUtil.trimAndRemoveDoubleWhiteSpaces(" a  a ")).isEqualTo("a a");
        assertThat(StringUtil.trimAndRemoveDoubleWhiteSpaces(" a     a ")).isEqualTo("a a");
    }

    @Test
    public void trimAndNormalize() {

        assertThat(StringUtil.trimAndNormalizeName(null)).isNull();
        assertThat(StringUtil.trimAndNormalizeName("")).isEqualTo("");
        assertThat(StringUtil.trimAndNormalizeName(" ")).isEqualTo("");

        assertThat(StringUtil.trimAndNormalizeName("á-à")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeName("á à")).isEqualTo("á à");
        assertThat(StringUtil.trimAndNormalizeName("á  à")).isEqualTo("á à");
        assertThat(StringUtil.trimAndNormalizeName("á     à")).isEqualTo("á à");
        assertThat(StringUtil.trimAndNormalizeName("á-\tà")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeName("á\r-à")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeName("á-\nà")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeName("á\b-à")).isEqualTo("á-à");

        assertThat(StringUtil.trimAndNormalizeName(" äöüÄÖÜßáàâøØ0123456789")).isEqualTo("äöüÄÖÜßáàâøØ0123456789");
        assertThat(StringUtil.trimAndNormalizeName(" O'Neill")).isEqualTo("O'Neill");
        assertThat(StringUtil.trimAndNormalizeName(" John the \"King\"")).isEqualTo("John the \"King\"");
        assertThat(StringUtil.trimAndNormalizeName(" John the ”King“")).isEqualTo("John the \"King\"");
        assertThat(StringUtil.trimAndNormalizeName(" John the «King»")).isEqualTo("John the \"King\"");
        assertThat(StringUtil.trimAndNormalizeName(" O'Neill")).isEqualTo("O'Neill");
        assertThat(StringUtil.trimAndNormalizeName(" O`Neill")).isEqualTo("O'Neill");

        assertThat(StringUtil.trimAndNormalizeName(" á-à ")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeName(" á -à ")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeName(" á- à ")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeName(" á - à ")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeName(" á-\tà ")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeName(" á\r-à ")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeName(" á-\nà ")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeName(" á\b-à ")).isEqualTo("á-à");
    }

    @Test
    public void trimAndNormalizeText() {

        assertThat(StringUtil.trimAndNormalizeText(null)).isNull();
        assertThat(StringUtil.trimAndNormalizeText("")).isEqualTo("");
        assertThat(StringUtil.trimAndNormalizeText(" ")).isEqualTo("");

        assertThat(StringUtil.trimAndNormalizeText("á-à")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeText("á à")).isEqualTo("á à");
        assertThat(StringUtil.trimAndNormalizeText("á  à")).isEqualTo("á à");
        assertThat(StringUtil.trimAndNormalizeText("á     à")).isEqualTo("á à");
        assertThat(StringUtil.trimAndNormalizeText("á-\tà")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeText("á\r-à")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeText("á-\nà")).isEqualTo("á-à");
        assertThat(StringUtil.trimAndNormalizeText("á\b-à")).isEqualTo("á-à");

        assertThat(StringUtil.trimAndNormalizeText(" äöüÄÖÜßáàâøØ0123456789")).isEqualTo("äöüÄÖÜßáàâøØ0123456789");
        assertThat(StringUtil.trimAndNormalizeText(" O'Neill")).isEqualTo("O'Neill");
        assertThat(StringUtil.trimAndNormalizeText(" John the \"King\"")).isEqualTo("John the \"King\"");
        assertThat(StringUtil.trimAndNormalizeText(" John the ”King“")).isEqualTo("John the ”King“");
        assertThat(StringUtil.trimAndNormalizeText(" John the «King»")).isEqualTo("John the «King»");
        assertThat(StringUtil.trimAndNormalizeText(" O'Neill")).isEqualTo("O'Neill");
        assertThat(StringUtil.trimAndNormalizeText(" O`Neill")).isEqualTo("O`Neill");
    }
}
