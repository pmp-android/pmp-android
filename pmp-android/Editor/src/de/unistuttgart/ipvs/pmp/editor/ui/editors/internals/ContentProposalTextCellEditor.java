package de.unistuttgart.ipvs.pmp.editor.ui.editors.internals;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

public class ContentProposalTextCellEditor extends TextCellEditor {

    private ContentProposalAdapter contentProposalAdapter;
    private boolean popupOpen = false; // true, iff popup is currently open

    public ContentProposalTextCellEditor(Composite parent,
	    IContentProposalProvider contentProposalProvider,
	    KeyStroke keyStroke, char[] autoActivationCharacters) {
	super(parent);

	enableContentProposal(contentProposalProvider, keyStroke,
		autoActivationCharacters);
    }

    private void enableContentProposal(
	    IContentProposalProvider contentProposalProvider,
	    KeyStroke keyStroke, char[] autoActivationCharacters) {
	contentProposalAdapter = new ContentProposalAdapter(this.getControl(),
		new TextContentAdapter(), contentProposalProvider, keyStroke,
		autoActivationCharacters);

	// Listen for popup open/close events to be able to handle focus events
	// correctly
	contentProposalAdapter
		.addContentProposalListener(new IContentProposalListener2() {

		    @Override
		    public void proposalPopupClosed(
			    ContentProposalAdapter adapter) {
			popupOpen = false;
		    }

		    @Override
		    public void proposalPopupOpened(
			    ContentProposalAdapter adapter) {
			popupOpen = true;
		    }
		});
    }

    /**
     * Return the {@link ContentProposalAdapter} of this cell editor.
     * 
     * @return the {@link ContentProposalAdapter}
     */
    public ContentProposalAdapter getContentProposalAdapter() {
	return contentProposalAdapter;
    }

    @Override
    protected void focusLost() {
	if (!popupOpen) {
	    // Focus lost deactivates the cell editor.
	    // This must not happen if focus lost was caused by activating
	    // the completion proposal popup.
	    super.focusLost();
	}
    }

    @Override
    protected boolean dependsOnExternalFocusListener() {
	// Always return false;
	// Otherwise, the ColumnViewerEditor will install an additional focus
	// listener
	// that cancels cell editing on focus lost, even if focus gets lost due
	// to
	// activation of the completion proposal popup. See also bug 58777.
	return false;
    }

}
