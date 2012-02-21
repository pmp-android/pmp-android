package de.unistuttgart.ipvs.pmp.editor.ui.editors.internals;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;

public abstract class ColumnViewerSorter extends ViewerComparator {

	private int direction = SWT.NONE;

	public ColumnViewerSorter(final TableViewer tableViewer, TableViewerColumn columnViewer) {

		final TableColumn column = columnViewer.getColumn();
		column.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Switch direction
				switch (direction) {
				case SWT.NONE:
					direction = SWT.UP;
					break;
				case SWT.UP:
					direction = SWT.DOWN;
					break;
				case SWT.DOWN:
					direction = SWT.None;
					break;

				}
				
				// Mark column used for selection and show direction-arrow
				column.getParent().setSortColumn(column);
				column.getParent().setSortDirection(direction);

				// Set comparator and refresh table
				tableViewer.setComparator(ColumnViewerSorter.this);
				tableViewer.refresh();				

			}
		});
		
		
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (direction == SWT.NONE) {
			return 0;
		}
		
		int compared = doCompare(viewer, e1, e2);
		
		if (direction == SWT.DOWN) {
			compared *= -1;
		}
		
		return compared;
	}
	
	public abstract int doCompare(Viewer viewer, Object e1, Object e2);
}