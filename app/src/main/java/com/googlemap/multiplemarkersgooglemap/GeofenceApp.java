package com.googlemap.multiplemarkersgooglemap;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class GeofenceApp extends Application {

    static  final String CHANNEL_ID ="Geofence_notify";
    static  final String PACKAGE ="com.example.geofencingwithintentservice";
    static final String ACTION_BROADCAST = PACKAGE + ".broadcast";
    static final String EXTRA_LOCATION = PACKAGE + ".location";
    static final String STARTED_NOTIFICATION = PACKAGE +
            ".started_from_notification";
    static final int NOTIFICATION_ID = 12345888;
  public static Boolean Startservice = false;
    static NotificationManager manager;


    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }
void createNotification(){
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "JobIntent Geofence Service",
                NotificationManager.IMPORTANCE_DEFAULT
        );
         manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

}
}
