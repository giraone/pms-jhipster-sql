package com.giraone.pms.service.util;

import java.util.regex.Pattern;

public class StringUtil {


    // See https://www.regular-expressions.info/unicode.html

    private static Pattern SPACES =  Pattern.compile("\\p{Z}");
    private static Pattern DASHES =  Pattern.compile("\\p{Pd}");
    private static Pattern QUOTES =  Pattern.compile("[\\p{Pi}\\p{Pf}]");
    private static Pattern SINGLE_QUOTES =  Pattern.compile("['`′]");
    private static Pattern MULTIPLE_WHITESPACE =  Pattern.compile("[\\s][\\s]+");

    private static Pattern NON_PRINTABLE =  Pattern.compile("\\p{C}");
    private static Pattern NON_NAME =  Pattern.compile("[^\\p{L}\\p{Nd}\\p{Zs}\"'-]");
    private static Pattern WHITESPACE_AND_DASHES =  Pattern.compile("[\\p{Z}]*[-][\\p{Z}]*");

    /**
     * Remove non-printable characters
     * @param input the input string or null (if null, the returned string is also null)
     * @return the changed string
     */
    public static String removeNonPrintable(String input) {

        if (input == null) {
            return null;
        }
        return NON_PRINTABLE.matcher(input).replaceAll("");
    }

    /**
     * Remove characters, that are not in normal names (e.g. non-printables, punctations other than dash)
     * @param input the input string or null (if null, the returned string is also null)
     * @return the changed string
     */
    public static String removeNonName(String input) {

        if (input == null) {
            return null;
        }
        input = SPACES.matcher(input).replaceAll(" "); // normalizes spaces
        input = DASHES.matcher(input).replaceAll("-"); // normalizes dashes
        input = QUOTES.matcher(input).replaceAll("\""); // normalizes quotes
        input = SINGLE_QUOTES.matcher(input).replaceAll("'"); // normalizes single quotes
        input = NON_NAME.matcher(input).replaceAll("");
        return input;
    }

    /**
     * Trim white spaces and remove double whitespaces
     * @param input the input string or null (if null, the returned string is also null)
     * @return the changed string
     */
    public static String trimAndRemoveDoubleWhiteSpaces(String input) {

        if (input == null) {
            return null;
        }
        return MULTIPLE_WHITESPACE.matcher(input.trim()).replaceAll(" ");
    }

    /**
     * Trim white spaces, remove double whitespaces, remove non-name characters and reduced dash/whitespace to single dash
     * @param input the input string or null (if null, the returned string is also null)
     * @return the changed string
     */
    public static String trimAndNormalizeName(String input) {

        if (input == null) {
            return null;
        }
        input = removeNonName(input);
        input = trimAndRemoveDoubleWhiteSpaces(input);
        input = WHITESPACE_AND_DASHES.matcher(input).replaceAll("-");
        return input;
    }

    /**
     * Trim white spaces, remove double whitespaces and remove non-printable
     * @param input the input string or null (if null, the returned string is also null)
     * @return the changed string
     */
    public static String trimAndNormalizeText(String input) {

        if (input == null) {
            return null;
        }
        input = removeNonPrintable(input);
        input = trimAndRemoveDoubleWhiteSpaces(input);
        return input;
    }
}
