package de.unistuttgart.ipvs.pmp.gui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.PMPPreferences;
import de.unistuttgart.ipvs.pmp.gui.view.BasicTitleView;
import de.unistuttgart.ipvs.pmp.gui.view.ServiceFeatureView;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;

public class ServiceFeatureDialog extends Dialog {
    
    private IServiceFeature serviceFeature;
    private ServiceFeatureView serviceFeatureView;
    
    
    public ServiceFeatureDialog(Context context, IServiceFeature serviceFeature, ServiceFeatureView serviceFeatureView) {
        super(context);
        
        this.serviceFeature = serviceFeature;
        this.serviceFeatureView = serviceFeatureView;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.dialog_sf);
        
        BasicTitleView btv = (BasicTitleView) findViewById(R.id.Title);
        btv.setTitle(String.format(getContext().getResources().getString(R.string.service_feature_title),
                this.serviceFeature.getName()));
        
        TextView descriptionTv = (TextView) findViewById(R.id.TextView_Description);
        descriptionTv.setText(serviceFeature.getDescription());
        
        TextView requiredPSTv = (TextView) findViewById(R.id.TextView_PrivacySettings);
        String text = "<html>";
        
        for (IPrivacySetting ps : serviceFeature.getRequiredPrivacySettings()) {
            text += "<p>";
            text += "<b>" + ps.getResourceGroup().getName() + " - <i>" + ps.getName() + "</i></b><br/>";
            
            try {
                text += getContext().getResources().getString(R.string.required_value) + ": "
                        + ps.getHumanReadableValue(serviceFeature.getRequiredPrivacySettingValue(ps));
            } catch (PrivacySettingValueException e) {
                text += "<span style=\"color:red;\">"
                        + getContext().getResources().getString(R.string.ps_invalid_value) + "</span>";
            }
            text += "</p>";
        }
        text += "</html>";
        
        requiredPSTv.setText(Html.fromHtml(text));
        
        Button enableDisableButton = (Button) findViewById(R.id.Button_EnableDisable);
        if (PMPPreferences.getInstanace().isExpertMode()) {
            enableDisableButton.setVisibility(View.INVISIBLE);
        } else {
            enableDisableButton.setVisibility(View.VISIBLE);
        }
        enableDisableButton.setEnabled(serviceFeature.isAvailable());
        
        if (serviceFeature.isActive()) {
            enableDisableButton.setText(getContext().getResources().getString(R.string.disable));
        } else {
            enableDisableButton.setText(getContext().getResources().getString(R.string.enable));
        }
        
        addListener();
    }
    
    
    private void addListener() {
        Button closeButton = (Button) findViewById(R.id.Button_Close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ServiceFeatureDialog.this.cancel();
            }
        });
        
        Button enableDisableButton = (Button) findViewById(R.id.Button_EnableDisable);
        enableDisableButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                boolean newState = !ServiceFeatureDialog.this.serviceFeature.isActive();
                
                ServiceFeatureDialog.this.serviceFeatureView.reactOnChange(newState);
                ServiceFeatureDialog.this.cancel();
            }
        });
    }
    
}
