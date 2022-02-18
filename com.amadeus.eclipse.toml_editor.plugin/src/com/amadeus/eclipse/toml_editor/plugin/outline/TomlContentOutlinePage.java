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

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * A content outline page.
 */
public class TomlContentOutlinePage extends ContentOutlinePage {

    protected Object fInput;
    protected IDocumentProvider fDocumentProvider;
    protected ITextEditor fTextEditor;

    
    /**
     * Creates a content outline page using the given provider and the given editor.
     * 
     * @param provider the document provider
     * @param editor the editor
     */
    public TomlContentOutlinePage(IDocumentProvider provider, ITextEditor editor) {
        super();
        fDocumentProvider = provider;
        fTextEditor = editor;
    }
    
    /* (non-Javadoc)
     * Method declared on ContentOutlinePage
     */
    public void createControl(Composite parent) {

        super.createControl(parent);

        TreeViewer viewer= getTreeViewer();
        viewer.setContentProvider(new TomlContentOutlineProvider(fDocumentProvider, fInput));
        viewer.setUseHashlookup(true);
        viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new TomlLabelProvider()));
        viewer.addSelectionChangedListener(this);

        if (fInput != null) {
            viewer.setInput(fInput);
            viewer.expandAll();
        }
    }

    /* (non-Javadoc)
     * Method declared on ContentOutlinePage
     */
    public void selectionChanged(SelectionChangedEvent event) {

        super.selectionChanged(event);

        ISelection selection= event.getSelection();
        if (selection.isEmpty())
            fTextEditor.resetHighlightRange();
        else {
            TomlDocTag element= (TomlDocTag) ((IStructuredSelection) selection).getFirstElement();
            if (element.lineNumber > 0) {
                int len = element.lineLength;
                fTextEditor.resetHighlightRange();
                len   = 0; // do not select, just move caret to proper line
                try {
                    fTextEditor.setHighlightRange(element.documentOffset, len, true);
                    fTextEditor.selectAndReveal(element.documentOffset, len);
                } catch (IllegalArgumentException x) {
                    fTextEditor.resetHighlightRange();
                }
            }
        }
    }
    
    /**
     * Sets the input of the outline page
     * 
     * @param input the input of this outline page
     */
    public void setInput(Object input) {
        fInput = input;
        update();
    }
    
    /**
     * Updates the outline page.
     */
    public void update() {
        TreeViewer viewer= getTreeViewer();

        if (viewer != null) {
            Control control= viewer.getControl();
            if (control != null && !control.isDisposed()) {
                control.setRedraw(false);
                viewer.setInput(fInput);
                viewer.expandAll();
                control.setRedraw(true);
            }
        }
    }
}
