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
package de.unistuttgart.ipvs.pmp.editor.ui.wizard.rgis;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.xml.sax.SAXException;

import de.unistuttgart.ipvs.pmp.editor.exceptions.androidmanifestparser.AppIdentifierNotFoundException;
import de.unistuttgart.ipvs.pmp.editor.util.AndroidManifestAdapter;
import de.unistuttgart.ipvs.pmp.editor.util.I18N;

/**
 * The {@link WizardPage} of the RGIS wizard
 * 
 * @author Thorsten Berberich
 * 
 */
public class WizardPageCreateRGIS extends WizardPage {
    
    /**
     * {@link Text} for the project
     */
    private Text assetsFolderText;
    
    /**
     * The identifier of the project
     */
    private Text identifier;
    
    
    /**
     * Constructor for the {@link WizardPageCreateRGIS}
     * 
     * @param selection
     *            {@link ISelection}
     */
    public WizardPageCreateRGIS(ISelection selection) {
        super("Resourcegroup-Information-Set"); //$NON-NLS-1$
        setTitle(I18N.wizard_rgis_title);
        setDescription(I18N.wizard_rgis_description);
    }
    
    
    /**
     * @see IDialogPage#createControl(Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        
        // Project label
        Label label = new Label(container, SWT.NULL);
        label.setText(I18N.wizard_general_folder + ":"); //$NON-NLS-1$
        this.assetsFolderText = new Text(container, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        this.assetsFolderText.setLayoutData(gd);
        this.assetsFolderText.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        
        // Button to browse the project
        Button button = new Button(container, SWT.PUSH);
        button.setText(I18N.general_browse);
        button.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleBrowse();
            }
        });
        
        label = new Label(container, SWT.NULL);
        label.setText(I18N.wizard_general_file + ":"); //$NON-NLS-1$
        
        Text fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
        fileText.setText("rgis.xml"); //$NON-NLS-1$
        fileText.setEnabled(false);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fileText.setLayoutData(gd);
        fileText.addKeyListener(new KeyListener() {
            
            @Override
            public void keyReleased(KeyEvent e) {
            }
            
            
            @Override
            public void keyPressed(KeyEvent e) {
                dialogChanged();
            }
        });
        
        new Text(container, SWT.NULL).setVisible(false);
        
        label = new Label(container, SWT.NULL);
        label.setText(I18N.general_identifier + ":"); //$NON-NLS-1$
        
        this.identifier = new Text(container, SWT.BORDER | SWT.SINGLE);
        this.identifier.setEnabled(false);
        this.identifier.setLayoutData(gd);
        
        dialogChanged();
        setControl(container);
    }
    
    
    /**
     * Uses the standard container selection dialog to choose the value for the
     * project field. Only the project path will be taken and "/assets" will be
     * appended
     */
    private void handleBrowse() {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace()
                .getRoot(), false, I18N.wizard_general_selectdialog_text);
        if (dialog.open() == Window.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                String[] split = ((Path) result[0]).toString().split("/"); //$NON-NLS-1$
                this.assetsFolderText.setText("/" + split[1] + "/assets"); //$NON-NLS-1$ //$NON-NLS-2$
                dialogChanged();
            }
        }
    }
    
    
    /**
     * Checks the project field if it is correct
     */
    private void dialogChanged() {
        // Split only the project out of the string
        String[] projects = getProjectName().split("/"); //$NON-NLS-1$
        
        // The project name
        String project = ""; //$NON-NLS-1$
        if (projects.length >= 2) {
            project = "/" + projects[1]; //$NON-NLS-1$
        }
        
        IResource iRes = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(project));
        
        // Path doesn't start with a "/"
        if (!getProjectName().startsWith("/")) { //$NON-NLS-1$
            updateStatus(I18N.wizard_general_error_pathstart);
            this.identifier.setText(""); //$NON-NLS-1$
            return;
        }
        
        // No project entered
        if (getProjectName().length() == 0) {
            updateStatus(I18N.wizard_general_error_projectspecified);
            this.identifier.setText(""); //$NON-NLS-1$
            return;
        }
        
        // Project doesn't exist
        if (iRes == null || (iRes.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
            updateStatus(I18N.wizard_general_error_projectcontainer);
            this.identifier.setText(""); //$NON-NLS-1$
            return;
        }
        
        // Project isn't writable
        if (!iRes.isAccessible()) {
            updateStatus(I18N.wizard_general_error_projectwritable);
            this.identifier.setText(""); //$NON-NLS-1$
            return;
        }
        
        // Check if it's an Android-Project
        try {
            String appIdentifier = new AndroidManifestAdapter().getAppIdentifier(project + "/", "AndroidManifest.xml"); //$NON-NLS-1$ //$NON-NLS-2$
            this.identifier.setText(appIdentifier);
        } catch (ParserConfigurationException e) {
            updateStatus(I18N.wizard_general_error_androidproject);
            return;
        } catch (SAXException e) {
            updateStatus(I18N.wizard_general_error_androidproject);
            return;
        } catch (IOException e) {
            updateStatus(I18N.wizard_general_error_androidproject);
            return;
        } catch (AppIdentifierNotFoundException e) {
            updateStatus(e.getMessage());
            return;
        }
        
        // Assets folder is not specified at the path
        if (!getProjectName().endsWith(project + "/assets")) { //$NON-NLS-1$
            updateStatus(I18N.wizard_general_error_folder);
            return;
        }
        
        // Everything correct
        updateStatus(null);
    }
    
    
    /**
     * Sets the error message and if the page is complete
     * 
     * @param message
     *            <code>null</code> if the page is complete, else the error
     *            message to display
     */
    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }
    
    
    /**
     * Returns the selected project with the /assets appended
     * 
     * @return project path + "/assets"
     */
    public String getProjectName() {
        return this.assetsFolderText.getText();
    }
    
    
    /**
     * Returns the app identifier out of the AndroidManifest.xml
     * 
     * @return App identifier
     */
    public String getIdentifier() {
        return this.identifier.getText();
    }
    
    
    /**
     * Returns the project without the "/assets"
     * 
     * @return project only
     */
    public String getProjectOnly() {
        // Split only the project out of the string
        String[] projects = getProjectName().split("/"); //$NON-NLS-1$
        
        // The project name
        String project = ""; //$NON-NLS-1$
        if (projects.length >= 2) {
            project = projects[1];
        }
        return project;
    }
}
