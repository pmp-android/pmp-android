package de.unistuttgart.ipvs.pmp.editor.ui.editors.ais;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import de.unistuttgart.ipvs.pmp.editor.model.Model;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.AisEditor;
import de.unistuttgart.ipvs.pmp.editor.ui.editors.internals.ais.ServiceFeatureTable;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.AISRequiredPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.AISRequiredResourceGroup;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.AISServiceFeature;

/**
 * Creates the preferences page for the {@link AisEditor}
 * 
 * @author Thorsten Berberich
 * 
 */
public class AISServiceFeaturesPage extends FormPage {

    /**
     * ID of this page
     */
    public static final String ID = "ais_service_features";

    public int width;
    public int height;

    /**
     * Constructor
     * 
     * @param editor
     */
    public AISServiceFeaturesPage(FormEditor editor) {
	super(editor, ID, "Service Features");
	width = editor.getSite().getShell().getSize().x;
	height = editor.getSite().getShell().getSize().y;
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
	ScrolledForm form = managedForm.getForm();
	FormToolkit toolkit = managedForm.getToolkit();
	form.setText("Define general information");

	form.getBody().setLayout(new GridLayout(2, false));

	addServiceFeatureSection(form.getBody(), toolkit);
	addServiceFeatureSectionTree(form.getBody(), toolkit);
    }

    private void addServiceFeatureSection(Composite parent, FormToolkit toolkit) {
	// Set the section's parameters
	Section section = createSectionWithDescription(parent,
		"Service Features", toolkit,
		"Defines the service features the ais.xml");

	// Create elements stored inside this section
	Composite client = toolkit.createComposite(section, SWT.NULL);
	client.setLayout(new FillLayout());

	new ServiceFeatureTable(client, toolkit,
		height);
	section.setClient(client);
    }

    private void addServiceFeatureSectionTree(Composite parent,
	    FormToolkit toolkit) {
	// Set the section's parameters
	Section section = createSectionWithDescription(parent,
		"Service Features", toolkit,
		"Defines the service features the ais.xml");

	// Create elements stored inside this section
	Composite client = toolkit.createComposite(section, SWT.BORDER);
	client.setLayout(new FillLayout());
	client.setSize(parent.getSize());

	Tree tree = toolkit.createTree(client, SWT.BORDER);
	for (AISServiceFeature sf : Model.getInstance().getAis()
		.getServiceFeatures()) {
	    TreeItem name = new TreeItem(tree, 0);
	    name.setText(sf.getIdentifier());
	    for (AISRequiredResourceGroup rg : sf.getRequiredResourceGroups()) {
		TreeItem resGroup = new TreeItem(name, 0);
		resGroup.setText(rg.getIdentifier());
		for (AISRequiredPrivacySetting ps : rg
			.getRequiredPrivacySettings()) {
		    TreeItem privacySetting = new TreeItem(resGroup, 0);
		    privacySetting.setText(ps.getIdentifier() + " : "
			    + ps.getValue());
		}
	    }
	}
	section.setClient(client);
    }

    /**
     * Creates a default section which spans over the whole editor
     * 
     * @param parent
     *            parent {@link Composite}
     * @param title
     *            the title
     * @param toolkit
     *            {@link FormToolkit}
     * @param desc
     *            description of the Section
     * @return
     */
    private Section createSectionWithDescription(Composite parent,
	    String title, FormToolkit toolkit, String desc) {
	Section section = toolkit.createSection(parent, Section.TWISTIE
		| Section.TITLE_BAR | Section.DESCRIPTION);
	section.setDescription(desc);
	section.setText(title);
	section.setExpanded(true);

	GridData layoutData = new GridData();
	layoutData.horizontalAlignment = GridData.FILL;
	layoutData.widthHint = width / 4;

	section.setLayoutData(layoutData);

	return section;
    }

}
