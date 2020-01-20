package com.googlemap.multiplemarkersgooglemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
Button btn1,btn2,btn3,btn4,btng;
    private static final int REQUEST_PERMISSION_CODE = 20001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!CheckPermissions()){
            RequestPermissions();
        }
        initViews();

    }

    private void initViews() {
        btng = findViewById(R.id.btng);
        btng.setOnClickListener(this);
        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(this);
        btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(this);
        btn4 = findViewById(R.id.btn4);
        btn4.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:
                Intent placetent = new Intent(this,PlaceSearchActivity.class);
                startActivity(placetent);
                break;

                case R.id.btn2:
                    Intent selftent = new Intent(this,SelfLocateActivity.class);
                    startActivity(selftent);
                break;
                case R.id.btn3:
                    Intent marktent = new Intent(this,MarkerActivity.class);
                    startActivity(marktent);
                break;
                case R.id.btn4:
                    Intent geotent = new Intent(this,GeoFenceActivity.class);
                    startActivity(geotent);
                break;
                case R.id.btng:
                    Intent signtent = new Intent(this,SignInActivity.class);
                    startActivity(signtent);
                break;
        }

    }

    private boolean CheckPermissions() {
        int Location_Permission = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION");
        int Fine_Location_Permission = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION");
        int GPS_State_Permission = ContextCompat.checkSelfPermission(this, "com.google.android.providers.gsf.permission.READ_GSERVICES");
        return Location_Permission == 0 && Fine_Location_Permission == 0 && GPS_State_Permission == 0;
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION","com.google.android.providers.gsf.permission.READ_GSERVICES"}, REQUEST_PERMISSION_CODE);
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
