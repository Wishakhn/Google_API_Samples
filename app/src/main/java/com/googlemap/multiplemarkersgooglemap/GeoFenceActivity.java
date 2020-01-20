package com.googlemap.multiplemarkersgooglemap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import static android.widget.Toast.LENGTH_SHORT;
import static com.googlemap.multiplemarkersgooglemap.GeofenceApp.Startservice;

public class GeoFenceActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener, ResultCallback<Status> {

    GoogleMap geomap;
    FusedLocationProviderClient fused_client;
    Circle geocircle;
    Marker geomarker, usermarker;
    ImageView geobtn;
    GeofencingRequest request;
    GoogleApiClient client;
    LocationRequest locreq;
    PendingIntent geoIntent;
LocationService loc_service=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence);
        initMAP();
        geobtn = findViewById(R.id.geobtn);
        geobtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startGeofencing();
                        StartLocationTrackingService();
                    }
                });
            }
        });
    }

    void setupClient() {
        client = new GoogleApiClient
                .Builder(GeoFenceActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startGeofencing() {
        if (geomarker != null) {
            Geofence geofence = createGeofence(geomarker.getPosition(), 700f);
            request = creatGeoRequest(geofence);
            addGeofence(geofence);

        } else {
            Toast.makeText(this, "Please Add a Marker first", LENGTH_SHORT).show();
        }
    }

    private void addGeofence(Geofence geofence) {
        LocationServices.GeofencingApi.addGeofences(client, request, createGeofencePendingIntent())
                .setResultCallback(this);

    }

    private PendingIntent createGeofencePendingIntent() {
        if (geoIntent != null) {
            return geoIntent;
        }
        Intent e = new Intent(this, GeofenceBroadcastReceiver.class);
        geoIntent = PendingIntent.getBroadcast(this, 0, e, PendingIntent.FLAG_UPDATE_CURRENT);
        return geoIntent;
    }

    private GeofencingRequest creatGeoRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private Geofence createGeofence(LatLng position, float v) {
        return new Geofence.Builder()
                .setRequestId("Test GeoFence")//requestId of user to track
                .setCircularRegion(position.latitude, position.longitude, v)
                .setExpirationDuration(60 * 60 * 1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private void initMAP() {
        SupportMapFragment map_fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.geomap);
        map_fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                geomap = googleMap;
                geomap.setMyLocationEnabled(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        geomap.setOnMapClickListener(GeoFenceActivity.this);
                        geomap.setOnMarkerClickListener(GeoFenceActivity.this);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getmyDevice_Location();
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupClient();
                    }
                });
            }
        });

    }

    void getmyDevice_Location() {
        fused_client = LocationServices.getFusedLocationProviderClient(this);
        fused_client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull final Task<Location> task) {
                if (task.isSuccessful()) {
                    final Location current_location = task.getResult();
                    final LatLng latLng1 = new LatLng(current_location.getLatitude(), current_location.getLongitude());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            moveCamera_method(latLng1, 18.0f, "User's Location");

                        }
                    });

                } else {
                    Toast.makeText(GeoFenceActivity.this, "Unable to load your current location on the MAP..!!", LENGTH_SHORT).show();
                }
            }
        });

    }

    private void moveCamera_method(LatLng latLng, float zoom, String my_location) {
        Log.d("empty", "CurrentLocation :: Longitutdeis :-" + latLng.longitude + "  _Latitudeis :-" + latLng.latitude);
        geomap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (usermarker != null) {
            usermarker.remove();
        }
        BitmapDescriptor user = BitmapDescriptorFactory.fromResource(R.drawable.person);
        MarkerOptions option = new MarkerOptions().position(latLng).title(my_location).icon(user);
        usermarker = geomap.addMarker(option);


    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (geocircle != null) {
            geocircle.remove();
        }
        get_Geofencemarker(latLng);

    }

    private void get_Geofencemarker(LatLng latLng) {
        BitmapDescriptor mark = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Geofence Position").icon(mark);
        if (geomap != null) {
            if (geomarker != null) {
                geomarker.remove();
            }
            geomarker = geomap.addMarker(options);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locreq = new LocationRequest().create();
        locreq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000);
        LocationServices.FusedLocationApi.requestLocationUpdates(client, locreq, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(this, "There is no Current Location", LENGTH_SHORT).show();
        } else {
            LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
            moveCamera_method(ltlng, 15.0f, "User's Location");
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        draw_GeoCircle();
    }

    private void draw_GeoCircle() {
        if (geocircle != null) {
            geocircle.remove();
        }
        CircleOptions crclopt = new CircleOptions()
                .center(geomarker.getPosition())
                .strokeColor(getResources().getColor(R.color.strock))
                .strokeWidth(3.5f)
                .fillColor(getResources().getColor(R.color.fill))
                .radius(700f);
        geocircle = geomap.addCircle(crclopt);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            loc_service = binder.getService();
            Startservice = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            loc_service = null;
            Startservice = false;
        }
    };

    private void StopLocationTrackingService() {
        Toast.makeText(this, "Serive has stop tracking location", Toast.LENGTH_SHORT).show();
        Startservice = false;
        loc_service.removeLocationUpdates();
    }

    private void StartLocationTrackingService() {
        Toast.makeText(this, "Service has start tracking location", Toast.LENGTH_SHORT).show();
        Startservice = true;
        loc_service.requestLocationUpdates();

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, LocationService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if (Startservice) {
            unbindService(mServiceConnection);
            Startservice = false;
        }
        super.onStop();
    }
}
