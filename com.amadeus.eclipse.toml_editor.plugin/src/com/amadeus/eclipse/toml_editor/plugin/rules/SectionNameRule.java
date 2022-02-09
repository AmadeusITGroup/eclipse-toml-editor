package com.amadeus.eclipse.toml_editor.plugin.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class SectionNameRule extends AbsLineRule {

    private static Pattern PATT_SECTION = Pattern.compile("\\s*\\[\\S+\\]\\s*(#.*)*");
    
    private final Token tokenKey;

    public SectionNameRule(Token key) {
        this.tokenKey = key;
    }

    private boolean isSectionName(String str) {
        String txt = str.stripTrailing();
        Matcher mkvc = PATT_SECTION.matcher(txt);
        return mkvc.matches();
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner) {
        String str = readLine(scanner);
        if (isSectionName(str) && isFullLine(scanner, str)) {
            int cpos = str.indexOf('#');
            if (cpos > 0) {
                // we need to rewind till '#' character, so then scanner is in a good shape
                int pos = str.length() - cpos + 1;
                for (int i = 0; i < pos; i++) {
                    scanner.unread();
                }
            }
            return tokenKey;
        }
        int count = str.length();

        // put the scanner back to the original position if no match
        for (int i = 0; i < count; i++) {
            scanner.unread();
        }

        return Token.UNDEFINED;
    }
}
