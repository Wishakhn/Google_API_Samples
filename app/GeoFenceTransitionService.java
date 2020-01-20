package com.example.geofencingwithintentservice;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import static com.example.geofencingwithintentservice.GeofenceApp.CHANNEL_ID;
import static com.example.geofencingwithintentservice.GeofenceApp.NOTIFICATION_ID;
import static com.example.geofencingwithintentservice.GeofenceApp.STARTED_NOTIFICATION;
import static com.example.geofencingwithintentservice.GeofenceApp.manager;

public class GeoFenceTransitionService extends JobIntentService {


    private static final int JOB_ID = 573;
    private static final String TAG = "ForeGorundGeofence";
    String geofence_details;
    private boolean mChangingConfiguration = false;
//    private final IBinder mBinder = new LocalBinder();
    private Handler mServiceHandler;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, JobIntentService.class, JOB_ID, intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        if (serviceIsRunningInForeground(this)){
            manager.notify(NOTIFICATION_ID, getNotification());
        }
        System.out.println("Service is started");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
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
        startForeground(NOTIFICATION_ID, getNotification());
        manager.notify(NOTIFICATION_ID, getNotification());


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
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.started) {
                    return true;
                }
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    Notification getNotification() {
        Intent intent = new Intent(this, GeoFenceTransitionService.class);
        intent.putExtra(STARTED_NOTIFICATION, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        Notification notify = new Notification
                .Builder(this, CHANNEL_ID)
                .setContentTitle("Job Intent Service")
                .setContentText(geofence_details)
                .setSmallIcon(R.drawable.person)
                .setContentIntent(pendingIntent)
                .setTicker(geofence_details)
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(1, notify);
        return notify;

    }
    void startgeofence_service(){
        startService(new Intent(getApplicationContext(), GeoFenceTransitionService.class));
        System.out.println("Service has started");
    }
    void stopgeofence_service(){
        System.out.println("Service has stopped");
        stopSelf();}
}
