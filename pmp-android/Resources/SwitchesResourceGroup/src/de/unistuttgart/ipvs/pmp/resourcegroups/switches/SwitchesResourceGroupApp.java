package de.unistuttgart.ipvs.pmp.resourcegroups.switches;

import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.resource.ResourceGroupSingleApp;

/**
 * App for {@link SwitchesResourceGroup}.
 * 
 * @author Tobias Kuhn
 * 
 */
public class SwitchesResourceGroupApp extends
		ResourceGroupSingleApp<SwitchesResourceGroup> {
	
	static {
		Log.setTagSufix("CalendarApp");
	}
	
	
	@Override
	protected SwitchesResourceGroup createResourceGroup() {
		// create RG
		SwitchesResourceGroup srg = new SwitchesResourceGroup(
				getApplicationContext());
		// call methods for this RG
		srg.setContext(getApplicationContext());
		// start it
		srg.start(getApplicationContext());
		return srg;
	}

}
