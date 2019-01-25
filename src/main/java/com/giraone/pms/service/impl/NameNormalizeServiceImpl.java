package com.giraone.pms.service.impl;

import com.codahale.metrics.annotation.Timed;
import com.giraone.pms.service.NameNormalizeService;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NameNormalizeServiceImpl implements NameNormalizeService {

    private final DoubleMetaphone doubleMetaphone = new DoubleMetaphone();

    public List<String> normalize(String name) {

        List<String> ret = new ArrayList<>();
        name = normalizeSingleName(name);

        // Split a all non word characters
        String[] parts = name.split("[^\\p{L}\\p{Nd}]+");
        for (String part : parts) {
            if (part.length() > 1) { // single characters are not accepted
                ret.add(part);
            }
        }

        if (ret.size() > 2) {
            // Accept a maximum of 2 parts
            ret.sort(StringLengthComparator);
            ret = ret.subList(0, 2);
            // Remove small parts, when we have at least one longer
            // TODO
        }
        return ret;
    }

    @Timed
    public String normalizeSingleName(String name) {

        name = name.trim().toLowerCase();
        name = replaceUmlauts(name);
        return name;
    }

    @Timed
    public String normalizeSimplePhoneticSingleName(String name) {

        name = replaceSimplePhoneticVariants(name);
        name = replaceCharacterRepetitions(name);
        return name;
    }

    public String normalizePhoneticSingleName(String name) {

        name = replaceDigits(name);
        return this.doubleMetaphone.doubleMetaphone(name);
    }

    String replaceUmlauts(String in) {
        if (in == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder();
        String r;
        for (int i = 0; i < in.length(); i++) {
            final char c = in.charAt(i);
            if ((r = UMLAUT_REPLACEMENTS.get(c)) != null)
                ret.append(r);
            else
                ret.append(c);
        }
        return ret.toString();
    }

    String replaceSimplePhoneticVariants(String in) {
        if (in == null) {
            return null;
        }
        for (Pair<String, String> entry : SIMPLE_PHONETIC_REPLACEMENTS) {
            in = in.replace(entry.getFirst(), entry.getSecond());
        }
        return in;
    }

    String replaceDigits(String in) {
        if (in == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder();
        String r;
        for (int i = 0; i < in.length(); i++) {
            final char c = in.charAt(i);
            if ((r = SOUNDEX_DIGIT_REPLACEMENTS.get(c)) != null)
                ret.append(r);
            else
                ret.append(c);
        }
        return ret.toString();
    }

    String replaceCharacterRepetitions(String in) {
        if (in == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder();
        char lastChar = ' ';
        for (int i = 0; i < in.length(); i++) {
            final char c = in.charAt(i);
            if (c != lastChar) {
                ret.append(c);
                lastChar = c;
            }
        }
        return ret.toString();
    }

    private static final Comparator<String> StringLengthComparator = (s1, s2) -> s2.length() - s1.length();

    private static final HashMap<Character, String> UMLAUT_REPLACEMENTS = new HashMap<>();
    private static final List<Pair<String, String>> SIMPLE_PHONETIC_REPLACEMENTS = new ArrayList<>();
    private static final HashMap<Character, String> SOUNDEX_DIGIT_REPLACEMENTS = new HashMap<>();

    static {
        UMLAUT_REPLACEMENTS.put('Ä', "ae");
        UMLAUT_REPLACEMENTS.put('ä', "ae");
        UMLAUT_REPLACEMENTS.put('Ö', "oe");
        UMLAUT_REPLACEMENTS.put('ö', "oe");
        UMLAUT_REPLACEMENTS.put('Ü', "ue");
        UMLAUT_REPLACEMENTS.put('ü', "ue");
        UMLAUT_REPLACEMENTS.put('ß', "ss");
        UMLAUT_REPLACEMENTS.put('é', "e");
        UMLAUT_REPLACEMENTS.put('è', "e");
        UMLAUT_REPLACEMENTS.put('ê', "e");
        UMLAUT_REPLACEMENTS.put('á', "a");
        UMLAUT_REPLACEMENTS.put('à', "a");
        UMLAUT_REPLACEMENTS.put('â', "a");
        UMLAUT_REPLACEMENTS.put('ç', "c");

        // Order is important!
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("sch", "s"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("chr", "kr"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("ck", "k"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("dt", "t"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("th", "t"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("hm", "m"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("hn", "n"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("hl", "l"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("hr", "r"));
        //SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("ht", "t")); // wegen richter!
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("ts", "tz"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("ai", "ei"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("ay", "ei"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("ey", "ei"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("ie", "i"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("ad", "at"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("ed", "et"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("id", "it"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("od", "ot"));
        SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("ud", "ut"));

        /*
        Becker vs Bäcker
         */
        //SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("y", "i"));
        //SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("v", "w"));
        //SIMPLE_PHONETIC_REPLACEMENTS.add(Pair.of("p", "b"));

        SOUNDEX_DIGIT_REPLACEMENTS.put('0', "l");
        SOUNDEX_DIGIT_REPLACEMENTS.put('1', "s");
        SOUNDEX_DIGIT_REPLACEMENTS.put('2', "w");
        SOUNDEX_DIGIT_REPLACEMENTS.put('3', "d");
        SOUNDEX_DIGIT_REPLACEMENTS.put('4', "r");
        SOUNDEX_DIGIT_REPLACEMENTS.put('5', "f");
        SOUNDEX_DIGIT_REPLACEMENTS.put('6', "ch");
        SOUNDEX_DIGIT_REPLACEMENTS.put('7', "b");
        SOUNDEX_DIGIT_REPLACEMENTS.put('8', "t");
        SOUNDEX_DIGIT_REPLACEMENTS.put('9', "n");
    }
}
