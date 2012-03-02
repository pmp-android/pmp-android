package de.unistuttgart.ipvs.pmp.gui.preset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;

public class AdapterString extends BaseAdapter {
    
    private List<String> items;
    private Context context;
    private Map<String, Boolean> checkedMap = new HashMap<String, Boolean>();
    
    
    public AdapterString(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }
    
    
    @Override
    public int getCount() {
        return items.size();
    }
    
    
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }
    
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        checkedMap.put(items.get(position), false);
        
        return new CheckableListItem(this.context, this, items.get(position));
    }
    
    
    public void setChecked(String item, boolean checked) {
        checkedMap.put(item, checked);
    }
    
    
    public boolean isChecked(int position) {
        return checkedMap.get(items.get(position));
    }
}

class CheckableListItem extends LinearLayout {
    
    private AdapterString adapter;
    private String item;
    
    
    public CheckableListItem(Context context, AdapterString adapter, String item) {
        super(context);
        
        this.adapter = adapter;
        this.item = item;
        
        setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));
        
        LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View entryView = infalInflater.inflate(R.layout.listitem_checkable_string, null);
        entryView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));
        addView(entryView);
        
        refresh();
        addListener();
    }
    
    
    private void refresh() {
        ((TextView) findViewById(R.id.TextView_Name)).setText(this.item);
    }
    
    
    private void addListener() {
        this.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ((CheckBox) findViewById(R.id.CheckBox_State))
                        .setChecked(!((CheckBox) findViewById(R.id.CheckBox_State)).isChecked());
            }
        });
        
        ((CheckBox) findViewById(R.id.CheckBox_State))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        adapter.setChecked(item, buttonView.isChecked());
                    }
                });
    }
}