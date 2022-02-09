package com.amadeus.eclipse.toml_editor.plugin;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class TomlSourceViewerConfiguration extends TextSourceViewerConfiguration {
    private PresentationReconciler presentationReconciler;
    private IAutoEditStrategy autoEditStrategy;

    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        if (presentationReconciler == null)
            presentationReconciler = new TomlPresentationReconciler();
        return presentationReconciler;
    }

    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        ContentAssistant assistant = new ContentAssistant();

        assistant.setContentAssistProcessor(new TomlContentAssistProcessor(), IDocument.DEFAULT_CONTENT_TYPE);

        assistant.enableAutoInsert(true);
        assistant.enableAutoActivation(true);
        assistant.setAutoActivationDelay(500);
        assistant.setProposalPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
        assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
        // assistant.setContextInformationPopupBackground(colorProvider.getColor(new
        // RGB(0, 0, 0)));

        return assistant;
    }

    @Override
    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
        if (autoEditStrategy == null)
            autoEditStrategy = new TomlAutoEditStrategy();
        return new IAutoEditStrategy[] { autoEditStrategy };
    }

}
