package com.amadeus.eclipse.toml_editor.plugin.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;

/**
 * SingleLineRule with mandatory start and end sequence
 * @author zbigniew.kacprzak
 *
 */
public class StartEndMultilineRule extends PatternRule {

    public StartEndMultilineRule(String startSequenec, String endSequence, Token token) {
        super(startSequenec, endSequence, token, (char) 0, false, true);
    }

    public StartEndMultilineRule(String startSequenec, String endSequence, Token token, char escape) {
        super(startSequenec, endSequence, token, escape, false, true);
    }
    
    /**
     * Returns whether the next characters to be read by the character scanner
     * are an exact match with the given sequence. No escape characters are allowed
     * within the sequence. If specified the sequence is considered to be found
     * when reading the EOF character.
     *
     * @param scanner the character scanner to be used
     * @param sequence the sequence to be detected
     * @return <code>true</code> if the given sequence has been detected
     */
    protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence) {
        for (int i= 1; i < sequence.length; i++) {
            int c= scanner.read();
            if (c == ICharacterScanner.EOF) {
                return true;
            } else if (c != sequence[i]) {
//                if (c=='\r' || c=='\n') {
//                    i--;
//                    continue;
//                }
                // Non-matching character detected, rewind the scanner back to the start.
                // Do not unread the first character.
                scanner.unread();
                for (int j= i-1; j > 0; j--)
                    scanner.unread();
                return false;
            }
        }

        return true;
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
                if (c == '\r') {
                    scanner.read();
                }
            } else if (c == fEscapeCharacter) {
                scanner.read();
            } else if (fEndSequence.length > 0 && c == fEndSequence[0]) {
                // Check if the specified end sequence has been found, ignore EOL.
                if (sequenceDetected(scanner, fEndSequence))
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
