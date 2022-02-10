package com.amadeus.eclipse.toml_editor.plugin.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;

public abstract class AbsLineRule implements IRule {

    public boolean isWhitespace(char c) {
        return c == ' ' || c == '\t';
    }

    public boolean isWhitespace(int c) {
        return c == ' ' || c == '\t';
    }
    
    public boolean isNewLine(String str) {
        return "\r\n".equals(str) || "\n".equals(str) || "\r".equals(str);
    }

    public boolean isNewLine(int c) {
        return c == ICharacterScanner.EOF || c == '\n' || c == '\r';
    }

    public String readLine(ICharacterScanner scanner) {
        return readLine(scanner, "");
    }

    public String readLine(ICharacterScanner scanner, String breakChars) {
        String str = "";
        int c;
        while ((c = scanner.read()) != ICharacterScanner.EOF) {
        	String s = Character.toString((char) c);
            if (breakChars.contains(s)) {
                scanner.unread();
                break;
            }
            str += s;
            if ('\n' == c || '\r' == c) {
                c = scanner.read();
                if ('\n' == c || '\r' == c) {
                    str += Character.toString((char) c);
                } else
                    scanner.unread();
                break;
            }
        }
        return str;
    }
    
    public boolean isFullLine(ICharacterScanner scanner, String line) {
        int count = Math.max(0, line.length());
        for (int i=0; i<=count; i++)
            scanner.unread();

        int c = scanner.read(); scanner.unread();
        boolean newLine = isNewLine(c);
        while (!newLine) {
            count++;
            scanner.unread();
            c = scanner.read();
            scanner.unread();
            newLine = isNewLine(c);
            if (!newLine && !isWhitespace(c)) {
                newLine = false;
                break;
            }
        }
        for (int i=0; i<=count; i++)
            scanner.read();
        return newLine;
    }
}
