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

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.unistuttgart.ipvs.pmp.editor.model.AisModel;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.internals.ILocaleTableAction;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.internals.Images;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.internals.LocaleTable;
import de.unistuttgart.ipvs.pmp.editor.xml.AISValidatorWrapper;
import de.unistuttgart.ipvs.pmp.editor.xml.IssueTranslator;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.AISServiceFeature;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IssueType;

/**
 * Shows the lists for the names and the descriptions of a service feature
 * 
 * @author Thorsten Berberich
 * 
 */
public class ServiceFeatureNameDetailsPage implements IDetailsPage {

    /**
     * ID of this page
     */
    public static final String ID = "ais_service_feature_names";

    /**
     * Given form
     */
    private IManagedForm form;

    /**
     * The model of this editor instance
     */
    private final AisModel model;

    /**
     * The {@link LocaleTable} for the descriptions
     */
    private LocaleTable descriptionTable;

    /**
     * The {@link LocaleTable} for the names
     */
    private LocaleTable nameTable;

    /**
     * The tree of the {@link ServiceFeatureMasterBlock}
     */
    private TreeViewer parentTree;

    /**
     * The displayed ServiceFeature
     */
    private AISServiceFeature displayed;

    /**
     * The textfield of the identifier
     */
    private Text identifierField;

    /**
     * The decoration of the identifier label
     */
    private ControlDecoration identifierDec;

    /**
     * Constructor to get the model and the tree to refresh it
     * 
     * @param model
     *            model of this instance
     * @param parentTree
     *            the {@link TreeViewer} of the parent view to refresh it
     */
    public ServiceFeatureNameDetailsPage(AisModel model, TreeViewer parentTree) {
	this.model = model;
	this.parentTree = parentTree;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.forms.IFormPart#initialize(org.eclipse.ui.forms.IManagedForm
     * )
     */
    @Override
    public void initialize(IManagedForm arg0) {
	form = arg0;
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

	// Set parent's layout
	GridData parentLayout = new GridData();
	parentLayout.verticalAlignment = GridData.FILL;
	parentLayout.grabExcessVerticalSpace = true;
	parentLayout.horizontalAlignment = GridData.FILL;
	parentLayout.grabExcessHorizontalSpace = true;
	parent.setLayout(new GridLayout(1, false));

	FormToolkit toolkit = form.getToolkit();

	// The attribute layout data
	GridData attributeLayout = new GridData();
	attributeLayout.horizontalAlignment = GridData.FILL;
	attributeLayout.grabExcessHorizontalSpace = true;

	// The attribute section
	Section attributeSection = toolkit.createSection(parent,
		ExpandableComposite.CLIENT_INDENT
			| ExpandableComposite.TITLE_BAR);
	attributeSection.setText("Attribute");
	attributeSection.setLayout(new GridLayout(1, false));
	attributeSection.setExpanded(true);
	attributeSection.setLayoutData(attributeLayout);

	Composite attributeComp = toolkit.createComposite(attributeSection);
	GridLayout attributeGridLayout = new GridLayout(2, false);
	attributeGridLayout.horizontalSpacing = 7;
	attributeComp.setLayout(attributeGridLayout);

	GridData textLayout = new GridData();
	textLayout.horizontalAlignment = GridData.FILL;
	textLayout.grabExcessHorizontalSpace = true;

	Label identifierLabel = new Label(attributeComp, SWT.NONE);

	identifierLabel.setText("Identifier:");

	identifierField = new Text(attributeComp, SWT.BORDER);
	attributeSection.setClient(attributeComp);
	identifierField.setLayoutData(textLayout);

	identifierDec = new ControlDecoration(identifierField, SWT.TOP
		| SWT.LEFT);
	identifierDec.setImage(Images.ERROR_DEC);

	// Store the field in the model when sth. was changed
	identifierField
		.addKeyListener(new org.eclipse.swt.events.KeyListener() {

		    @Override
		    public void keyReleased(org.eclipse.swt.events.KeyEvent arg0) {
			displayed.setIdentifier(identifierField.getText());
			parentTree.refresh();
			model.setDirty(true);
		    }

		    @Override
		    public void keyPressed(org.eclipse.swt.events.KeyEvent arg0) {
		    }
		});

	// Validate the ais if the focus was lost
	identifierField.addFocusListener(new FocusListener() {

	    @Override
	    public void focusLost(FocusEvent arg0) {
		AISValidatorWrapper.getInstance().validateAIS(model.getAis(),
			true);
		updateIdentifierDecoration();
	    }

	    @Override
	    public void focusGained(FocusEvent arg0) {
	    }
	});

	// The name section
	Section nameSection = toolkit.createSection(parent,
		ExpandableComposite.CLIENT_INDENT
			| ExpandableComposite.TITLE_BAR);
	nameSection.setText("Names");
	nameSection.setLayout(new GridLayout(1, false));
	nameSection.setExpanded(true);
	nameSection.setLayoutData(parentLayout);

	// The description section
	Section descriptionSection = toolkit.createSection(parent,
		ExpandableComposite.CLIENT_INDENT
			| ExpandableComposite.TITLE_BAR);
	descriptionSection.setText("Descriptions");
	descriptionSection.setLayout(new GridLayout(1, false));
	descriptionSection.setExpanded(true);
	descriptionSection.setLayoutData(parentLayout);

	// Composite that is display in the description section
	Composite descriptionComposite = toolkit
		.createComposite(descriptionSection);
	descriptionComposite.setLayout(new GridLayout(1, false));
	descriptionComposite.setLayoutData(parentLayout);

	// Build localization table
	ILocaleTableAction dirtyAction = new ILocaleTableAction() {

	    @Override
	    public void doSetDirty(boolean dirty) {
		model.setDirty(true);

	    }

	    @Override
	    public void doValidate() {
		AISValidatorWrapper.getInstance().validateAIS(model.getAis(),
			true);
		parentTree.refresh();
	    }

	};
	descriptionTable = new LocaleTable(descriptionComposite,
		LocaleTable.Type.DESCRIPTION, dirtyAction, toolkit);

	descriptionTable.getComposite().setLayoutData(parentLayout);

	// Composite that is display in the name section
	Composite nameComposite = toolkit.createComposite(nameSection);
	nameComposite.setLayout(new GridLayout(1, false));
	nameComposite.setLayoutData(parentLayout);
	nameTable = new LocaleTable(nameComposite, LocaleTable.Type.NAME,
		dirtyAction, toolkit);

	nameTable.getComposite().setLayoutData(parentLayout);

	// Set the composites
	descriptionSection.setClient(descriptionComposite);
	nameSection.setClient(nameComposite);
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
	// Get the selected service feature and set the name
	TreePath[] path = ((TreeSelection) selection).getPaths();
	displayed = (AISServiceFeature) path[0].getFirstSegment();
	identifierField.setText(displayed.getIdentifier());
	updateIdentifierDecoration();
	descriptionTable.setData(displayed);
	descriptionTable.refresh();

	nameTable.setData(displayed);
	nameTable.refresh();
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
     * @see org.eclipse.ui.forms.IFormPart#isDirty()
     */
    @Override
    public boolean isDirty() {
	return model.isDirty();
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

    /**
     * Updates the decoration of the identifier field
     */
    private void updateIdentifierDecoration() {
	identifierDec.hide();
	if (displayed.hasIssueType(IssueType.IDENTIFIER_MISSING)) {
	    identifierDec.show();
	    identifierDec
		    .setDescriptionText(new IssueTranslator()
			    .getTranslationWithoutParameters(IssueType.IDENTIFIER_MISSING));
	}
    }
}
