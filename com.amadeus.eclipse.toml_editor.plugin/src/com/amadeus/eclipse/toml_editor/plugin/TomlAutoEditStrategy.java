package com.amadeus.eclipse.toml_editor.plugin;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class TomlAutoEditStrategy implements IAutoEditStrategy {
    private static final String STRING_BLOCK = "\"\"\"";

    @Override
    public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
        if (!"\"".equals(command.text)) { //NON-NLS-1
            return;
        }
        try {
            String NEW_LINE = document.getLineDelimiter(0);
            IRegion region = document.getLineInformationOfOffset(command.offset);
            String line = document.get(region.getOffset(), command.offset - region.getOffset());
            if (line.startsWith("\""))
                return;
            if (!line.endsWith("\"\""))
                return;
            command.text += NEW_LINE + NEW_LINE + STRING_BLOCK;
//            command.caretOffset = command.offset;
//            command.text += NEW_LINE + "" + command.offset + "|" + command.caretOffset + "|" + command.shiftsCaret;
//            int index = line.lastIndexOf('<');
//            if (index != -1 && (index != line.length() - 1) && line.charAt(index + 1) != '/') {
//                String tag = line.substring(index + 1);
//                command.text += "</" + tag + command.text; //NON-NLS-1
//            }
        } catch (BadLocationException e) {
            // ignore
        }
    }

}
