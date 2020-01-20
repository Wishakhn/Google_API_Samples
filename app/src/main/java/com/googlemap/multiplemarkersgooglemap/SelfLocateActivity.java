package com.googlemap.multiplemarkersgooglemap;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.widget.Toast.LENGTH_SHORT;

public class SelfLocateActivity extends FragmentActivity  {

    private GoogleMap selfmap;
FusedLocationProviderClient fused_client;
    static final String TAG = "SelfLocationActivity";
    private static float zoom = 18.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_locate);
        initMAP();

    }
    private void initMAP() {
        SupportMapFragment map_fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.selfmap);
        map_fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                selfmap = googleMap;
                selfmap.setMyLocationEnabled(true);
               // selfmap.getUiSettings().setMyLocationButtonEnabled(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getmyDevice_Location();
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
                            moveCamera_method(latLng1, zoom, "User's Location");
                        }
                    });

                } else {
                    Toast.makeText(SelfLocateActivity.this, "Unable to load your current location on the MAP..!!", LENGTH_SHORT).show();
                }
            }
        });
    /*    final Task locations = flpc.getLastLocation();
        locations.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (locations.isSuccessful()) {
                    Location current_location = (Location) locations.getResult();
                    LatLng latLng1 = new LatLng(current_location.getLatitude(), current_location.getLongitude());
                    moveCamera_method(latLng1, zoom, "My Location");

                } else {
                    Toast.makeText(MapActivity.this, "Unable to load your current location on the MAP..!!", LENGTH_SHORT).show();
                }
            }
        });*/

    }
    private void moveCamera_method(LatLng latLng, float zoom, String my_location) {
        Log.d(TAG, "CurrentLocation :: Longitutdeis :-" + latLng.longitude + "  _Latitudeis :-" + latLng.latitude);
        selfmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        BitmapDescriptor user = BitmapDescriptorFactory.fromResource(R.drawable.person);
        MarkerOptions option = new MarkerOptions().position(latLng).title(my_location).icon(user);
        selfmap.addMarker(option);

    }



}
