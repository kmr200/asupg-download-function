package org.asupg.downloader.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractorUtil {

    public static String extractPatternFromBody(String body, Pattern pattern, String name) {
        Matcher matcher = pattern.matcher(body);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Response body does not contain " + name);
        }
        return matcher.group(1);
    }

}
