package com.giraone.pms.service.impl;

import com.giraone.pms.service.NameNormalizeService;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
public class NameNormalizeServiceImpl implements NameNormalizeService {

    private static final Logger log = LoggerFactory.getLogger(NameNormalizeServiceImpl.class);

    private final DoubleMetaphone doubleMetaphone = new DoubleMetaphone();

    public NameNormalizeServiceImpl() {
    }

    /**
     * Split a given string into name pairs, e.g. spit double names like "Wagner-Schmidt", "von der Tann"
     *
     * @param input The input string
     * @param minLength minimal length of string to be accepted
     * @return list of names, that have at least 2 characters, which is sorted by string length descending and
     * which has a maximum of 2 entries.
     */
    public List<String> split(String input, int minLength) {

        log.debug("NameNormalizeServiceImpl.split input={}, minLength={}", input, minLength);
        List<String> ret = new ArrayList<>();
        input = normalize(input);
        if (input == null) {
            return ret;
        }
        // Split a all non word characters (this includes digits!)
        String[] parts = input.split("[^\\p{L}\\p{Nd}]+");
        for (String part : parts) {
            log.debug(" - split " + part);
            if (part.length() > 0) { // empty strings are not accepted
                if (part.charAt(0) < '0' || part.charAt(0) > '9') { // skip words starting with a leading digit (dates)
                    if (part.length() >= minLength) {
                        //log.debug("add " + part);
                        ret.add(part);
                    }
                }
            }
        }

        if (ret.size() >= 2) {
            // Accept a maximum of 2 parts
            ret.sort(StringLengthComparator);
            ret = ret.subList(0, 2);
            // Remove small parts, when we have at least one longer
            if (ret.get(0).length() >= 4 && ret.get(1).length() <= 2) {
                //log.debug("remove " + ret.get(1));
                ret.remove(1);
            }
        }
        return ret;
    }

    /**
     * Split a given string into name pairs, e.g. spit double names like "Wagner-Schmidt", "von der Tann"
     *
     * @param input The input string
     * @return list of names, that have at least 2 characters, which is sorted by string length descending and
     * which has a maximum of 2 entries.
     */
    public List<String> split(String input) {

       return split(input, 2);
    }

    /**
     * Normalize a name by using trim, lowercase and umlaut replacement
     *
     * @param input The input string
     * @return the normalized input string or null, if the input is ull or contains only whitespaces
     */
    public String normalize(String input) {

        if (input == null) {
            return null;
        }
        input = input.trim().toLowerCase();
        input = replaceUmlauts(input);
        if (input.length() > 0) {
            return input;
        } else {
            return null;
        }
    }

    /**
     * Apply simple phonetic reduction on top of normalization
     *
     * @param input The input string, that must be already normalized
     * @return the phonetic reduced string or null, if the input is null or contains only whitespaces
     */
    public String reduceSimplePhonetic(String input) {

        if (input == null) {
            return null;
        }
        input = replaceSimplePhoneticVariants(input);
        input = replaceCharacterRepetitions(input);
        if (input.length() > 0) {
            return input;
        } else {
            return null;
        }
    }

    /**
     * Apply double metaphone algorithm on top of normalization
     *
     * @param input The input string, that must be already normalized
     * @return the double metaphone string or null, if the input is null or contains only whitespaces
     */
    public String phonetic(String input) {

        if (input == null) {
            return null;
        }
        input = replaceDigits(input);
        return this.doubleMetaphone.doubleMetaphone(input);
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
