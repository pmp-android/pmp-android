package de.unistuttgart.ipvs.pmp.model.implementations.test.utils.ServiceLevelCalculatorTest.TestLocationRessourceGroup;

import de.unistuttgart.ipvs.pmp.model.interfaces.IApp;
import de.unistuttgart.ipvs.pmp.model.interfaces.IPrivacyLevel;
import de.unistuttgart.ipvs.pmp.model.interfaces.IResourceGroup;

public class TestLocationResourceGroup implements IResourceGroup{
    
    private String identifier = "de.unistuttgart.ipvs.pmp.model.implementations.test.utils.ServiceLevelCalculatorTest.TestLocationRessourceGroup.TestLocationResourceGroup";

    @Override
    public String getIdentifier() {
	return identifier;
    }

    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getDescription() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IPrivacyLevel[] getPrivacyLevels() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IPrivacyLevel getPrivacyLevel(String privacyLevelIdentifier) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IApp[] getAllAppsUsingThisResourceGroup() {
	// TODO Auto-generated method stub
	return null;
    }

}
