package de.unistuttgart.ipvs.pmp.gui.view;

import java.util.Random;

import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PrivacySettingView extends LinearLayout {
    
    public IServiceFeature serviceFeature;
    public IPrivacySetting privacySetting;
    
    
    public PrivacySettingView(Context context, IServiceFeature serviceFeature, IPrivacySetting privacySetting) {
        super(context);
        
        this.serviceFeature = serviceFeature;
        this.privacySetting = privacySetting;
        
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.listitem_app_ps, null);
        addView(v);
        
        addListener();
        
        refresh();
    }
    
    
    public void refresh() {
        TextView tvTitle = (TextView) findViewById(R.id.TextView_Title);
        TextView tvDescription = (TextView) findViewById(R.id.TextView_Description);
        ImageView stateImage = (ImageView) findViewById(R.id.ImageView_State);
        
        // Update name
        tvTitle.setText(Html.fromHtml("<b>" + this.privacySetting.getResourceGroup().getName() + " - <i>"
                + this.privacySetting.getName() + "</i></b>"));
        
        // Update description
        try {
            tvDescription.setText(getContext().getResources().getString(R.string.required_value)
                    + ": "
                    + this.privacySetting.getHumanReadableValue(this.serviceFeature
                            .getRequiredPrivacySettingValue(this.privacySetting)));
        } catch (PrivacySettingValueException e) {
            tvDescription.setText(Html.fromHtml("<span style=\"color:red;\">"
                    + getContext().getResources().getString(R.string.ps_invalid_value) + "</span>"));
        }
        
        // TODO Implement that! (very important for final release)
        if (new Random().nextBoolean()) {
            stateImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_success));
        } else {
            stateImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_delete));
        }
    }
    
    
    private void addListener() {
        setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Tapped on an item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}