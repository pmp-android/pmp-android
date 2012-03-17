package de.unistuttgart.ipvs.pmp.apps.vhike.gui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.vHikeService;

public class ChangeServiceFeature extends Dialog {
    
    private Button cancel;
    private Button change_SF;
    private Context context;
    
    
    public ChangeServiceFeature(Context context) {
        super(context);
        setContentView(R.layout.dialog_change_sf);
        this.context = context;
        
        setLayout();
    }
    
    
    private void setLayout() {
        cancel = (Button) findViewById(R.id.btn_cancel_change);
        change_SF = (Button) findViewById(R.id.btn_open_pmp);
        
        cancel.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        
        change_SF.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // Open PMP
                vHikeService.getInstance()
                        .requestServiceFeature((Activity) context, 0);
            }
        });
    }
    
}