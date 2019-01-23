package com.giraone.pms.service.impl;

import com.giraone.pms.service.NameNormalizeService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NameNormalizeServiceImpl implements NameNormalizeService {

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
            Collections.sort(ret, StringLengthComparator);
            ret = ret.subList(0, 2);
            // Remove small parts, when we have at least one longer
            // TODO
        }
        return ret;
    }

    public String normalizeSingleName(String name) {

        name = name.trim().toLowerCase();
        name = replaceCharacterRepetitions(name);
        name = replaceUmlauts(name);
        return name;
    }

    public String replaceUmlauts(String in) {
        if (in == null) return in;
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

    public String replaceDigits(String in) {
        if (in == null) return in;
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

    public String replaceCharacterRepetitions(String in) {
        if (in == null) return in;
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

    private static final Comparator<String> StringLengthComparator = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            return s2.length() - s1.length();
        }
    };

    private static final HashMap<Character, String> UMLAUT_REPLACEMENTS = new HashMap<Character, String>();
    private static final HashMap<Character, String> SOUNDEX_DIGIT_REPLACEMENTS = new HashMap<Character, String>();

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
