package de.unistuttgart.ipvs.pmp.apps.infoapp.panels.rss;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.unistuttgart.ipvs.pmp.apps.infoapp.panels.IPanel;

public class RSSPanel implements IPanel {

	private List<String> headlines;
	private List<String> links;

	private ListView view;
	
	private Context context;

	public RSSPanel(Context context) {
		this.context = context;
		
		view = new ListView(context);

		headlines = new ArrayList<String>();
		links = new ArrayList<String>();

		try {
			URL url = new URL("http://feeds.pcworld.com/pcworld/latestnews");
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			XmlPullParser xpp = factory.newPullParser();
			try {
				xpp.setInput(url.openConnection().getInputStream(), "UTF_8");
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			boolean insideItem = false;
			int eventType = xpp.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (xpp.getName().equalsIgnoreCase("item")) {
						insideItem = true;
					} else if (xpp.getName().equalsIgnoreCase("title")) {
						if (insideItem)
							headlines.add(xpp.nextText()); // extract the
															// headline
					} else if (xpp.getName().equalsIgnoreCase("link")) {
						if (insideItem)
							links.add(xpp.nextText()); // extract the link of
														// article
					}
				} else if (eventType == XmlPullParser.END_TAG
						&& xpp.getName().equalsIgnoreCase("item")) {
					insideItem = false;
				}

				eventType = xpp.next(); // move to next element
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		view.setAdapter(new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, headlines));
		view.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				Uri uri = Uri.parse((String) links.get(pos));
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				RSSPanel.this.context.startActivity(intent);
			}
		});
	}

	public View getView() {
		return view;
	}

	public String getTitle() {
		return "RSS";
	}

}
