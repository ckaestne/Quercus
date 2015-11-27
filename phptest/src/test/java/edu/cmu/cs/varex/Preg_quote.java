package edu.cmu.cs.varex; /**
 * source: https://gist.github.com/ferronrsmith/dc3c88fa6e4b0a1edaa0
 */

import jregex.Pattern;
import jregex.PatternSyntaxException;

public class Preg_quote {

    /**
     * Java implementation of preg_quote
     * {@link http://phpjs.org/functions/preg_quote/}
     * @param pStr - string to be REGEX quoted
     * @return quoted string
     */
    public static String preg_quote(String pStr) {
        return pStr.replaceAll("[.\\\\+*?\\[\\^\\]$(){}=!<>|:\\-]", "\\\\$0");
    }

    public static String preg_quote2(String pStr) {
        // discuss at: http://phpjs.org/functions/preg_quote/
        // original by: booeyOH
        // improved by: Ates Goral (http://magnetiq.com)
        // improved by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
        // improved by: Brett Zamir (http://brett-zamir.me)
        // bugfixed by: Onno Marsman
        // example 1: preg_quote("$40");
        // returns 1: '\\$40'
        // example 2: preg_quote("*RRRING* Hello?");
        // returns 2: '\\*RRRING\\* Hello\\?'
        // example 3: preg_quote("\\.+*?[^]$(){}=!<>|:");
        // returns 3:
        // '\\\\\\.\\+\\*\\?\\[\\^\\]\\$\\(\\)\\{\\}\\=\\!\\<\\>\\|\\:'

        return new Pattern("[.\\\\+*?\\[\\^\\]$()\\{\\}=!<>|:\\-]").replacer(
                "\\\\$&").replace(pStr);
        // return "";
    }

    public static boolean validRegex(String pTriggerValue) {
        try {
            new Pattern(pTriggerValue);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

}
