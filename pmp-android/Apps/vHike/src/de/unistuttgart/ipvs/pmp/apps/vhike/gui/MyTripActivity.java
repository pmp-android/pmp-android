package de.unistuttgart.ipvs.pmp.apps.vhike.gui;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.Controller;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.vHikeService;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.adapter.MyTripAdapter;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.ViewModel;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Model;

/**
 * 
 * @author Dang Huynh
 * 
 */
public class MyTripActivity extends ListActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String[] names = new String[] { "Linux", "Windows7", "Eclipse", "Suse", "Ubuntu", "Solaris", "Android",
                "iPhone", "Linux", "Windows7", "Eclipse" };
        //        View header = getLayoutInflater().inflate(R.layout.header, null);
        //        View footer = getLayoutInflater().inflate(R.layout.footer, null);
        ListView listView = getListView();
        //        listView.addHeaderView(header);
        //        listView.addFooterView(footer);
        listView.setAdapter(new MyTripAdapter(this, names));
    }
    
    
    @Override
    public void onResume() {
        super.onResume();
        
        Controller ctrl = new Controller(ViewModel.getInstance().getvHikeRG());
        
        if (vHikeService.isServiceFeatureEnabled(Constants.SF_HIDE_CONTACT_INFO)) {
            ctrl.enableAnonymity(Model.getInstance().getSid());
        } else {
            ctrl.disableAnonymity(Model.getInstance().getSid());
        }
        Log.i(this, "");
    }
}
