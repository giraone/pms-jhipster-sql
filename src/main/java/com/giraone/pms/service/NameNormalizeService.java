package com.giraone.pms.service;

import java.util.List;

/**
 * Interface for normalization of person name (lowercase, trim, umlauts, soundex, ...)
 */
public interface NameNormalizeService {

    /**
     * Split a given string into name pairs, e.g. spit double names like "Wagner-Schmidt", "von der Tann"
     *
     * @param input The input string
     * @return list of names, that have at least 2 characters, which is sorted by string length descending and
     * which has a maximum of 2 entries.
     */
    List<String> split(String input);

    /**
     * Normalize a name by using trim, lowercase and umlaut replacement
     *
     * @param input The input string
     * @return the normalized input string
     */
    String normalize(String input);

    String reduceSimplePhonetic(String name);

    String phonetic(String name);
}
