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
package de.unistuttgart.ipvs.pmp.editor.ui.editors.rgis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.unistuttgart.ipvs.pmp.editor.model.RgisModel;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.internals.Images;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.internals.localetable.LocaleTable;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.internals.tooltips.TooltipTreeListener;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.rgis.internal.ChangeDescriptionString;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.rgis.internal.DescriptionString;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.rgis.internal.NameString;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.rgis.internal.PrivacySettingsContentProvider;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.rgis.internal.PrivacySettingsLabelProvider;
import de.unistuttgart.ipvs.pmp.editor.xml.IssueTranslator;
import de.unistuttgart.ipvs.pmp.editor.xml.RGISValidatorWrapper;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGIS;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.RGISPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IIssue;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IssueType;

/**
 * Manages the master block of the privacy settings pages
 * 
 * @author Patrick Strobel
 * 
 */
public class PrivacySettingsBlock extends MasterDetailsBlock {

    private TreeViewer treeViewer;
    private final RgisModel model;
    private ControlDecoration treeDec;

    public PrivacySettingsBlock(PrivacySettingsPage form, RgisModel model) {
	this.model = model;
    }

    public void setDirty(boolean dirty) {
	model.setDirty(true);
    }

    @Override
    protected void createMasterPart(final IManagedForm managedForm,
	    Composite parent) {
	FormToolkit toolkit = managedForm.getToolkit();

	// Create section
	Section section = toolkit.createSection(parent,
		ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
	section.setText("Privacy Settings");
	section.setExpanded(true);
	section.marginWidth = 5;
	section.marginHeight = 5;

	Composite compo = toolkit.createComposite(section);
	compo.setLayout(new GridLayout(2, false));

	// Add tree
	treeViewer = new TreeViewer(compo, SWT.BORDER);
	treeViewer.setContentProvider(new PrivacySettingsContentProvider());
	treeViewer.setLabelProvider(new PrivacySettingsLabelProvider());
	treeViewer.setInput(model.getRgis());

	// Add decoration
	treeDec = new ControlDecoration(treeViewer.getControl(), SWT.TOP
		| SWT.LEFT);
	treeDec.setImage(Images.ERROR_DEC);
	validate();

	// Add tooltip listener
	TooltipTreeListener tooltipListener = new TooltipTreeListener(
		treeViewer, parent.getShell());
	treeViewer.getTree().addListener(SWT.Dispose, tooltipListener);
	treeViewer.getTree().addListener(SWT.KeyDown, tooltipListener);
	treeViewer.getTree().addListener(SWT.MouseMove, tooltipListener);
	treeViewer.getTree().addListener(SWT.MouseHover, tooltipListener);

	GridData treeLayout = new GridData();
	treeLayout.verticalAlignment = GridData.FILL;
	treeLayout.grabExcessVerticalSpace = true;
	treeLayout.horizontalAlignment = GridData.FILL;
	treeLayout.grabExcessHorizontalSpace = true;
	treeViewer.getControl().setLayoutData(treeLayout);

	// Create listener that handles selections of tree-items
	final SectionPart spart = new SectionPart(section);
	managedForm.addPart(spart);
	managedForm.reflow(true);

	// Add buttons
	Composite buttonCompo = toolkit.createComposite(compo);
	GridData buttonLayout = new GridData();
	buttonLayout.verticalAlignment = SWT.BEGINNING;
	buttonCompo.setLayout(new FillLayout(SWT.VERTICAL));
	buttonCompo.setLayoutData(buttonLayout);
	Button addButton = toolkit.createButton(buttonCompo, "Add", SWT.PUSH);
	addButton.setImage(Images.IMG_OBJ_ADD);

	addButton.addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		IRGIS rgis = model.getRgis();
		RGISPrivacySetting ps = new RGISPrivacySetting();
		rgis.addPrivacySetting(ps);
		setDirty(true);
		refresh();
	    }

	});

	final Button removeButton = toolkit.createButton(buttonCompo, "Remove",
		SWT.PUSH);
	removeButton.setEnabled(false);
	removeButton.setImage(Images.IMG_ETOOL_DELETE);
	removeButton.addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		// Get selected element
		RGISPrivacySetting ps = null;
		TreeSelection sel = (TreeSelection) treeViewer.getSelection();
		if (sel.getFirstElement() instanceof RGISPrivacySetting) {
		    ps = (RGISPrivacySetting) sel.getFirstElement();
		} else {
		    // Get parent if default name/desc is selected
		    TreePath[] path = sel.getPaths();
		    if (path.length < 1) {
			return;
		    }
		    ps = (RGISPrivacySetting) path[0].getFirstSegment();
		}

		// Remove selected entry for model
		IRGIS rgis = model.getRgis();
		rgis.removePrivacySetting(ps);
		setDirty(true);
		refresh();
	    }

	});

	treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

	    @Override
	    public void selectionChanged(SelectionChangedEvent event) {
		managedForm.fireSelectionChanged(spart, event.getSelection());
		removeButton.setEnabled(!event.getSelection().isEmpty());
	    }
	});

	section.setClient(compo);
    }

    private void validate() {
	RGISValidatorWrapper validator = RGISValidatorWrapper.getInstance();
	validator.validateRGIS(model.getRgis(), true);

	// Set decoration
	treeDec.hide();
	List<IIssue> dataIssues = model.getRgis().getIssues();
	List<IIssue> treeIssues = new ArrayList<IIssue>();

	for (IIssue i : dataIssues) {
	    if (i.getType() == IssueType.NO_PS_EXISTS) {
		treeDec.show();
		treeIssues.add(i);
	    }

	    if (i.getType() == IssueType.PS_IDENTIFIER_OCCURRED_TOO_OFTEN) {
		treeDec.show();
		treeIssues.add(i);
	    }
	}

	treeDec.setDescriptionText(new IssueTranslator()
		.translateIssues(treeIssues));
    }

    protected void refresh() {
	validate();
	treeViewer.refresh();

    }

    @Override
    protected void registerPages(DetailsPart detailsPart) {
	detailsPart.registerPage(RGISPrivacySetting.class,
		new PrivacySettingDetailsPage(this));
	detailsPart.registerPage(NameString.class, new LocalizationDetailsPage(
		this, LocaleTable.Type.NAME));
	detailsPart
		.registerPage(DescriptionString.class,
			new LocalizationDetailsPage(this,
				LocaleTable.Type.DESCRIPTION));
	detailsPart.registerPage(ChangeDescriptionString.class,
		new LocalizationDetailsPage(this,
			LocaleTable.Type.CHANGE_DESCRIPTION));

    }

    @Override
    protected void createToolBarActions(IManagedForm managedForm) {
	// TODO Auto-generated method stub

    }
    
    public RgisModel getModel() {
	return model;
    }

}
