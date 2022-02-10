package com.amadeus.eclipse.toml_editor.plugin.rules;

import java.time.format.DateTimeFormatter;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class TimestampRule extends AbsLineRule {

    private final Token tokenKey;

    public TimestampRule(Token keyword) {
        this.tokenKey = keyword;
    }

    @SuppressWarnings("serial")
    private static final SortedMap<String, String> DATE_FORMAT_REGEX = new TreeMap<>() {{
        // patterns are not perfect, we need them only to predict. Later on we parse, so we know exactly what we have
        // @formater:off
        put("^\\d{2}:\\d{2}$",                                          "HH:mm");
        put("^\\d{2}:\\d{2}:\\d{2}$",                                   "HH:mm:ss");
        put("^\\d{2}:\\d{2}:\\d{2}\\.\\d{3}$",                          "HH:mm:ss.SSS");
        put("^\\d{2}:\\d{2}:\\d{2}\\.\\d{6}$",                          "HH:mm:ss.SSSSSS");

        put("^\\d{4}-\\d{1,2}-\\d{1,2}$",                               "yyyy-MM-dd");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$",             "yyyy-MM-dd HH:mm");
        put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$",      "yyyy-MM-dd HH:mm:ss");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}$",                               "yyyy/MM/dd");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$",             "yyyy/MM/dd HH:mm");
        put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$",      "yyyy/MM/dd HH:mm:ss");

        put("^\\d{1,2}-\\d{1,2}-\\d{4}$",                               "dd-MM-yyyy");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$",             "dd-MM-yyyy HH:mm");
        put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",      "dd-MM-yyyy HH:mm:ss");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$",                           "dd MMM yyyy");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$",         "dd MMM yyyy HH:mm");
        put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",  "dd MMM yyyy HH:mm:ss");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$",                          "dd MMMM yyyy");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$",        "dd MMMM yyyy HH:mm");
        put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");

        put("^\\d{1,2}/\\d{1,2}/\\d{4}$",                               "MM/dd/yyyy");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$",             "MM/dd/yyyy HH:mm");
        put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",      "MM/dd/yyyy HH:mm:ss");
        // @formater:on
    }};

    /**
     * Determine DateTimeFormatter pattern matching with the given date string.
     * 
     * @param sDate The date string to determine the SimpleDateFormat pattern for.
     * @return The matching DateTimeFormatter pattern.
     * @see DateTimeFormatter
     */
    private static DateTimeFormatter determineDateFormat(String sDate) {
        for (String regexp : DATE_FORMAT_REGEX.keySet()) {
            if (sDate.toLowerCase().matches(regexp)) {
                return DateTimeFormatter.ofPattern(DATE_FORMAT_REGEX.get(regexp));
            }
        }
        return DateTimeFormatter.ISO_DATE_TIME; // unknown format? let's assume ISO
    }
    
    @Override
    public IToken evaluate(ICharacterScanner scanner) {

        String lstr = readLine(scanner, "#,;\r\n");
        String str = lstr.strip();

        if (!str.isBlank()) {
            // timestamps
            try {
                DateTimeFormatter formatter = determineDateFormat(str);
                formatter.parse(str);
                return tokenKey;
            } catch(Exception ex) {
            }
        }

        // put the scanner back to the original position if no match
        for (int i = 0; i < lstr.length(); i++) {
            scanner.unread();
        }
        return Token.UNDEFINED;
    }
}
