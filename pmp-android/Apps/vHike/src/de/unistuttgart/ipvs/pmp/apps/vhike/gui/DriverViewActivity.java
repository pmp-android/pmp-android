package de.unistuttgart.ipvs.pmp.apps.vhike.gui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.Controller;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.adapter.NotificationAdapter;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.dialog.vhikeDialogs;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.DriverOverlay;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.LocationUpdateHandler;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.MapModel;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.PassengerOverlay;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Model;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Profile;

/**
 * DriverViewActivity displays drivers current location on google maps
 * 
 * @author Andre Nguyen
 * 
 */
public class DriverViewActivity extends MapActivity {

	private Context context;
	private MapView mapView;
	private MapController mapController;
	private LocationManager locationManager;
	private GeoPoint p;

	private Controller ctrl;
	private NotificationAdapter appsAdapter;

	private SlidingDrawer drawer;

	private int imADriver = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driverview);

		ctrl = new Controller();
		MapModel.getInstance().initDriversList();

		showHitchhikers();
		setMapView();
		setUpNotiBar();
		startTripByUpdating();

		vhikeDialogs.getInstance().getAnnouncePD(DriverViewActivity.this)
				.dismiss();
		vhikeDialogs.getInstance().clearAnnouncPD();
	}

	public DriverViewActivity() {
		this.context = DriverViewActivity.this;
	}

	/**
	 * adds passengers (hitchhikers) to the notification slider
	 */
	private void showHitchhikers() {

		ListView pLV = (ListView) findViewById(R.id.ListView_SearchingHitchhikers);
		pLV.setClickable(true);

		appsAdapter = MapModel.getInstance().getDriverAdapter(context);
		pLV.setAdapter(appsAdapter);
	}

	/**
	 * adds hitchhiker/passenger to hitchhiker list
	 * 
	 * @param hitchhiker
	 */
	public void addHitchhiker(Profile hitchhiker) {
		MapModel.getInstance().getHitchPassengers().add(hitchhiker);
		appsAdapter.notifyDataSetChanged();
	}

	/**
	 * displays the map from xml file including a button to get current user
	 * location
	 */
	private void setMapView() {
		mapView = (MapView) findViewById(R.id.driverMapView);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();

		Button btnDriverLocation = (Button) findViewById(R.id.Button_SearchQuery);
		btnDriverLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Controller ctrl = new Controller();
				ctrl.searchQuery(Model.getInstance().getSid(), 0, 0, 10);

			}
		});
	}

	/**
	 * Simulating notifications per button click if button is pressed slider is
	 * opened and user receives a notification via the status bar
	 */
	private void setUpNotiBar() {
		Button notiButton = (Button) findViewById(R.id.Button_SimulateNoti);
		notiButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Profile passenger = new Profile("Passenger1", null, null, null,
						null, null, null, false, false, false, false, 4.5, 4.5);
				Drawable drawable = context.getResources().getDrawable(
						R.drawable.passenger_logo);
				PassengerOverlay pOverlay = new PassengerOverlay(drawable,
						context);
				float lat = 37.4230182f;
				float lng = -122.0840848f;
				GeoPoint gps = new GeoPoint((int) (lat * 1E6),
						(int) (lng * 1E6));
				OverlayItem oItem = new OverlayItem(gps,
						"Im looking for a ride!", "User: "
								+ passenger.getUsername() + ", Rating: "
								+ passenger.getRating_avg());
				pOverlay.addOverlay(oItem);
				MapModel.getInstance().getDriverOverlayList(mapView)
						.add(pOverlay);
				mapView.invalidate();

				// get reference to notificationManager
				String ns = Context.NOTIFICATION_SERVICE;
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

				// instantiate the notification
				int icon = R.drawable.passenger_logo;
				CharSequence tickerText = "Found hitchhiker!";
				long when = System.currentTimeMillis();

				Notification notification = new Notification(icon, tickerText,
						when);
				notification.defaults |= Notification.DEFAULT_SOUND;

				// define the notification's message and PendingContent
				Context context = getApplicationContext();
				Profile pro = new Profile("bestHitcher", ns, ns, ns, ns, ns,
						null, false, false, false, false, lat, lat);
				CharSequence contentTitle = pro.getUsername()
						+ " wants a ride!";
				CharSequence contentText = "Touch to open profile";
				Intent notificationIntent = new Intent(DriverViewActivity.this,
						ProfileActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(
						DriverViewActivity.this, 0, notificationIntent, 0);

				notification.setLatestEventInfo(context, contentTitle,
						contentText, contentIntent);

				// pass notification to notificationManager
				final int HELLO_ID = 1;

				mNotificationManager.notify(HELLO_ID, notification);

				Profile profile = new Profile("demoTest", null, null, null,
						null, null, null, false, false, false, false, 4.5,
						imADriver);
				addHitchhiker(profile);

				drawer = (SlidingDrawer) findViewById(R.id.notiSlider);
				drawer.open();
			}
		});

	}

	/**
	 * get current location and notify server that a trip was announced for
	 * possible passengers to see
	 */
	private void startTripByUpdating() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, new LocationUpdateHandler(context, locationManager, mapView,
						mapController, p, imADriver));
		Controller ctrl = new Controller();
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location != null) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint gPosition = new GeoPoint(lat, lng);

			Profile me = Model.getInstance().getOwnProfile();

			Drawable drawableDriver = context.getResources().getDrawable(
					R.drawable.icon_ride);
			DriverOverlay dOverlay = new DriverOverlay(drawableDriver, context,
					gPosition);

			OverlayItem oDriverItem = new OverlayItem(gPosition,
					"Who wants a ride?", "User: " + me.getUsername()
							+ ", Rating: " + me.getRating_avg());
			dOverlay.addOverlay(oDriverItem);

			MapModel.getInstance().getDriverOverlayList(mapView).add(dOverlay);

			switch (ctrl.tripUpdatePos(Model.getInstance().getSid(), Model
					.getInstance().getTripId(), (float) location.getLatitude(),
					(float) location.getLongitude())) {
			case Constants.STATUS_UPDATED:
				Toast.makeText(DriverViewActivity.this, "Status updated",
						Toast.LENGTH_LONG).show();
//				ctrl.searchQuery(Model.getInstance().getSid(),
//						(float) location.getLatitude(),
//						(float) location.getLongitude(), 10);
				break;
			case Constants.STATUS_UPTODATE:
				Toast.makeText(DriverViewActivity.this, "Status Up to date",
						Toast.LENGTH_LONG).show();
				break;
			case Constants.STATUS_NOTRIP:
				Toast.makeText(DriverViewActivity.this, "Status no trip",
						Toast.LENGTH_LONG).show();
				break;
			case Constants.STATUS_HASENDED:
				Toast.makeText(DriverViewActivity.this, "Status trip ended",
						Toast.LENGTH_LONG).show();
				break;
			case Constants.STATUS_INVALID_USER:
				Toast.makeText(DriverViewActivity.this, "Status invalid user",
						Toast.LENGTH_LONG).show();

			}
		} else {
			Toast.makeText(context, "Location null", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.driverview_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.mi_endTrip):

			switch (ctrl.endTrip(Model.getInstance().getSid(), Model
					.getInstance().getTripId())) {
			case (Constants.STATUS_UPDATED): {
				Toast.makeText(DriverViewActivity.this, "Trip ended",
						Toast.LENGTH_LONG).show();
				MapModel.getInstance().clearDriverOverlayList();
				MapModel.getInstance().getHitchPassengers().clear();
				appsAdapter.notifyDataSetChanged();
				DriverViewActivity.this.finish();
				break;
			}
			case (Constants.STATUS_UPTODATE): {
				Toast.makeText(DriverViewActivity.this, "Up to date",
						Toast.LENGTH_LONG).show();
				break;
			}
			case (Constants.STATUS_NOTRIP): {
				Toast.makeText(DriverViewActivity.this, "No trip",
						Toast.LENGTH_LONG).show();
				DriverViewActivity.this.finish();
				break;
			}
			case (Constants.STATUS_HASENDED): {
				Toast.makeText(DriverViewActivity.this, "Trip ended",
						Toast.LENGTH_LONG).show();
				DriverViewActivity.this.finish();
				break;
			}
			case (Constants.STATUS_INVALID_USER):
				Toast.makeText(DriverViewActivity.this, "Invalid user",
						Toast.LENGTH_LONG).show();
				break;
			}
			break;

		case (R.id.mi_updateData):
			vhikeDialogs.getInstance().getUpdateDataDialog(context).show();
			break;
		}
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
