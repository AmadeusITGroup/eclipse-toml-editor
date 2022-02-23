package com.amadeus.eclipse.toml_editor.plugin.outline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.amadeus.eclipse.toml_editor.plugin.TomlEditorPlugin;

/**
 * @author Zbigniew KACPRZAK
*/
class TomlContentOutlineProvider implements ITreeContentProvider {

    private final static String SEGMENTS = "__toml_editor_segments"; //$NON-NLS-1$
    private IPositionUpdater fPositionUpdater = new DefaultPositionUpdater(SEGMENTS);

    private IDocumentProvider fDocumentProvider;
    private Object fInput;

    /**
     * Original content - from document parsing. Only basic hierarchy (section ->
     * items)
     */
    private List<TomlDocTag> fContent = new ArrayList<TomlDocTag>();

    private List<TomlDocTag> fShuffledContent = new ArrayList<TomlDocTag>();

    private Map<String, TomlDocTag> fParentsMap = new HashMap<>();
    private static IEclipsePreferences settings = TomlEditorPlugin.getPreferency();

    public TomlContentOutlineProvider(IDocumentProvider provider, Object iInput) {
        super();
        fDocumentProvider = provider;
        fInput = iInput;
    }

    private void debug(String msg) {
        //System.out.println(msg);
    }
    
    public static int compareTags(TomlDocTag o1, TomlDocTag o2) {
        return o1.tokenValue.compareToIgnoreCase(o2.tokenValue);
//        if (o1.tokenType == o2.tokenType) {
//            return o1.tokenValue.compareToIgnoreCase(o2.tokenValue);
//        } else {
//            return o1.tokenType.compareTo(o2.tokenType);
//        }
    }

