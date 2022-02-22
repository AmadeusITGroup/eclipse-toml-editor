/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.amadeus.eclipse.toml_editor.plugin.outline;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;

import com.amadeus.eclipse.toml_editor.plugin.TomlEditorPlugin;

/**
 */
class TomlContentOutlineDocumentParser {
    
    private List<TomlDocTag> fContent;

    TomlContentOutlineDocumentParser(List<TomlDocTag> iContent) {
        fContent = iContent;
    }

    void parseDocument(IDocument document) {
        String blockOne = "\"\"\"";
        String blockTwo = "'''";

        Pattern pattSection = Pattern.compile("\\s*\\[(?<name>[^\\[\\]]+)\\]\\s*");
        Pattern pattKeyVal = Pattern.compile("\\s*(?<qt>[\\\"]?)(?<key>[a-zA-Z][^=]+)\\k<qt>\\s*=\\s*(?<value>.*)");
        Pattern pattFullBlock = Pattern.compile(".*(?<fblk>('|\"){3}).*\\k<fblk>\\s*");
        Pattern pattBlock = Pattern.compile(".*(?<blk>('|\"){3}).*");

        //System.out.println("\n**** START parseDocument");        
        TomlDocTag aCurrentSection = null;
        String blockFound = "";
        for (int i=0; i < document.getNumberOfLines(); i++) {
            try {
                int ofs = document.getLineOffset(i);
                int len = document.getLineLength(i);
                String aLine = document.get(ofs, len).replaceFirst("#.*", "").stripTrailing();
                if (aLine.isBlank())
                    continue;
                if (!blockFound.isBlank()) {
                    if (aLine.strip().endsWith(blockFound))
                        blockFound = "";
                    continue;
                }
                TomlDocTag aTag = null;
                Matcher msec = pattSection.matcher(aLine);
                Matcher mkvc = pattKeyVal.matcher(aLine);
                if (msec.matches()) {
                    aTag = new TomlDocTag(TokenType.SECTION, msec.group("name"), i+1, ofs, len);
                } else if (mkvc.matches()) {
                    String key = mkvc.group("key");
                    String value = mkvc.group("value");
                    aTag = new TomlDocTag(TokenType.KEY, key, i+1, ofs, len);

                    // skip next lines when multiline block-string starts
                    if (value.contains(blockOne) || value.contains(blockTwo)) {
                        Matcher fblk = pattFullBlock.matcher(value);
                        if (!fblk.matches()) {
                            Matcher blk = pattBlock.matcher(value);
                            if (blk.matches()) {
                                blockFound = blk.group("blk");
                            }
                        }
                    }
                } else {
                    continue; // line ignored
                }
                if (aTag != null) {
                    switch (aTag.tokenType) {
                        case SECTION:
                            aCurrentSection = aTag;
                            fContent.add(aTag);
                            break;
                        case KEY:
                            TomlDocTag aParent = aCurrentSection;//findAndCheckParent(aTag, aCurrentSection);
                            aTag.parent = aParent;
                            if (aParent != null)
                                aParent.children.add(aTag);
                            else
                                fContent.add(aTag);
                            break;
                        default:
                            break;
                    }
                }
            } catch (BadLocationException e) {
            }
        } // for
    } // parseDocument
}

/**
 * A segment element.
 */
enum TokenType {
    KEY,
    SECTION,
    PARENT
};
class TomlDocTag {

    ArrayList<TomlDocTag> children = new ArrayList<TomlDocTag>();
    TomlDocTag parent;

    TokenType tokenType;
    String tokenValue;
    int lineNumber = -1;
    int lineLength = -1;
    int documentOffset = -1;
    Image image;

    TomlDocTag(TokenType iTokType, String iToken) {
        this(iTokType, iToken, -1, -1, -1);
    }

    TomlDocTag(TokenType iTokType, String iToken, int iLineNo, int iDocOffset, int iLineLength) {
        
        tokenValue = iToken;
        tokenType = iTokType;
        lineNumber = iLineNo;
        lineLength = iLineLength;
        documentOffset = iDocOffset;
        image = getTokenTypeImage();
    }
        
    TomlDocTag(TomlDocTag tag) { // copy constructor
        tokenValue = tag.tokenValue;
        tokenType = tag.tokenType;
        lineNumber = tag.lineNumber;
        lineLength = tag.lineLength;
        documentOffset = tag.documentOffset;
        image = tag.image;
    }
    

    public String toString() {
        String ret = tokenValue;
        if (tokenType != TokenType.PARENT)
            ret += "   [" + lineNumber + "]";
        return ret;
    }
    
    public Image getImage() {
        if (tokenType == TokenType.KEY && children.size() > 0) {
            ImageRegistry registry = TomlEditorPlugin.getDefault().getImageRegistry();
            return registry.get("icons/parent.gif");
        }
        return image;
    }

    private Image getTokenTypeImage() {
        String aImageName = null;
        if (tokenType == TokenType.SECTION)
            aImageName = "icons/outl_section.gif";
        if (tokenType == TokenType.KEY)
            aImageName = "icons/outl_key.gif";
        if (tokenType == TokenType.PARENT)
            aImageName = "icons/parent.gif";
        if (aImageName != null) {
            ImageRegistry registry = TomlEditorPlugin.getDefault().getImageRegistry();
            return registry.get(aImageName);
        }
        return null;
    }
}
