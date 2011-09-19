package de.unistuttgart.ipvs.pmp.model.implementations.test.utils.ServiceLevelCalculatorTest.TestServiceLevel;

import de.unistuttgart.ipvs.pmp.model.implementations.test.utils.ServiceLevelCalculatorTest.TestAccuracyRessourceGroup.TestPrivacyLevel50m;
import de.unistuttgart.ipvs.pmp.model.implementations.test.utils.ServiceLevelCalculatorTest.TestLocationRessourceGroup.TestPrivacyLevelStuttgart;
import de.unistuttgart.ipvs.pmp.model.interfaces.IPrivacyLevel;
import de.unistuttgart.ipvs.pmp.model.interfaces.IServiceLevel;

public class ServiceLevel2 implements IServiceLevel{

    @Override
    public int getLevel() {
	return 2;
    }

    @Override
    public String getName() {
	return "Level 2";
    }

    @Override
    public String getDescription() {
	return "Second service level";
    }

    @Override
    public IPrivacyLevel[] getPrivacyLevels() {
	IPrivacyLevel[] pls = new IPrivacyLevel[2];
	pls[0] = new TestPrivacyLevelStuttgart();
	pls[1] = new TestPrivacyLevel50m();
	return pls;
    }

    @Override
    public boolean isAvailable() {
	return true;
    }

}
