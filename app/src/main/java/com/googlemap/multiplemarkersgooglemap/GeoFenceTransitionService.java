package com.googlemap.multiplemarkersgooglemap;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

class GeoFenceTransitionService extends IntentService {

    String geofence_details;

    public GeoFenceTransitionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geo_event = GeofencingEvent.fromIntent(intent);
        if (geo_event.hasError())
        {
            String erorr_is = String.valueOf(geo_event.getErrorCode());
            Log.d("", "Error is ::" + erorr_is);
            return;
        }
        int geotransition = geo_event.getGeofenceTransition();
        if (geotransition == Geofence.GEOFENCE_TRANSITION_ENTER || geotransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            List<Geofence> geofence_list = geo_event.getTriggeringGeofences();
            geofence_details = getGeofenceDetails(geotransition, geofence_list);
        }
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

        return user_status + TextUtils.join(", ", trigger);
    }


}
