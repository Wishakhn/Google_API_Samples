package com.googlemap.multiplemarkersgooglemap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MarkerActivity extends AppCompatActivity {
GoogleMap markerMap;
LatLngBounds.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initMAP();
            }
        });
    }

    private void initMAP() {
        SupportMapFragment map_fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.markermap);
        map_fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                markerMap = googleMap;
                markerMap.setMyLocationEnabled(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        makeMarks();
                    }
                });

            }
        });

    }
    BitmapDescriptor mark;
    void makeMarks(){
        mark = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        List<Marker> markes = new ArrayList<>();
        Marker Delhi = markerMap.addMarker(new MarkerOptions().position(new LatLng(
                28.61, 77.2099)).title("Delhi").icon(mark));
        Marker Chaandigarh = markerMap.addMarker(new MarkerOptions().position(new LatLng(
                30.75, 76.78)).title("Chandigarh").icon(mark));
        Marker SriLanka = markerMap.addMarker(new MarkerOptions().position(new LatLng(
                7.000, 81.0000)).title("Sri Lanka").icon(mark));
        Marker America = markerMap.addMarker(new MarkerOptions().position(new LatLng(
                38.8833, 77.0167)).title("America").icon(mark));
        Marker Arab = markerMap.addMarker(new MarkerOptions().position(new LatLng(
                24.000, 45.000)).title("Arab").icon(mark));

        markes.add(Delhi);
        markes.add(SriLanka);
        markes.add(America);
        markes.add(Arab);
        markes.add(Chaandigarh);
        builder = new LatLngBounds.Builder();
        for (Marker m : markes) {
            builder.include(m.getPosition());
        }

    }
}
