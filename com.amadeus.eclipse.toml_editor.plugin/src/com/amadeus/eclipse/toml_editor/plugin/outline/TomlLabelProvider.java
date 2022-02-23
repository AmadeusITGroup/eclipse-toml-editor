package com.amadeus.eclipse.toml_editor.plugin.outline;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;


/**
 * @author Zbigniew KACPRZAK
*/
class TomlLabelProvider extends LabelProvider implements IColorProvider, IStyledLabelProvider {
    public TomlLabelProvider() {
        super();
    }
    public Image getImage(Object element) {
        return ((TomlDocTag) element).getImage();
    }
    @Override
    public Color getForeground(Object element) {
//        if (element instanceof TomlDocTag) {
//            TomlDocTag el = (TomlDocTag) element;
//            if (el.tokenType == TokenType.SECTION)
//                return Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
//        }
        return null;
    }
    @Override
    public Color getBackground(Object element) {
        return null;
    }

    @Override
    public StyledString getStyledText(Object element) {
        String txt = getText(element);
        if (element instanceof TomlDocTag) {
            TomlDocTag el = (TomlDocTag) element;
            if (el.tokenType != TokenType.PARENT) {
                StyledString sts = new StyledString();
                sts.append(el.tokenValue);
                sts.append("  : " + el.lineNumber, StyledString.DECORATIONS_STYLER);
                return sts;
            }
        }
        return new StyledString(txt, StyledString.COUNTER_STYLER);
    }
    
}