    private List<TomlDocTag> getSortedList(List<TomlDocTag> list) {
        debug("  -- Provider.getSortedList; " + list);
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
            // debug(" ** Provider.getChildren");
            return getSortedList(((TomlDocTag) element).children).toArray();
        }
        if (element == fInput) {
            debug("!!**!! Provider.getChildren; element == fInput");
            return getSortedList(fContent).toArray();
        }
        return new Object[0];
    }

    /*
     * @see ITreeContentProvider#getParent(Object)
     */
    public Object getParent(Object element) {
        if (element instanceof TomlDocTag) {
            debug("%% Provider.getParent");
            return ((TomlDocTag) element).parent;
        }
        return null;
    }

    /*
     * @see ITreeContentProvider#hasChildren(Object)
     */
    public boolean hasChildren(Object element) {
        boolean ret = false;
        if (element instanceof TomlDocTag) {
            // debug(" == Provider.hasChildren [instanceof TomlDocTag]");
            ret = ((TomlDocTag) element).children.size() > 0;
        } else {
            debug("  == Provider.hasChildren [fInput]");
            ret = element == fInput;
        }
        return ret;
    }

    /*
     * @see IStructuredContentProvider#getElements(Object)
     */
    public Object[] getElements(Object element) {
        debug("^^ Provider.getElements");
        buildElementsHierarchy();
        return getSortedList(fShuffledContent).toArray();
    }

    /*
     * @see IContentProvider#inputChanged(Viewer, Object, Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        debug("@@ Provider.inputChanged");
        if (oldInput != null) {
            IDocument document = fDocumentProvider.getDocument(oldInput);
            if (document != null) {
                try {
                    document.removePositionCategory(SEGMENTS);
                } catch (BadPositionCategoryException x) {
                }
                document.removePositionUpdater(fPositionUpdater);
            }
        }

        fContent.clear();
        fShuffledContent.clear();
        fParentsMap.clear();

        if (newInput != null) {
            IDocument document = fDocumentProvider.getDocument(newInput);
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
        debug("### Provider.dispose");
        if (fContent != null) {
            fContent.clear();
            fContent = null;
        }
        if (fShuffledContent != null) {
            fShuffledContent.clear();
            fShuffledContent = null;
        }
        if (fParentsMap != null) {
            fParentsMap.clear();
            fParentsMap = null;
        }
    }

    /*
     * @see IContentProvider#isDeleted(Object)
     */
    public boolean isDeleted(Object element) {
        return false;
    }

    private boolean getOutlineHierarchy() {
        boolean ret = settings.getBoolean("outline.hierarchy.enabled", true);
        // debug("outline.hierarchy.enabled: " + ret);
        return ret;
    }

    private int getMaxDepth() {
        // TomlEditorPlugin.getPreferenceStore().
        int iDepth = settings.getInt("outline.max_depth", 1);
        // debug("outline.max_depth: " +iDepth);
        return iDepth;
    }

    private String getParentName(String aTagName) {
        String out = "";
        String names[] = aTagName.split("\\.");
        for (int i = 0; i < names.length - 1; i++) { // all but last one
            if (out.length() > 0)
                out += ".";
            out += names[i];
        }
        return out;
    }

    private void buildElementsHierarchy() {
        fShuffledContent.clear();
        fParentsMap.clear();
        if (!getOutlineHierarchy()) {
            fShuffledContent.addAll(fContent);
            debug(" ^^ Provider.buildElementsHierarchy; fShuffledContent.size: " + fShuffledContent.size());
            return;
        }
        
        // rebuild hierarchy here
        debug(" ^^ Provider.buildElementsHierarchy");
        TomlDocTag aCurrentSection = null;
        for (TomlDocTag tag : fContent) {
            TomlDocTag aTagP = new TomlDocTag(tag);
            if (addTag(aTagP, aCurrentSection))
                aCurrentSection = aTagP;
            for (TomlDocTag ctag : tag.children) {
                TomlDocTag aTagC = new TomlDocTag(ctag);
                addTag(aTagC, aCurrentSection);
            }
        }
    }

    private boolean addTag(TomlDocTag aTag, TomlDocTag aCurrentSection) {
        switch (aTag.tokenType) {
        case SECTION:
            aCurrentSection = aTag;
            fShuffledContent.add(aTag);
            return true;
        case KEY:
            TomlDocTag aParent = findAndCheckParent(aTag, aCurrentSection);
            aTag.parent = aParent;
            if (aParent != null)
                aParent.children.add(aTag);
            else
                fShuffledContent.add(aTag);
            break;
        default:
            break;
        }
        return false;
    }

    private TomlDocTag findAndCheckParent(TomlDocTag aTag, TomlDocTag aCurrentSection) {
        // this method was moved from DocumentPrser, so we can here shuffle hierarchy on demand
        // and based on properties stored - no need to change document

        if (!getOutlineHierarchy())
            return aCurrentSection;

        /*
         * When outline_structured is false, items are assigned only to sections.
         * Otherwise ewe split items on '.', and each part if potential parent
         * (TokenType.PARENT) By default we split only to depth-level 1 - otherwise view
         * is too chaotic when we have items with lots of segments.
         * 
         * Example: one.two.three.four.enabled = true one.two.three.four.five.item =
         * true
         *
         * - outline_structured: true - depth level inactive: -1 one |_ two |_ three |_
         * four |_ one.two.three.four.enabled |_ five |_ one.two.three.four.five.item -
         * depth level active: set to 1 one |_ one.two.three.four.enabled |_
         * one.two.three.four.five.item
         */
        TomlDocTag aPrevTag = aCurrentSection != null ? aCurrentSection : null;
        String pmapkey = aCurrentSection == null ? "DEFAULT|" : aCurrentSection.tokenValue + "|";
        String kname = "";
        String names[] = aTag.tokenValue.split("\\.");
        if (names.length > 1) {
            String pname = pmapkey + getParentName(aTag.tokenValue);
            if (!fParentsMap.containsKey(pname)) {
                for (int i = 0; i < names.length - 1; i++) { // all but last one
                    if (i == getMaxDepth())
                        break;
                    String name = names[i];
                    kname = kname.length() > 0 ? kname + "." + name : name;
                    if (fParentsMap.containsKey(pmapkey + kname)) {
                        aPrevTag = fParentsMap.get(pmapkey + kname);
                        continue;
                    }
                    TomlDocTag tag = new TomlDocTag(TokenType.PARENT, name);
                    aTag.parent = aPrevTag;
                    if (aPrevTag != null)
                        aPrevTag.children.add(tag);
                    else
                        fShuffledContent.add(tag);
                    aPrevTag = tag;
                    fParentsMap.put(pmapkey + kname, tag);
                }
            }
            if (fParentsMap.containsKey(pname))
                return fParentsMap.get(pname);
            if (aPrevTag != null)
                return aPrevTag;
        }
        return aCurrentSection;
    }

}
