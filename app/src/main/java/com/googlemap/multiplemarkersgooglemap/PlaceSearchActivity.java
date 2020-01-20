package com.googlemap.multiplemarkersgooglemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class PlaceSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);
        initviews();
        if (!CheckPermissions()) {
            RequestPermissions();
            Log.d(TAG, "Required Permissions ::" + CheckPermissions());
        }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initMAP();
                    setListeners();
                }
            });

            Log.d(TAG, "Permissions Granted ::" + CheckPermissions());

    }


        static final String TAG = "MapActivity";
        private static final int REQUEST_PERMISSION_CODE = 20001;
        GoogleMap testMaps;
        ImageView locationsearch, searchicon;
        EditText searchinput;
        RelativeLayout search_container;

        static float zoom = 18.0f;


        void initviews() {
            locationsearch = findViewById(R.id.locationsearch);
            searchicon = findViewById(R.id.searchicon);
            searchinput = findViewById(R.id.searchinput);
            search_container = findViewById(R.id.search_container);
            String apiKey = getString(R.string.google_maps_key);
            if (apiKey.isEmpty()) {
                Toast.makeText(this, "Your API KEY is MISSING", LENGTH_SHORT).show();
                return;
            }

        }

        void setListeners() {
            locationsearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (locationsearch.getVisibility() == View.VISIBLE) {
                        locationsearch.setVisibility(View.GONE);
                    }
                    if (search_container.getVisibility() == View.GONE) {
                        search_container.setVisibility(View.VISIBLE);
                    }

                }
            });
            searchicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (locationsearch.getVisibility() == View.GONE) {
                        locationsearch.setVisibility(View.VISIBLE);
                    }
                    if (search_container.getVisibility() == View.VISIBLE) {
                        search_container.setVisibility(View.GONE);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            goto_Location();
                        }
                    });

                }
            });


        }

        private void goto_Location() {
            String address = searchinput.getText().toString();
            Geocoder geocoder = new Geocoder(PlaceSearchActivity.this);
            List<Address> lists = new ArrayList<>();
            try {
                lists = geocoder.getFromLocationName(address, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (lists.size() > 0) {
                final Address address1 = lists.get(0);
                Log.d(TAG, "found Location ::" + address1.toString());
                final LatLng latlng = new LatLng(address1.getLatitude(), address1.getLatitude());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moveCamera_method(latlng, zoom, address1.getAddressLine(0));
                    }
                });
            }
        }

        private void initMAP() {
            SupportMapFragment map_fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.searchmap);
            map_fragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    testMaps = googleMap;
                    testMaps.setMyLocationEnabled(true);
                    testMaps.getUiSettings().setMyLocationButtonEnabled(false);
                }
            });

        }


        private void moveCamera_method(LatLng latLng, float zoom, String my_location) {
            Log.d(TAG, "CurrentLocation :: Longitutdeis :-" + latLng.longitude + "  _Latitudeis :-" + latLng.latitude);
            testMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            BitmapDescriptor mark = BitmapDescriptorFactory.fromResource(R.drawable.marker);
            MarkerOptions option = new MarkerOptions().position(latLng).title(my_location).icon(mark);
            testMaps.addMarker(option);

        }


        private boolean CheckPermissions() {
            int Location_Permission = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION");
            int Fine_Location_Permission = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION");
            return Location_Permission == 0 && Fine_Location_Permission == 0;
        }

        private void RequestPermissions() {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, REQUEST_PERMISSION_CODE);
        }

        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == REQUEST_PERMISSION_CODE) {
                if (grantResults.length <= 0 || grantResults[0] != 0) {
                    Toast.makeText(this, "Permissions are denied", LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permissions are Granted", LENGTH_SHORT).show();
                }
            }
        }




}
