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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.amadeus.eclipse.toml_editor.plugin.TomlEditorPlugin;

/**
 * A content outline page.
 */
public class TomlContentOutlinePage extends ContentOutlinePage implements ISelectionListener {

    private static final int MAX_DEPTH_LIMIT = 4;

    protected Object fInput;
    protected IDocumentProvider fDocumentProvider;
    protected ITextEditor fTextEditor;

    private static IEclipsePreferences settings = TomlEditorPlugin.getPreferency();

    private boolean expanded = true;

    private Action showFlatAction;
    private Action decreaseLevelAction;
    private Action increaseLevelAction;
    private Action expandAllAction;
    private Action refreshAction;

    /**
     * Creates a content outline page using the given provider and the given editor.
     * 
     * @param provider the document provider
     * @param editor   the editor
     */
    public TomlContentOutlinePage(IDocumentProvider provider, ITextEditor editor) {
        super();
        fDocumentProvider = provider;
        fTextEditor = editor;
    }

    /*
     * (non-Javadoc) Method declared on ContentOutlinePage
     */
    public void createControl(Composite parent) {

        super.createControl(parent);

        TreeViewer viewer = getTreeViewer();
        viewer.setContentProvider(new TomlContentOutlineProvider(fDocumentProvider, fInput));
        viewer.setUseHashlookup(true);
        viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new TomlLabelProvider()));
        viewer.addSelectionChangedListener(this);

        fTextEditor.getSite().getPage().addPostSelectionListener(this);

        if (fInput != null) {
            viewer.setInput(fInput);
            viewer.expandAll();
        }
        addActionsAndToolbar(parent);
    }

    private String getShowFlatActionText() {
        boolean enabled = settings.getBoolean("outline.hierarchy.enabled", true);
        return enabled ? "Show Flat" : "Split Keys";
    }

    private void addActionsAndToolbar(Composite parent) {
        showFlatAction = new Action(getShowFlatActionText(), IAction.AS_CHECK_BOX) {
            public void run() {
                boolean enabled = settings.getBoolean("outline.hierarchy.enabled", true);
                settings.putBoolean("outline.hierarchy.enabled", !enabled);

                showFlatAction.setText(getShowFlatActionText());
                refreshOutlineView();
                getTreeViewer().expandAll();
                expanded = true;
            }
        };
        showFlatAction.setImageDescriptor(getImageDescriptor("icons/parent.gif"));
        showFlatAction.setToolTipText("Enable/Disable outline's keys hierarchy");
        showFlatAction.setChecked(settings.getBoolean("outline.hierarchy.enabled", true));

        increaseLevelAction = new Action("Increase Level") {
            public void run() {
                addHierarchyLevel(1);
            }
        };
        increaseLevelAction.setImageDescriptor(getImageDescriptor("icons/outl_up.gif"));
        increaseLevelAction.setToolTipText("Increase keys split level (up to max " + MAX_DEPTH_LIMIT + ")");

        decreaseLevelAction = new Action("Decrease Level") {
            public void run() {
                addHierarchyLevel(-1);
            }
        };
        decreaseLevelAction.setImageDescriptor(getImageDescriptor("icons/outl_down.gif"));
        decreaseLevelAction.setToolTipText("Decrease keys split level (up to min 0)");

        expandAllAction = new Action("Expand All") {
            public void run() {
                TreeViewer viewer = getTreeViewer();
                if (expanded)
                    viewer.collapseAll();
                else
                    viewer.expandAll(true);
                expanded = !expanded;
            }
        };
        expandAllAction.setImageDescriptor(getImageDescriptor("icons/outl_expand.gif"));
        expandAllAction.setToolTipText("Expand or Collapse All Items");

        refreshAction = new Action("Refresh") {
            public void run() {
                refreshOutlineView();
            }
        };
        refreshAction.setImageDescriptor(getImageDescriptor("icons/outl_refresh.gif"));
        refreshAction.setToolTipText("Refresh content of OutlineView");

        IToolBarManager mgr = getSite().getActionBars().getToolBarManager();
        mgr.removeAll();
        mgr.add(expandAllAction);
        mgr.add(showFlatAction);
        mgr.add(increaseLevelAction);
        mgr.add(decreaseLevelAction);
        mgr.add(refreshAction);
    }

    protected void addHierarchyLevel(int inc) {
        if (!settings.getBoolean("outline.hierarchy.enabled", true))
            return;
        int curLevel = settings.getInt("outline.max_depth", 1);
        curLevel += inc;
        if (curLevel >= 0 && curLevel <= MAX_DEPTH_LIMIT) {
            settings.putInt("outline.max_depth", curLevel);
            refreshOutlineView();
            decreaseLevelAction.setEnabled(true);
            increaseLevelAction.setEnabled(true);
        }
        if (curLevel <= 0)
            decreaseLevelAction.setEnabled(false);
        if (curLevel >= MAX_DEPTH_LIMIT)
            increaseLevelAction.setEnabled(false);
    }

    protected void refreshOutlineView() {
        //getTreeViewer().refresh();
        update();
        // getSite().getActionBars().getToolBarManager().update(true);
    }

    private ImageDescriptor getImageDescriptor(String string) {
        ImageRegistry registry = TomlEditorPlugin.getDefault().getImageRegistry();
        return registry.getDescriptor(string);
    }

    /*
     * (non-Javadoc) Method declared on ContentOutlinePage
     */
    public void selectionChanged(SelectionChangedEvent event) {

        super.selectionChanged(event);

        ISelection selection = event.getSelection();
        if (selection.isEmpty())
            fTextEditor.resetHighlightRange();
        else {
            TomlDocTag element = (TomlDocTag) ((IStructuredSelection) selection).getFirstElement();
            if (element.lineNumber > 0) {
                int len = element.lineLength;
                fTextEditor.resetHighlightRange();
                len = 0; // do not select, just move caret to proper line
                try {
                    fTextEditor.setHighlightRange(element.documentOffset, len, true);
                    fTextEditor.selectAndReveal(element.documentOffset, len);
                } catch (IllegalArgumentException x) {
                    fTextEditor.resetHighlightRange();
                }
            }
        }
    }

    /* ISelectionListener */
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        return;
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
        TreeViewer viewer = getTreeViewer();

        if (viewer != null) {
            Control control = viewer.getControl();
            if (control != null && !control.isDisposed()) {
                control.setRedraw(false);
                viewer.setInput(fInput);
                viewer.expandAll();
                control.setRedraw(true);
                expanded = true;
            }
        }
    }
}
