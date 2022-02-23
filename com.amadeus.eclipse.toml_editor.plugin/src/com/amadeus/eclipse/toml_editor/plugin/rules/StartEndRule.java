package com.amadeus.eclipse.toml_editor.plugin.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;

/**
 * SingleLineRule with mandatory start and end sequence.
 * 
 * @author Zbigniew KACPRZAK
 *
 */
public class StartEndRule extends PatternRule {

    public StartEndRule(String startSequenec, String endSequence, Token token) {
        super(startSequenec, endSequence, token, (char) 0, true, false);
    }

    public StartEndRule(String startSequenec, String endSequence, Token token, char escape) {
        super(startSequenec, endSequence, token, escape, true, false);
    }
    
    /**
     * Returns whether the end sequence was detected. As the pattern can be considered
     * ended by a line delimiter, the result of this method is <code>true</code> if the
     * rule breaks on the end of the line, or if the EOF character is read.
     *
     * @param scanner the character scanner to be used
     * @return <code>true</code> if the end sequence has been detected
     */
    @Override
    protected boolean endSequenceDetected(ICharacterScanner scanner) {
        int readCount= 1;
        int c;
        while ((c= scanner.read()) != ICharacterScanner.EOF) {
            readCount++;
            if (c == '\r' || c == '\n') {
                scanner.read();
                if (fBreaksOnEOL)
                    break;
            } else if (c == fEscapeCharacter) {
                scanner.read();
            } else if (fEndSequence.length > 0 && c == fEndSequence[0]) {
                // Check if the specified end sequence has been found.
                if (sequenceDetected(scanner, fEndSequence, fBreaksOnEOF))
                    return true;
            }
        }

        if (fBreaksOnEOF)
            return true;

        for (; readCount > 0; readCount--)
            scanner.unread();

        return false;
    }
}
