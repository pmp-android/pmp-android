package de.unistuttgart.ipvs.pmp.editor.ui.editors.internals;

import java.util.Locale;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

/**
 * Provides a cell editor that enables the user to put in a locale. It provides
 * a autocomplete-text field the shows all available locales while typing.
 * 
 * @author Patrick Strobel
 */
public abstract class LocaleEditingSupport extends EditingSupport {

    // private ComboBoxViewerCellEditor editor;
    private TextCellEditor editor;

    public LocaleEditingSupport(TableViewer viewer) {
	super(viewer);
	editor = new AutocompleteTextCellEditor(viewer.getTable(),
		Locale.getISOLanguages());
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
	return editor;
    }

    @Override
    protected boolean canEdit(Object element) {
	return true;
    }

}
