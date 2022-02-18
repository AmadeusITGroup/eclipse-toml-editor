package com.amadeus.eclipse.toml_editor.plugin;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * @author zbigniew.kacprzak
 *
 */
public class TomlContentAssistProcessor implements IContentAssistProcessor {
    public static final List<String> PROPOSALS = Arrays.asList( "true", "false", "today", "tomorrow", "yesterday");

    /**
     * Find first non-word character moving left from current offset
     * @param document
     * @param offset
     * @return
     * @throws BadLocationException
     */
    private int findReplacementOffset(IDocument document, int offset) throws BadLocationException {
        int beginLineOffset = document.getLineOffset(document.getLineOfOffset(offset));

        int repOffset = -1;
        for (int i=offset-1; i >= beginLineOffset; i--) {
            String t = document.get(i, 1);
            if (t.matches("\\W")) {
                repOffset = i + 1;
                break;
            }
        }
        return repOffset;
    }

    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {

        IDocument document = viewer.getDocument();
        try {
            int lineIndex = document.getLineOfOffset(offset);
            int beginLineOffset = document.getLineOffset(lineIndex);

            int lineTextLenght = offset - beginLineOffset;
            String lineText = document.get(beginLineOffset, lineTextLenght).toLowerCase();
            if (!lineText.contains("=")) {
                return new ICompletionProposal[0];
            }

            final int fromOffset = findReplacementOffset(document, offset) ;
            String replText = fromOffset < 0 ? "" : document.get(fromOffset, offset - fromOffset).toLowerCase();

            // @formatter:off
            ICompletionProposal[] props = PROPOSALS.stream()
                    .filter(p -> fromOffset > 0 && p.toLowerCase().startsWith(replText))
                    .map(p -> new CompletionProposal(p, fromOffset, offset - fromOffset, p.length()))
                    .toArray(ICompletionProposal[]::new);
            return props;
            // @formatter:on
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        // TODO this is logic for .project file to complete on nature and project references. Replace with your language logic!
//        String text = viewer.getDocument().get();
//        String natureTag= "<nature>";
//        String projectReferenceTag="<project>";
//        IWorkspace workspace = ResourcesPlugin.getWorkspace();
//        if (text.length() >= natureTag.length() && text.substring(offset - natureTag.length(), offset).equals(natureTag)) {
//            IProjectNatureDescriptor[] natureDescriptors= workspace.getNatureDescriptors();
//            ICompletionProposal[] proposals = new ICompletionProposal[natureDescriptors.length];
//            for (int i= 0; i < natureDescriptors.length; i++) {
//                IProjectNatureDescriptor descriptor= natureDescriptors[i];
//                proposals[i] = new CompletionProposal(descriptor.getNatureId(), offset, 0, descriptor.getNatureId().length());
//            }
//            return proposals;
//        }
//        if (text.length() >= projectReferenceTag.length() && text.substring(offset - projectReferenceTag.length(), offset).equals(projectReferenceTag)) {
//            IProject[] projects= workspace.getRoot().getProjects();
//            ICompletionProposal[] proposals = new ICompletionProposal[projects.length];
//            for (int i= 0; i < projects.length; i++) {
//                proposals[i]=new CompletionProposal(projects[i].getName(), offset, 0, projects[i].getName().length());
//            }
//            return proposals;
//        }
        return new ICompletionProposal[0];
    }

    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return null;
    }

    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";//NON-NLS-1
        return str.toCharArray();
        //return new char[] { '"' }; //NON-NLS-1
    }

    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public IContextInformationValidator getContextInformationValidator() {
        return null;
    }

}