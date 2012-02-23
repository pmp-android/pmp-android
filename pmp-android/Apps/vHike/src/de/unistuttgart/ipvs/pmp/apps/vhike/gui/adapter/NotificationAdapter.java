package de.unistuttgart.ipvs.pmp.apps.vhike.gui.adapter;

import java.util.List;

import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.apps.vhike.Constants;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.Controller;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.dialog.vhikeDialogs;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.ViewModel;
import de.unistuttgart.ipvs.pmp.apps.vhike.gui.maps.ViewObject;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Model;
import de.unistuttgart.ipvs.pmp.apps.vhike.model.Profile;
import de.unistuttgart.ipvs.pmp.apps.vhike.tools.QueryObject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Handles list elements where drivers or passengers are added/removed through
 * finding/accepting/denying/sending offers to hitchhikers
 * 
 * mWhichHitcher indicates which list is to handle. 0 handles passengers list 1
 * handles drivers list
 * 
 * Also gives user opportunity to open profile of a found hitchhiker
 * 
 * @author Alexander Wassiljew, Andre Nguyen
 * 
 */
public class NotificationAdapter extends BaseAdapter {
    
    private Context context;
    private List<Profile> hitchhikers;
    private Profile hitchhiker;
    private int mWhichHitcher;
    private int userID;
    private Controller ctrl;
    
    
    public NotificationAdapter(Context context, List<Profile> hitchhikers, int whichHitcher) {
        this.context = context;
        this.hitchhikers = hitchhikers;
        mWhichHitcher = whichHitcher;
        ctrl = new Controller();
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
        userID = hitchhiker.getID();
        
        /* load the layout from the xml file */
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout entryView = (LinearLayout) inflater.inflate(R.layout.hitchhiker_list, null);
        
        Button dismiss = (Button) entryView.findViewById(R.id.dismissBtn);
        RatingBar noti_rb = (RatingBar) entryView.findViewById(R.id.notification_ratingbar);
        final TextView name = (TextView) entryView.findViewById(R.id.TextView_Name);
        final Button accept_invite = (Button) entryView.findViewById(R.id.acceptBtn);
        
        name.setText(hitchhiker.getUsername());
        name.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                List<QueryObject> lqo = Model.getInstance().getQueryHolder();
                userID = lqo.get(position).getUserid();
                Log.i(this, "ProfileID: " + userID + ", Position: " + position);
                
                vhikeDialogs.getInstance().getProfileDialog(context, userID).show();
            }
        });
        
        noti_rb.setRating((float) hitchhiker.getRating_avg());
        
        List<ViewObject> lqo = ViewModel.getInstance().getLVO();
        Log.i(this, "Position: " + position);
        final ViewObject actObject = lqo.get(position);
        
        if (mWhichHitcher == 0) {
            dismiss.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    
                    ViewModel.getInstance().addToBanned(actObject.getViewObjectToBann());
                    ViewModel.getInstance().updateView(mWhichHitcher);
                    if (actObject.getStatus() == Constants.V_OBJ_SATUS_PICKED_UP) {
                        ViewModel.getInstance().setNewNumSeats(ViewModel.getInstance().getNumSeats() + 1);
                        ctrl.tripUpdateData(Model.getInstance().getSid(), Model.getInstance().getTripId(), ViewModel
                                .getInstance().getNumSeats());
                    }
                }
            });
        } else {
            dismiss.setOnClickListener(actObject.getDenieOfferClickListener());
        }
        
        switch (actObject.getStatus()) {
            case Constants.V_OBJ_SATUS_FOUND:
                accept_invite.setOnClickListener(actObject.getOnClickListener(mWhichHitcher));
                break;
            case Constants.V_OBJ_SATUS_INVITED:
                accept_invite.setOnClickListener(actObject.getOnClickListener(mWhichHitcher));
                accept_invite.setBackgroundResource(R.drawable.bg_waiting);
                
                break;
            case Constants.V_OBJ_SATUS_AWAIT_ACCEPTION:
                accept_invite.setOnClickListener(actObject.getOnClickListener(mWhichHitcher));
                accept_invite.setBackgroundResource(R.drawable.bg_waiting);
                break;
            case Constants.V_OBJ_SATUS_ACCEPTED:
                accept_invite.setOnClickListener(actObject.getOnClickListener(mWhichHitcher));
                accept_invite.setBackgroundResource(R.drawable.bg_check);
                break;
            case Constants.V_OBJ_SATUS_PICKED_UP:
                accept_invite.setOnClickListener(actObject.getOnClickListener(mWhichHitcher));
                accept_invite.setBackgroundResource(R.drawable.bg_disabled);
                name.setTextColor(Color.BLUE);
                break;
        }
        
        return entryView;
    }
    
}
