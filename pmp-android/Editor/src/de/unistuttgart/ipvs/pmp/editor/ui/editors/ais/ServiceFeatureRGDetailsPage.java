/*
 * Copyright 2012 pmp-android development team
 * Project: Editor
 * Project-Site: http://code.google.com/p/pmp-android/
 *
 * ---------------------------------------------------------------------
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.unistuttgart.ipvs.pmp.editor.ui.editors.ais;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.unistuttgart.ipvs.pmp.editor.model.Model;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.ais.internals.InputNotEmptyValidator;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.ais.internals.contentprovider.RequiredPSContentProvider;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.ais.internals.dialogs.RequiredPrivacySettingsDialog;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.internals.Images;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.internals.TooltipTableListener;
import de.unistuttgart.ipvs.pmp.editor.xml.AISValidatorWrapper;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.AISRequiredPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.AISRequiredResourceGroup;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAISRequiredPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGISPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.RGIS;

/**
 * Shows the table with the details to the resource groups
 * 
 * @author Thorsten Berberich
 * 
 */
public class ServiceFeatureRGDetailsPage implements IDetailsPage,
		IDoubleClickListener {

	/**
	 * ID of this page
	 */
	public static final String ID = "ais_service_feature_required_rgs";

	/**
	 * Given form
	 */
	private IManagedForm form;

	/**
	 * {@link Shell} of the parent
	 */
	private Shell parentShell;

	/**
	 * Required privacy setting {@link TableViewer}
	 */
	private TableViewer psTableViewer;

	/**
	 * The remove actiond
	 */
	private Action remove;

	/**
	 * Columns of the table
	 */
	private TableColumn identifierColumn;
	private TableColumn valueColumn;

	private AISRequiredResourceGroup displayed;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.forms.IFormPart#initialize(org.eclipse.ui.forms.IManagedForm
	 * )
	 */
	@Override
	public void initialize(IManagedForm arg0) {
		this.form = arg0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createContents(Composite parent) {
		this.parentShell = parent.getShell();

		// Set parent's layout
		GridData parentLayout = new GridData();
		parentLayout.verticalAlignment = GridData.FILL;
		parentLayout.grabExcessVerticalSpace = true;
		parentLayout.horizontalAlignment = GridData.FILL;
		parentLayout.grabExcessHorizontalSpace = true;
		parent.setLayout(new GridLayout(1, false));

		// Attributes section
		FormToolkit toolkit = this.form.getToolkit();

		// The name section
		Section psSection = toolkit.createSection(parent,
				ExpandableComposite.CLIENT_INDENT
						| ExpandableComposite.TITLE_BAR);
		psSection.setText("Required Privacy Setting");
		psSection.setLayout(new GridLayout(1, false));
		psSection.setExpanded(true);
		psSection.setLayoutData(parentLayout);
		createPrivacySettingSectionAttributeToolbar(psSection);

		// Composite that is display in the description section
		Composite psComposite = toolkit.createComposite(psSection);
		psComposite.setLayout(new GridLayout(1, false));
		psComposite.setLayoutData(parentLayout);
		createRGTable(psComposite, toolkit);

		psSection.setClient(psComposite);
	}

	private TableViewer createRGTable(Composite parent, FormToolkit toolkit) {
		// Use grid layout so that the table uses the whole screen width
		final GridData layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.grabExcessVerticalSpace = true;

		// Workaround for SWT-Bug needed
		// (https://bugs.eclipse.org/bugs/show_bug.cgi?id=215997)
		layoutData.widthHint = 1;

		this.psTableViewer = new TableViewer(parent, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		this.psTableViewer.setContentProvider(new RequiredPSContentProvider());
		this.psTableViewer.addDoubleClickListener(this);

		// Disable the default tool tips
		this.psTableViewer.getTable().setToolTipText("");

		TooltipTableListener tooltipListener = new TooltipTableListener(
				this.psTableViewer, this.parentShell);

		this.psTableViewer.getTable().addListener(SWT.Dispose, tooltipListener);
		this.psTableViewer.getTable().addListener(SWT.KeyDown, tooltipListener);
		this.psTableViewer.getTable().addListener(SWT.MouseMove,
				tooltipListener);
		this.psTableViewer.getTable().addListener(SWT.MouseHover,
				tooltipListener);

		this.psTableViewer.getTable().addSelectionListener(
				new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent arg0) {
						if (ServiceFeatureRGDetailsPage.this.psTableViewer
								.getTable().getSelectionCount() > 0) {
							ServiceFeatureRGDetailsPage.this.remove
									.setEnabled(true);
						} else {
							ServiceFeatureRGDetailsPage.this.remove
									.setEnabled(false);
						}
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
					}
				});

		// The identifier column with the LabelProvider
		TableViewerColumn identifierViewerColumn = new TableViewerColumn(
				this.psTableViewer, SWT.NULL);
		identifierViewerColumn.getColumn().setText("Identifier");
		identifierViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((AISRequiredPrivacySetting) element).getIdentifier();
			}

			@Override
			public Image getImage(Object element) {
				AISRequiredPrivacySetting item = (AISRequiredPrivacySetting) element;
				if (!item.getIssues().isEmpty()) {
					return Images.ERROR16;
				}
				return null;
			}
		});
		this.identifierColumn = identifierViewerColumn.getColumn();

		// The value column with the LabelProvider
		TableViewerColumn valueViewerColumn = new TableViewerColumn(
				this.psTableViewer, SWT.NULL);
		valueViewerColumn.getColumn().setText("Value");
		valueViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((AISRequiredPrivacySetting) element).getValue();
			}
		});

		this.valueColumn = valueViewerColumn.getColumn();

		// Define the table's view
		Table psTable = this.psTableViewer.getTable();
		psTable.setLayoutData(layoutData);
		psTable.setHeaderVisible(true);
		psTable.setLinesVisible(true);

		return this.psTableViewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse
	 * .ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IFormPart arg0, ISelection selection) {
		this.psTableViewer.getTable().setRedraw(false);

		// Get the selected service feature and set the name
		TreePath[] path = ((TreeSelection) selection).getPaths();
		this.displayed = (AISRequiredResourceGroup) path[0].getLastSegment();
		this.psTableViewer.setInput(this.displayed);

		// Pack all columns
		this.identifierColumn.pack();
		this.valueColumn.pack();

		this.remove.setEnabled(false);

		this.psTableViewer.getTable().setRedraw(true);
		this.psTableViewer.getTable().redraw();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IFormPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return Model.getInstance().isAisDirty();
	}

	/**
	 * Adds the toolbar with the remove and add buttons to the description
	 * section
	 * 
	 * @param section
	 *            {@link Section} to set the toolbar
	 */
	private void createPrivacySettingSectionAttributeToolbar(Section section) {
		// Create the toolbar
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(section);

		final Cursor handCursor = new Cursor(Display.getCurrent(),
				SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);
		toolbar.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if ((handCursor != null) && (handCursor.isDisposed() == false)) {
					handCursor.dispose();
				}
			}
		});

		// Picture can be added also to the actions
		Action add = new Action("Add") {

			@Override
			public void run() {
				// Flag if an error happend while downloading
				Boolean error = false;
				RGIS resGroup = null;

				// Get the resource groups from the server
				List<RGIS> rgList = Model.getInstance().getRgisList(
						ServiceFeatureRGDetailsPage.this.parentShell);
				if (rgList != null) {
					for (RGIS rgis : rgList) {
						if (rgis.getIdentifier().equals(
								ServiceFeatureRGDetailsPage.this.displayed
										.getIdentifier())) {
							resGroup = rgis;
						}
					}
				} else {
					error = true;
				}

				// Build a custom RGIS with the privacy settings that are
				// not set yet
				RGIS customRGIS = resGroup;

				// Check if there are RGs from the server
				if (resGroup != null) {
					HashMap<String, IRGISPrivacySetting> privacySettings = new HashMap<String, IRGISPrivacySetting>();

					// Iterate through all available PSs
					for (IRGISPrivacySetting ps : resGroup.getPrivacySettings()) {
						privacySettings.put(ps.getIdentifier(), ps);
					}

					/*
					 * Iterate through the set of required Privacy Settings and
					 * delete the ones that are already added
					 */
					for (IAISRequiredPrivacySetting requiredPS : ServiceFeatureRGDetailsPage.this.displayed
							.getRequiredPrivacySettings()) {
						if (privacySettings.containsKey(requiredPS
								.getIdentifier())) {
							customRGIS.removePrivacySetting(privacySettings
									.get(requiredPS.getIdentifier()));
						}

					}

					// If there are some PS to add
					if (!customRGIS.getPrivacySettings().isEmpty()) {

						// Open the dialog
						SelectionDialog dialog = new RequiredPrivacySettingsDialog(
								ServiceFeatureRGDetailsPage.this.parentShell,
								customRGIS);

						// Get the results
						if (dialog.open() == Window.OK
								&& dialog.getResult().length > 0) {

							// Store them at the model
							for (Object object : dialog.getResult()) {
								AISRequiredPrivacySetting rps = (AISRequiredPrivacySetting) object;
								ServiceFeatureRGDetailsPage.this.displayed
										.addRequiredPrivacySetting(rps);

								AISValidatorWrapper.getInstance()
										.validateRequiredPrivacySetting(rps,
												true);
							}

							AISValidatorWrapper
									.getInstance()
									.validateRequiredResourceGroup(
											ServiceFeatureRGDetailsPage.this.displayed,
											true);
							AISValidatorWrapper.getInstance()
									.validateServiceFeatures(
											Model.getInstance().getAis(), true);

							ServiceFeatureMasterBlock.refreshTree();
							// Update the view
							ServiceFeatureRGDetailsPage.this.psTableViewer
									.refresh();
							ServiceFeatureRGDetailsPage.this.psTableViewer
									.getTable().setRedraw(false);
							ServiceFeatureRGDetailsPage.this.identifierColumn
									.pack();
							ServiceFeatureRGDetailsPage.this.valueColumn.pack();
							ServiceFeatureRGDetailsPage.this.psTableViewer
									.getTable().redraw();
							ServiceFeatureRGDetailsPage.this.psTableViewer
									.getTable().setRedraw(true);
							Model.getInstance().setAISDirty(true);
						}

						// There are no PS to add to this service feature
					} else {
						MessageDialog
								.openInformation(
										ServiceFeatureRGDetailsPage.this.parentShell,
										"No Privacy Settings to add",
										"You already added all Privacy Settings of this Resource Group");
					}
				} else {
					/*
					 * The Resource group wasn't found at the server and no
					 * error happen while downloading them, show the
					 * corresponding message
					 */
					if (!error) {
						MessageDialog
								.openInformation(
										ServiceFeatureRGDetailsPage.this.parentShell,
										"Unknown Resource Group",
										"This Resource Group was not found at the Resource Group server.\n"
												+ "Therefore you can not add a Privacy Setting");
					}
				}

			}
		};
		add.setToolTipText("Add a new required Privacy Setting for the Service Feature");

		// The remove action
		this.remove = new Action("Remove") {

			@Override
			public void run() {
				TableItem[] selected = ServiceFeatureRGDetailsPage.this.psTableViewer
						.getTable().getSelection();
				for (TableItem item : selected) {
					AISRequiredPrivacySetting ps = (AISRequiredPrivacySetting) item
							.getData();
					ServiceFeatureRGDetailsPage.this.displayed
							.removeRequiredPrivacySetting(ps);
				}

				AISValidatorWrapper.getInstance()
						.validateRequiredResourceGroup(
								ServiceFeatureRGDetailsPage.this.displayed,
								true);
				AISValidatorWrapper.getInstance().validateServiceFeatures(
						Model.getInstance().getAis(), true);

				ServiceFeatureMasterBlock.refreshTree();

				// Update the view
				ServiceFeatureRGDetailsPage.this.psTableViewer.refresh();
				ServiceFeatureRGDetailsPage.this.psTableViewer.getTable()
						.setRedraw(false);
				ServiceFeatureRGDetailsPage.this.identifierColumn.pack();
				ServiceFeatureRGDetailsPage.this.valueColumn.pack();
				ServiceFeatureRGDetailsPage.this.psTableViewer.getTable()
						.redraw();
				ServiceFeatureRGDetailsPage.this.psTableViewer.getTable()
						.setRedraw(true);

				Model.getInstance().setAISDirty(true);
			}
		};
		this.remove
				.setToolTipText("Remove the selected required Privacy Setting");
		this.remove.setEnabled(false);

		// Add the actions to the toolbar
		toolBarManager.add(add);
		toolBarManager.add(this.remove);

		toolBarManager.update(true);
		section.setTextClient(toolbar);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse
	 * .jface.viewers.DoubleClickEvent)
	 */
	@Override
	public void doubleClick(DoubleClickEvent arg0) {
		int selectionCount = this.psTableViewer.getTable().getSelectionCount();
		if (selectionCount == 1) {
			RGIS resGroup = null;

			if (Model.getInstance().isRGListAvailable()) {
				// Get the resource groups from the server
				List<RGIS> rgList = Model.getInstance().getRgisList(
						this.parentShell);
				if (rgList != null) {
					for (RGIS rgis : rgList) {
						if (rgis.getIdentifier().equals(
								this.displayed.getIdentifier())) {
							resGroup = rgis;
						}
					}
				}
			}
			AISRequiredPrivacySetting selected = (AISRequiredPrivacySetting) this.psTableViewer
					.getTable().getSelection()[0].getData();

			String requiredValues = null;

			if (resGroup != null) {
				for (IRGISPrivacySetting ps : resGroup.getPrivacySettings()) {
					if (ps.getIdentifier().equals(selected.getIdentifier())) {
						requiredValues = ps.getValidValueDescription();
						break;
					}
				}
			}

			String message;
			if (requiredValues != null) {
				message = "Enter the value of the required Privacy Setting \""
						+ selected.getIdentifier() + "\" \n Valid values are: "
						+ requiredValues;
			} else {
				message = "Enter the value of the required Privacy Setting \""
						+ selected.getIdentifier() + "\"";
			}

			// Show the input dialog
			InputDialog dialog = new InputDialog(this.parentShell,
					"Change the value of the required Privacy Setting",
					message, selected.getValue(), new InputNotEmptyValidator(
							"Value"));

			if (dialog.open() == Window.OK) {
				String result = dialog.getValue();

				if (!result.equals(selected.getValue())) {
					selected.setValue(result);

					AISValidatorWrapper.getInstance()
							.validateRequiredPrivacySetting(selected, true);
					AISValidatorWrapper
							.getInstance()
							.validateRequiredResourceGroup(this.displayed, true);
					AISValidatorWrapper.getInstance().validateServiceFeatures(
							Model.getInstance().getAis(), true);

					ServiceFeatureMasterBlock.refreshTree();

					// Update the view
					this.psTableViewer.refresh();
					this.psTableViewer.getTable().setRedraw(false);
					this.identifierColumn.pack();
					this.valueColumn.pack();
					this.psTableViewer.getTable().redraw();
					this.psTableViewer.getTable().setRedraw(true);

					Model.getInstance().setAISDirty(true);
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IFormPart#commit(boolean)
	 */
	@Override
	public void commit(boolean arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IFormPart#dispose()
	 */
	@Override
	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IFormPart#isStale()
	 */
	@Override
	public boolean isStale() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IFormPart#refresh()
	 */
	@Override
	public void refresh() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IFormPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
	 */
	@Override
	public boolean setFormInput(Object arg0) {
		return false;
	}
}
