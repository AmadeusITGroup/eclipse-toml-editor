package com.amadeus.eclipse.toml_editor.plugin;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * @author Zbigniew KACPRZAK
*/
public class TomlAutoEditStrategy implements IAutoEditStrategy {
    private static final String STRING_BLOCKS[] = {
            "\"\"\"",  // NON-NLS-1
            "'''"      // NON-NLS-1
    };
    private static final String INDENT = "    ";  // NON-NLS-1

    private static final Map<String, AutoEditRule> singleAuto = new HashMap<>();

    static {
        singleAuto.put("\"", new AutoEditRule("\"", "\"", "\\"));  // NON-NLS-1
        singleAuto.put("[",  new AutoEditRule("[", "]"));          // NON-NLS-1
        singleAuto.put("'",  new AutoEditRule("'", "'", "\\"));    // NON-NLS-1
    }

    private String checkStringBlock(String insText, String insLine) {
        for (String sb : STRING_BLOCKS) {
            if (!sb.startsWith(insText))
                continue;
            String fullLine = insLine + insText;
            if (fullLine.endsWith(sb))
                return sb;
        }
        return null;
    }

    @Override
    public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
        String cmdTxt = command.text;
        if ("".equals(cmdTxt) || cmdTxt.isBlank()) { // NON-NLS-1
            return;
        }
        try {
            String NEW_LINE = document.getLineDelimiter(0);
            IRegion region = document.getLineInformationOfOffset(command.offset);
            String lineIns = document.get(region.getOffset(), command.offset - region.getOffset());
            String lineFul = document.get(region.getOffset(), region.getLength());

            String strBlock = checkStringBlock(cmdTxt, lineFul);
            if (strBlock != null) {
                if (!lineFul.endsWith(strBlock)) {
                    command.text += NEW_LINE + INDENT + NEW_LINE + strBlock;
                    command.shiftsCaret = false;
                    command.caretOffset = command.offset + strBlock.length() + INDENT.length() + 1;
                    command.offset = region.getOffset() + region.getLength();
                }
                return;
            }
            if (singleAuto.containsKey(cmdTxt)) {
                AutoEditRule ar = singleAuto.get(command.text);
                if (ar.escape == null || !lineIns.endsWith(ar.escape)) {
                    command.shiftsCaret = false;
                    command.caretOffset = command.offset + 1;
                    command.text += ar.end;
                }
                return;
            }
        } catch (BadLocationException e) {
            System.err.println("BadLocationException: " + e.getMessage());
        }
    }
}

class AutoEditRule {
    String begin;
    String end;
    String escape;

    AutoEditRule(String begin) {
        this(begin, begin, null);
    }
    AutoEditRule(String begin, String end) {
        this(begin, end, null);
    }
    AutoEditRule(String begin, String end, String escape) {
        this.begin = begin;
        this.end = end;
        this.escape = escape;
    }
}