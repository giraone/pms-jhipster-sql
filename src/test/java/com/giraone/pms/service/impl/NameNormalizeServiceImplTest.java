package com.giraone.pms.service.impl;

import com.giraone.pms.service.NameNormalizeService;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class NameNormalizeServiceImplTest {

    NameNormalizeServiceImpl nameNormalizeService = new NameNormalizeServiceImpl();
    DoubleMetaphone doubleMetaphone = new DoubleMetaphone();

    @Test
    public void metaphone() {

        Assert.assertEquals("HNS", doubleMetaphone.doubleMetaphone("heinz"));
        Assert.assertEquals("MR", doubleMetaphone.doubleMetaphone("maier"));
        Assert.assertEquals("XLS", doubleMetaphone.doubleMetaphone("schuelz"));
        Assert.assertEquals("XLSR", doubleMetaphone.doubleMetaphone("schuelzer"));
        Assert.assertEquals("XMT", doubleMetaphone.doubleMetaphone("schmidt"));
        Assert.assertEquals("XMT", doubleMetaphone.doubleMetaphone("schmit"));
        Assert.assertEquals("XMT", doubleMetaphone.doubleMetaphone("schmitt"));
    }

    @Test
    public void normalize() {

        Assert.assertEquals(Arrays.asList(new String[] { "karl", "heinz" }), nameNormalizeService.normalize("Karl-Heinz"));
        Assert.assertEquals(Arrays.asList(new String[] { "karl", "heinz" }), nameNormalizeService.normalize("Karl- Heinz"));
        Assert.assertEquals(Arrays.asList(new String[] { "karl", "heinz" }), nameNormalizeService.normalize("Karl - Heinz"));
        Assert.assertEquals(Arrays.asList(new String[] { "dr", "wegner" }), nameNormalizeService.normalize("Dr. Wegner"));

        Assert.assertEquals(Arrays.asList(new String[] { "vierer", "zwei" }), nameNormalizeService.normalize("Ein Zwei Li Vierer"));
    }

    @Test
    public void normalizeSingleName() {

        Assert.assertEquals("aether", nameNormalizeService.normalizeSingleName("Ätheer"));
        Assert.assertEquals("tuer", nameNormalizeService.normalizeSingleName(" Tür "));
    }

    @Test
    public void replaceUmlauts() {

        Assert.assertEquals("ae", nameNormalizeService.replaceUmlauts("ä"));
        Assert.assertEquals("ae", nameNormalizeService.replaceUmlauts("Ä"));
        Assert.assertEquals("oe", nameNormalizeService.replaceUmlauts("ö"));
        Assert.assertEquals("oe", nameNormalizeService.replaceUmlauts("Ö"));
        Assert.assertEquals("ue", nameNormalizeService.replaceUmlauts("ü"));
        Assert.assertEquals("ue", nameNormalizeService.replaceUmlauts("Ü"));
    }

    @Test
    public void replaceDigits() {
    }

    @Test
    public void replaceCharacterRepetitions() {

        Assert.assertEquals("a", nameNormalizeService.replaceCharacterRepetitions("a"));
        Assert.assertEquals("a", nameNormalizeService.replaceCharacterRepetitions("aa"));
        Assert.assertEquals("a", nameNormalizeService.replaceCharacterRepetitions("aaa"));

        Assert.assertEquals("abc", nameNormalizeService.replaceCharacterRepetitions("abc"));
        Assert.assertEquals("abc", nameNormalizeService.replaceCharacterRepetitions("abbc"));
        Assert.assertEquals("abc", nameNormalizeService.replaceCharacterRepetitions("abcc"));
    }
}
