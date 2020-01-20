package com.googlemap.multiplemarkersgooglemap;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import static com.googlemap.multiplemarkersgooglemap.GeofenceApp.ACTION_BROADCAST;
import static com.googlemap.multiplemarkersgooglemap.GeofenceApp.CHANNEL_ID;
import static com.googlemap.multiplemarkersgooglemap.GeofenceApp.EXTRA_LOCATION;
import static com.googlemap.multiplemarkersgooglemap.GeofenceApp.NOTIFICATION_ID;
import static com.googlemap.multiplemarkersgooglemap.GeofenceApp.STARTED_NOTIFICATION;
import static com.googlemap.multiplemarkersgooglemap.GeofenceApp.manager;
import static com.googlemap.multiplemarkersgooglemap.SelfLocateActivity.TAG;


public class LocationService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Location mLocation;

    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISEC = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISEC =
            UPDATE_INTERVAL_IN_MILLISEC / 2;
    private boolean mChangingConfiguration = false;
    private final IBinder mBinder = new LocalBinder();
    private Handler mServiceHandler;
    NotificationManager not_manager;
    GeofenceBroadcastReceiver reciever;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onUnbind(Intent intent) {
        if (!mChangingConfiguration) {


            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true;
    }

    @Override
    public void onRebind(Intent intent) {

        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        LocalBroadcastManager.getInstance(this).registerReceiver(reciever,
                new IntentFilter(ACTION_BROADCAST));
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        getLatestLocation();
        createLocationRequest();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onNewLocation(Location newLocation) {
        Log.i(TAG, "New location: " + newLocation);

        mLocation = newLocation;

        Intent changetext = new Intent(ACTION_BROADCAST);
        changetext.putExtra(EXTRA_LOCATION, mLocation);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(changetext);

        if (serviceIsRunningInForeground(this)) {
            manager.notify(NOTIFICATION_ID, getNotification());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean startedFromNotification = intent.getBooleanExtra(STARTED_NOTIFICATION,
                false);
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }
    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);

    }

    public class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    private void getLatestLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }

    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }


    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
        startService(new Intent(getApplicationContext(), LocationService.class));
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISEC);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISEC);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

     void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            stopSelf();
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    Notification getNotification(){
        Intent intent = new Intent(this, LocationService.class);
        intent.putExtra(STARTED_NOTIFICATION, true);
        String text ="Geofencing is on transition";
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        Notification notify = new Notification
                .Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notification)
//                .setContentIntent(pendingIntent)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(1, notify);
        return notify;

    }

}
