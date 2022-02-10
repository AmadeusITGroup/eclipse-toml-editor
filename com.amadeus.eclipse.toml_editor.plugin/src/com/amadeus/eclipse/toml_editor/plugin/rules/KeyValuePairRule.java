package com.amadeus.eclipse.toml_editor.plugin.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class KeyValuePairRule extends AbsLineRule {

    private static Pattern PATT_KEY_VALUE = Pattern.compile("\\s*(?<qt>[\"]?)(?<key>[a-zA-Z0-9]\\S*\\k<qt>)\\s*=(?<value>.*)");
    
    private final Token tokenKey;
    private final Token tokenDelim;

    public KeyValuePairRule(Token key, Token delim) {
        this.tokenKey = key;
        this.tokenDelim = delim;
    }

    private boolean isPropertyName(String str) {
        String txt = str.stripTrailing();
        Matcher mkvc = PATT_KEY_VALUE.matcher(txt);
        return mkvc.matches();
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner) {

        String str = readLine(scanner, "#\r\n");
        if (isPropertyName(str)) {
            // we might have: "  tab_before = true"
            // we need to rewind till '=' character, so then scanner is in a good shape
            // we highlight only what left: "  tab_before " - without '='
            int pos = str.length() - str.indexOf('=');
            for (int i = 0; i < pos; i++) {
                scanner.unread();
            }
            return tokenKey;

        } else if (str.startsWith("=")) {
            int pos = str.length() - str.indexOf('=') - 1;
            for (int i = 0; i < pos; i++) {
                scanner.unread();
            }
            return tokenDelim;
        }

        // put the scanner back to the original position if no match
        for (int i = 0; i < str.length(); i++) {
            scanner.unread();
        }

        return Token.UNDEFINED;
    }
}
