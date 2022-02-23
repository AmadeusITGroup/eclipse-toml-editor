package com.amadeus.eclipse.toml_editor.plugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.amadeus.eclipse.toml_editor.plugin.outline.TomlContentOutlinePage;

/**
 * @author Zbigniew KACPRZAK
*/
public class TomlTextEditor extends TextEditor implements IPropertyChangeListener {

    TomlContentOutlinePage outlinePage;

    public TomlTextEditor() {
        super();
        setSourceViewerConfiguration(new TomlSourceViewerConfiguration());
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
    }

    @Override
    public void dispose() {
        if (outlinePage != null)
            outlinePage.setInput(null);
        super.dispose();
    }

    @Override
    public void doSetInput(IEditorInput input) throws CoreException {
        super.doSetInput(input);
        if (outlinePage != null)
            outlinePage.setInput(input);
    }

    @Override
    public void doRevertToSaved() {
        super.doRevertToSaved();
        update();
    }
    
    @Override
    public void doSave(IProgressMonitor progressMonitor) {
        super.doSave(progressMonitor);
        update();
    }

    @Override
    public void doSaveAs() {
        super.doSaveAs();
        update();
    }

    @Override
    protected void editorSaved() {
        super.editorSaved();
        if (outlinePage != null)
            outlinePage.update();    
    }

    protected void update() {
        if (outlinePage != null)
            outlinePage.update();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Object getAdapter(Class required) {
        if (IContentOutlinePage.class.equals(required)) {
            if (outlinePage == null) {
                outlinePage= new TomlContentOutlinePage(getDocumentProvider(), this);
                if (getEditorInput() != null)
                    outlinePage.setInput(getEditorInput());
            }
            return outlinePage;
        }
        return super.getAdapter(required);
    }
}
