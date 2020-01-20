package com.googlemap.multiplemarkersgooglemap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.text.TextUtils;


import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import static com.googlemap.multiplemarkersgooglemap.GeofenceApp.EXTRA_LOCATION;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {
String geofence_details;
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Reciever is started");
        Location location = intent.getParcelableExtra(EXTRA_LOCATION);
        if (location != null) {
            System.out.println("location is :"+location);
        }
//        GeoFenceTransitionJobService.enqueueWork(context,intent);
        GeofencingEvent geo_event = GeofencingEvent.fromIntent(intent);
        if (geo_event.hasError())
        {
            String erorr_is = String.valueOf(geo_event.getErrorCode());
            System.out.println("Error is ::" + erorr_is);
            return;
        }
        int geotransition = geo_event.getGeofenceTransition();
        /*if (geotransition == Geofence.GEOFENCE_TRANSITION_ENTER || geotransition == Geofence.GEOFENCE_TRANSITION_EXIT )
        {
            List<Geofence> geofence_list = geo_event.getTriggeringGeofences();
            geofence_details = getGeofenceDetails(geotransition, geofence_list);
        }*/
        List<Geofence> geofence_list = geo_event.getTriggeringGeofences();
        geofence_details = getGeofenceDetails(geotransition, geofence_list);
        System.out.println("GeoFence Details ::" + geofence_details);
    }
    private String getGeofenceDetails(int geotransition, List<Geofence> geofence_list) {
        List<String> trigger = new ArrayList<>();

        for (Geofence geofence : geofence_list)
        {
            trigger.add(geofence.getRequestId());
        }
        String user_status = null;
        if (geotransition == Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            user_status = "User is ENTERING";
        }
        if (geotransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            user_status = "User is EXITING";
        }
        if (geotransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            user_status = "User is DWELLING";
        }

        return user_status + TextUtils.join(", ", trigger);
    }

}
