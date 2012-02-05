package de.unistuttgart.ipvs.pmp.apps.vhike.gui.adapter;

import java.util.List;

import com.google.android.maps.MapView;

import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.Controller;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.ProfileActivity;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.MapModel;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Model;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Profile;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.OfferObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.QueryObject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Handles list elements where drivers or passengers are added/removed through
 * finding/accepting/denying hitchhikers
 * 
 * mWhichHitcher indicates which list is to handle. 0 handles passengers list 1
 * handles drivers list
 * 
 * @author andres
 * 
 */
public class NotificationAdapter extends BaseAdapter {

	private Context context;
	private Controller ctrl;
	private List<Profile> hitchhikers;
	private Profile hitchhiker;
	private Profile me;
	private int mWhichHitcher;
	private int queryID;
	private int offerID;
	private int userID;
	private int driverID;
	private MapView mapView;

	public NotificationAdapter(Context context, List<Profile> hitchhikers,
			int whichHitcher, MapView mapView) {
		this.context = context;
		this.hitchhikers = hitchhikers;
		ctrl = new Controller();
		mWhichHitcher = whichHitcher;
		this.mapView = mapView;
	}

	@Override
	public int getCount() {
		return hitchhikers.size();
	}

	@Override
	public Object getItem(int position) {
		return hitchhikers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		hitchhiker = hitchhikers.get(position);
		
		Log.i(this, "POsition NOTI ADAPT: "+ position); 
		
		
		/* load the layout from the xml file */
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout entryView = (LinearLayout) inflater.inflate(
				R.layout.hitchhiker_list, null);

		Button dismiss = (Button) entryView.findViewById(R.id.dismissBtn);
		RatingBar noti_rb = (RatingBar) entryView
				.findViewById(R.id.notification_ratingbar);
		TextView name = (TextView) entryView.findViewById(R.id.TextView_Name);
		final Button accept_invite = (Button) entryView
				.findViewById(R.id.acceptBtn);
		
		final List<QueryObject> lqo = Model.getInstance().getQueryHolder();

		// determine which id to receive
		if (mWhichHitcher == 0) {

			queryID = lqo.get(position).getQueryid();
			
			userID = lqo.get(position).getUserid();
			Log.i(this, "USERID: " + userID);
			Log.i(this, "Position: " + position);
		} else {
			List<OfferObject> loo = Model.getInstance().getOfferHolder();
			offerID = loo.get(position).getOffer_id();
			driverID = loo.get(position).getUser_id();
		}

		if (Model.getInstance().isInInvitedList(userID)) {
			
			accept_invite.setBackgroundResource(R.drawable.bg_waiting);
			accept_invite.invalidate();
		}		

		dismiss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mWhichHitcher == 0) {
					// if (all seats taken) -> remove all
					// else -> JUST ONE
					// TODO: remove PassengerOverlay
					MapModel.getInstance().getHitchPassengers()
							.remove(position);
					MapModel.getInstance().getDriverOverlayList(mapView)
							.remove(position + 1);
					Model.getInstance().addToBannList(userID);
					mapView.invalidate();
					notifyDataSetChanged();
				} else {
					switch (ctrl.handleOffer(Model.getInstance().getSid(), 1,
							false)) {
					case Constants.STATUS_HANDLED:
						Toast.makeText(context, "HANDLED: DENY",
								Toast.LENGTH_SHORT).show();
						// remove driver from list if denied
						MapModel.getInstance().getHitchDrivers()
								.remove(position);
						MapModel.getInstance().getPassengerOverlayList(mapView)
								.remove(position + 1);
						mapView.invalidate();
						notifyDataSetChanged();
						break;
					case Constants.STATUS_INVALID_OFFER:
						Toast.makeText(context, "INVALID OFFER",
								Toast.LENGTH_SHORT).show();
						break;
					case Constants.STATUS_INVALID_USER:
						Toast.makeText(context, "INVALID_USER",
								Toast.LENGTH_SHORT).show();
						break;
					}
				}

			}
		});

		name.setText(hitchhiker.getUsername());
		name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ProfileActivity.class);
				List<QueryObject> lqo = Model.getInstance().getQueryHolder();
				List<OfferObject> loo = Model.getInstance().getOfferHolder();

				if (mWhichHitcher == 0) {
					userID = lqo.get(position).getUserid();
				} else {
					userID = loo.get(position).getUser_id();
				}

				if (mWhichHitcher == 0) {
					intent.putExtra("PASSENGER_ID", userID);
				} else {
					intent.putExtra("PASSENGER_ID", driverID);
				}

				intent.putExtra("MY_PROFILE", 1);

				context.startActivity(intent);
			}
		});

		noti_rb.setRating((float) hitchhiker.getRating_avg());
		me = Model.getInstance().getOwnProfile();

		accept_invite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				queryID = lqo.get(position).getQueryid();
				userID = lqo.get(position).getUserid();

				if (mWhichHitcher == 0) {
					switch (ctrl.sendOffer(Model.getInstance().getSid(), Model
							.getInstance().getTripId(), queryID,
							me.getUsername() + ": Need a ride?")) {
					case Constants.STATUS_SENT:
						Log.i(this, "P: " + position + ", " + queryID);
						accept_invite
								.setBackgroundResource(R.drawable.bg_waiting);
						Model.getInstance().addToInvitedUser(userID);
						Log.i(this, "USER ID ADDED: " + userID);
						//
						// notifyDataSetChanged();
						Toast.makeText(context, "STATUS_SENT",
								Toast.LENGTH_SHORT).show();

						break;
					case Constants.STATUS_INVALID_TRIP:
						Toast.makeText(context, "STATUS_INVALID_TRIP",
								Toast.LENGTH_SHORT).show();
						break;
					case Constants.STATUS_INVALID_QUERY:
						Toast.makeText(context, "INVALID_QUERY",
								Toast.LENGTH_SHORT).show();
						break;
					case Constants.STATUS_ALREADY_SENT:
						Log.i(this, "P: " + position + ", " + queryID);
						Toast.makeText(context, "ALREADY SENT",
								Toast.LENGTH_SHORT).show();
						break;
					}

				} else {

					switch (ctrl.handleOffer(Model.getInstance().getSid(),
							offerID, true)) {
					case Constants.STATUS_HANDLED:
						Toast.makeText(context, "HANDLED: ACCEPT",
								Toast.LENGTH_SHORT).show();
						// Entfernen
						notifyDataSetChanged();
						accept_invite
								.setBackgroundResource(R.drawable.bg_check);
						accept_invite.refreshDrawableState();
						accept_invite.invalidate();

						break;
					case Constants.STATUS_INVALID_OFFER:
						Toast.makeText(context, "INVALID_OFFER",
								Toast.LENGTH_SHORT).show();
						break;
					case Constants.STATUS_INVALID_USER:
						Toast.makeText(context, "INVALID_USER",
								Toast.LENGTH_SHORT).show();
						break;
					}
				}

				accept_invite.invalidate();

			}
		});

		return entryView;
	}

}
