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

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.unistuttgart.ipvs.pmp.editor.ui.editors.internals.Images;
import de.unistuttgart.ipvs.pmp.editor.xml.IssueTranslator;
import de.unistuttgart.ipvs.pmp.editor.xml.RGISValidatorWrapper;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGIS;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.RGISPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IIssue;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IssueType;

/**
 * Defines the details page that allows the user to edit the privacy setting's
 * identifier and valid values
 * 
 * @author Patrick Strobel
 */
public class PrivacySettingDetailsPage implements IDetailsPage {
    
    private final PrivacySettingsBlock block;
    private IManagedForm form;
    private ControlDecoration identifierDec;
    private ControlDecoration valuesDec;
    private Text identifier;
    private Text values;
    private RGISPrivacySetting privacySetting;
    
    
    public PrivacySettingDetailsPage(PrivacySettingsBlock block) {
        this.block = block;
    }
    
    
    @Override
    public void initialize(IManagedForm form) {
        this.form = form;
    }
    
    
    @Override
    public void createContents(Composite parent) {
        // Set parent's layout
        GridData parentLayout = new GridData();
        parentLayout.verticalAlignment = GridData.FILL;
        parentLayout.grabExcessVerticalSpace = true;
        parentLayout.horizontalAlignment = GridData.FILL;
        parentLayout.grabExcessHorizontalSpace = true;
        parent.setLayout(new GridLayout());
        // parent.setLayoutData(parentLayout);
        
        // Build view
        IssueTranslator it = new IssueTranslator();
        // System.out.println("Draw");
        FormToolkit toolkit = this.form.getToolkit();
        Section section = toolkit.createSection(parent, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
        section.setText("Privacy Setting");
        section.setExpanded(true);
        section.setLayoutData(parentLayout);
        
        // Add Textfields
        Composite compo = toolkit.createComposite(section);
        compo.setLayout(new GridLayout(2, false));
        GridData textLayout = new GridData();
        textLayout.horizontalAlignment = GridData.FILL;
        textLayout.grabExcessHorizontalSpace = true;
        toolkit.createLabel(compo, "Identifier");
        this.identifier = toolkit.createText(compo, "Value");
        this.identifier.setLayoutData(textLayout);
        this.identifier.addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyReleased(KeyEvent e) {
                if (!privacySetting.getIdentifier().equals(identifier.getText())) {
                    privacySetting.setIdentifier(identifier.getText());
                    block.setDirty(true);
                }
                
            }
        });
        this.identifier.addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusLost(FocusEvent e) {
                validate();
            }
            
        });
        this.identifierDec = new ControlDecoration(this.identifier, SWT.TOP | SWT.LEFT);
        this.identifierDec.setImage(Images.IMG_DEC_FIELD_ERROR);
        this.identifierDec.setDescriptionText(it.getTranslationWithoutParameters(IssueType.PS_IDENTIFIER_MISSING));
        
        toolkit.createLabel(compo, "Valid values");
        this.values = toolkit.createText(compo, "True/False");
        this.values.setLayoutData(textLayout);
        this.values.addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyReleased(KeyEvent e) {
                if (!privacySetting.getValidValueDescription().equals(values.getText())) {
                    privacySetting.setValidValueDescription(values.getText());
                    block.setDirty(true);
                }
                
            }
        });
        this.values.addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusLost(FocusEvent e) {
                validate();
            }
        });
        this.valuesDec = new ControlDecoration(this.values, SWT.TOP | SWT.LEFT);
        this.valuesDec.setImage(Images.IMG_DEC_FIELD_ERROR);
        this.valuesDec
                .setDescriptionText(it.getTranslationWithoutParameters(IssueType.VALID_VALUE_DESCRIPTION_MISSING));
        section.setClient(compo);
        
    }
    
    
    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        
    }
    
    
    @Override
    public boolean isDirty() {
        return false;
    }
    
    
    @Override
    public void commit(boolean onSave) {
        
        this.block.refresh();
        
    }
    
    
    @Override
    public boolean setFormInput(Object input) {
        return false;
    }
    
    
    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
        
    }
    
    
    @Override
    public boolean isStale() {
        return false;
    }
    
    
    @Override
    public void refresh() {
        update();
    }
    
    
    @Override
    public void selectionChanged(IFormPart part, ISelection selection) {
        this.privacySetting = (RGISPrivacySetting) ((TreeSelection) selection).getFirstElement();
        update();
    }
    
    
    private void update() {
        this.identifier.setText(this.privacySetting.getIdentifier());
        this.values.setText(this.privacySetting.getValidValueDescription());
        validate();
    }
    
    
    private void validate() {
        block.refresh();
        RGISValidatorWrapper validator = RGISValidatorWrapper.getInstance();
        IRGIS rgis = this.block.getModel().getRgis();
        validator.validateRGIS(rgis, true);
        
        this.identifierDec.hide();
        this.valuesDec.hide();
        
        for (IIssue i : privacySetting.getIssues()) {
            switch (i.getType()) {
                case IDENTIFIER_MISSING:
                    this.identifierDec.show();
                    break;
                case VALID_VALUE_DESCRIPTION_MISSING:
                    this.valuesDec.show();
                    break;
            }
        }
    }
    
}
