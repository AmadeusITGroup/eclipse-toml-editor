package com.amadeus.eclipse.toml_editor.plugin.rules;

import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class AnyNumberRule extends AbsLineRule {

	private final Token tokenKey;

	private static Pattern PATT_NUM = Pattern.compile("[+-]?[0-9]+([.][0-9]+)?([Ee][+-]?[0-9]+)?");
	private static Pattern PATT_BIN = Pattern.compile("[+-]?0b[01_]+");
	private static Pattern PATT_HEX = Pattern.compile("[+-]?0x[0-9A-Fa-f_]+");
	private static Pattern PATT_OCT = Pattern.compile("[+-]?0o[0-9_]+");

	private static Pattern[] PATTERNS = { PATT_NUM, PATT_BIN, PATT_HEX, PATT_OCT, };

	public AnyNumberRule(Token keyword) {
		this.tokenKey = keyword;
	}

    @Override
    public IToken evaluate(ICharacterScanner scanner) {

        String lstr = readLine(scanner, "#,;\r\n");
        String str = lstr.strip();

        if (!str.isBlank()) {
            for (Pattern patt : PATTERNS) {
                if (patt.matcher(str).matches())
                    return tokenKey;
            }
        }

        // put the scanner back to the original position if no match
        for (int i = 0; i < lstr.length(); i++) {
            scanner.unread();
        }
        return Token.UNDEFINED;
    }
}
