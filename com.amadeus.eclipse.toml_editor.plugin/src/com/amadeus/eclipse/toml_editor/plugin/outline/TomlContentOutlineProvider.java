package com.amadeus.eclipse.toml_editor.plugin.outline;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 */
class TomlContentOutlineProvider implements ITreeContentProvider {
    
    private final static String SEGMENTS = "__toml_editor_segments"; //$NON-NLS-1$
    private IPositionUpdater fPositionUpdater = new DefaultPositionUpdater(SEGMENTS);
    
    private IDocumentProvider fDocumentProvider;
    private Object fInput;
    
    private List<TomlDocTag> fContent = new ArrayList<TomlDocTag>();

    public TomlContentOutlineProvider(IDocumentProvider provider, Object iInput)
    {
        super();
        fDocumentProvider = provider;
        fInput = iInput;
    }
    
    public static int compareTags(TomlDocTag o1, TomlDocTag o2) {
        if (o1.tokenType == o2.tokenType) {
            return o1.tokenValue.compareToIgnoreCase(o2.tokenValue);
        } else {
            return o1.tokenType.compareTo(o2.tokenType);
        }
    }
    
    private List<TomlDocTag> getSortedList(List<TomlDocTag> list) {
        // @formatter:off
        List<TomlDocTag> slist = list.stream()
                                     .sorted(TomlContentOutlineProvider::compareTags)
                                     .collect(Collectors.toList());
        //Collections.sort(list, (o1, o2) -> o1.tokenValue.compareToIgnoreCase(o2.tokenValue));
        // @formatter:on
        return slist;
    }

    /*
     * @see ITreeContentProvider#getChildren(Object)
     */
    public Object[] getChildren(Object element) {
        if (element instanceof TomlDocTag) {
            return getSortedList(((TomlDocTag)element).children).toArray();
        }
        if (element == fInput) {
            return getSortedList(fContent).toArray();
        }
        return new Object[0];
    }

    /*
     * @see ITreeContentProvider#getParent(Object)
     */
    public Object getParent(Object element) {
        if (element instanceof TomlDocTag) {
            return ((TomlDocTag)element).parent;
        }
        return null;
    }

    /*
     * @see ITreeContentProvider#hasChildren(Object)
     */
    public boolean hasChildren(Object element) {
        boolean ret = false;
        if (element instanceof TomlDocTag) {
            ret = ((TomlDocTag)element).children.size() > 0;
        } else
            ret= element == fInput;
        return ret;
    }

    /*
     * @see IStructuredContentProvider#getElements(Object)
     */
    public Object[] getElements(Object element) {
        return getSortedList(fContent).toArray();
    }

    /*
     * @see IContentProvider#inputChanged(Viewer, Object, Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (oldInput != null) {
            IDocument document= fDocumentProvider.getDocument(oldInput);
            if (document != null) {
                try {
                    document.removePositionCategory(SEGMENTS);
                } catch (BadPositionCategoryException x) {}
                document.removePositionUpdater(fPositionUpdater);
            }
        }

        fContent.clear();

        if (newInput != null) {
            IDocument document= fDocumentProvider.getDocument(newInput);
            if (document != null) {
                document.addPositionCategory(SEGMENTS);
                document.addPositionUpdater(fPositionUpdater);
                (new TomlContentOutlineDocumentParser(fContent)).parseDocument(document);
            }
        }
    }

    /*
     * @see IContentProvider#dispose
     */
    public void dispose() {
        if (fContent != null) {
            fContent.clear();
            fContent= null;
        }
    }

    /*
     * @see IContentProvider#isDeleted(Object)
     */
    public boolean isDeleted(Object element) {
        return false;
    }

}

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
