package de.unistuttgart.ipvs.pmp.apps.infoapp.panels.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.apps.infoapp.R;
import de.unistuttgart.ipvs.pmp.apps.infoapp.panels.IPanel;

public class ExamplePanel implements IPanel {
	
	private LinearLayout view;
	
	
	public ExamplePanel(Context context) {
		
		/* load the layout from the xml file */
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.view = (LinearLayout) inflater.inflate(R.layout.example_panel, null);
		
		// Set texts
		TextView text1 = (TextView) this.view.findViewById(R.id.textView1);
		text1.setText("This is my example text 1");
		
		TextView text2 = (TextView) this.view.findViewById(R.id.textView2);
		text2.setText("This is my example text 2");
		
	}
	
	
	public View getView() {
		return this.view;
	}
	
	
	public String getTitle() {
		return "Example";
	}
	
}