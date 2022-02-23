package com.amadeus.eclipse.toml_editor.plugin.rules;

import java.util.Arrays;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * @author Zbigniew KACPRZAK
*/
public class KeywordRule extends AbsLineRule {

    private final Token tokenKey;

    public KeywordRule(Token keyword) {
        this.tokenKey = keyword;
    }

    private static final String[] KEYWORDS = {
        "true",
        "false",
        "nan",
        "inf",
    };

    @Override
    public IToken evaluate(ICharacterScanner scanner) {

        String lstr = readLine(scanner, "#\r\n");

        String str = lstr.strip();
        boolean kw = Arrays.stream(KEYWORDS).anyMatch(e -> e.equals(str));
        if (kw)
            return tokenKey;

        // put the scanner back to the original position if no match
        for (int i = 0; i < lstr.length(); i++) {
            scanner.unread();
        }
        return Token.UNDEFINED;
    }
}
