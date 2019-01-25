package com.giraone.pms.service.impl;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NameNormalizeServiceImplTest {

    private NameNormalizeServiceImpl nameNormalizeService = new NameNormalizeServiceImpl();
    private DoubleMetaphone doubleMetaphone = new DoubleMetaphone();

    @Test
    public void metaphone() {

        assertNull(doubleMetaphone.doubleMetaphone(""));
        assertEquals("A", doubleMetaphone.doubleMetaphone("A"));
        assertEquals("L", doubleMetaphone.doubleMetaphone("li"));

        assertEquals("HNS", doubleMetaphone.doubleMetaphone("heinz"));
        assertEquals("MR", doubleMetaphone.doubleMetaphone("maier"));
        assertEquals("XLS", doubleMetaphone.doubleMetaphone("schuelz"));
        assertEquals("XLSR", doubleMetaphone.doubleMetaphone("schuelzer"));
        assertEquals("XMT", doubleMetaphone.doubleMetaphone("schmidt"));
        assertEquals("XMT", doubleMetaphone.doubleMetaphone("schmit"));
        assertEquals("XMT", doubleMetaphone.doubleMetaphone("schmitt"));
    }

    @Test
    public void normalize() {

        assertEquals(Arrays.asList("karl", "heinz"), nameNormalizeService.normalize("Karl-Heinz"));
        assertEquals(Arrays.asList("karl", "heinz"), nameNormalizeService.normalize("Karl- Heinz"));
        assertEquals(Arrays.asList("karl", "heinz"), nameNormalizeService.normalize("Karl - Heinz"));
        assertEquals(Arrays.asList("dr", "wegner"), nameNormalizeService.normalize("Dr. Wegner"));

        assertEquals(Arrays.asList("virer", "zwei"), nameNormalizeService.normalize("Ein Zwei Li Vierer"));
    }

    @Test
    public void normalizeSingleName() {

        assertEquals("aeter", nameNormalizeService.normalizeSingleName("Ätheer"));
        assertEquals("tuer", nameNormalizeService.normalizeSingleName(" Tür "));
    }

    @Test
    public void normalizeSimplePhoneticSingleName() {

        assertEquals("boem", nameNormalizeService.normalizeSimplePhoneticSingleName("boehm"));
        assertEquals("boen", nameNormalizeService.normalizeSimplePhoneticSingleName("boehn"));
        assertEquals("boek", nameNormalizeService.normalizeSimplePhoneticSingleName("boeck"));
        assertEquals("koeler", nameNormalizeService.normalizeSimplePhoneticSingleName("koehler"));
        assertEquals("bart", nameNormalizeService.normalizeSimplePhoneticSingleName("barth"));
        assertEquals("smit", nameNormalizeService.normalizeSimplePhoneticSingleName("schmidt"));
        assertEquals("smit", nameNormalizeService.normalizeSimplePhoneticSingleName("schmied"));
        assertEquals("meir", nameNormalizeService.normalizeSimplePhoneticSingleName("mayr"));
        assertEquals("meir", nameNormalizeService.normalizeSimplePhoneticSingleName("maier"));
        assertEquals("smitz", nameNormalizeService.normalizeSimplePhoneticSingleName("schmits"));
        assertEquals("krist", nameNormalizeService.normalizeSimplePhoneticSingleName("christ"));
        assertEquals("richter", nameNormalizeService.normalizeSimplePhoneticSingleName("richter"));
    }

    @Test
    public void replaceUmlauts() {

        assertEquals("ae", nameNormalizeService.replaceUmlauts("ä"));
        assertEquals("ae", nameNormalizeService.replaceUmlauts("Ä"));
        assertEquals("oe", nameNormalizeService.replaceUmlauts("ö"));
        assertEquals("oe", nameNormalizeService.replaceUmlauts("Ö"));
        assertEquals("ue", nameNormalizeService.replaceUmlauts("ü"));
        assertEquals("ue", nameNormalizeService.replaceUmlauts("Ü"));
        assertEquals("e", nameNormalizeService.replaceUmlauts("é"));
    }

    @Test
    public void replaceSimplePhoneticVariants() {

        assertEquals("bart", nameNormalizeService.replaceSimplePhoneticVariants("barth"));
        assertEquals("smit", nameNormalizeService.replaceSimplePhoneticVariants("schmidt"));
        assertEquals("smit", nameNormalizeService.replaceSimplePhoneticVariants("schmied"));
        assertEquals("meir", nameNormalizeService.replaceSimplePhoneticVariants("mayr"));
        assertEquals("meir", nameNormalizeService.replaceSimplePhoneticVariants("maier"));
        assertEquals("smitz", nameNormalizeService.replaceSimplePhoneticVariants("schmits"));
        assertEquals("richter", nameNormalizeService.replaceSimplePhoneticVariants("richter"));
    }

    @Test
    public void replaceDigits() {

        assertEquals("testl", nameNormalizeService.replaceDigits("test0"));
    }

    @Test
    public void replaceCharacterRepetitions() {

        assertEquals("a", nameNormalizeService.replaceCharacterRepetitions("a"));
        assertEquals("a", nameNormalizeService.replaceCharacterRepetitions("aa"));
        assertEquals("a", nameNormalizeService.replaceCharacterRepetitions("aaa"));

        assertEquals("abc", nameNormalizeService.replaceCharacterRepetitions("abc"));
        assertEquals("abc", nameNormalizeService.replaceCharacterRepetitions("abbc"));
        assertEquals("abc", nameNormalizeService.replaceCharacterRepetitions("abcc"));
    }
}
