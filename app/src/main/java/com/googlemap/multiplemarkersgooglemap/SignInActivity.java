package com.googlemap.multiplemarkersgooglemap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignInActivity extends AppCompatActivity {

    /********************** Strings **********************/
    private static final int RC_SIGN_IN = 191;
    private static final String TAG = "GooleSignTAG";
    private static final String GOOGLE_SIGN_IN = "google_data";
    private static final String PIC_URI = "Pic_uri";
    private static final String USERNAME = "usernamee";
    private static final String LOGED_EMAIL = "user_email";


    /******************* GOOGLE Attributes******************/
    Button button;
    GoogleSignInOptions gso;
    GoogleSignInClient googleSignInClient;

    /************** OTher Attributes ************************/
    ProgressDialog pDialog;
    ImageView profileImage;
    TextView name, email;
    Button signout;
    LinearLayout signin_container, signinscreen;
    String usename ="";
    String mail="" ;
    String pic ="";
    Uri pic_uri ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();
    }

    private void initViews() {
        pDialog = new ProgressDialog(SignInActivity.this);
        profileImage = findViewById(R.id.profileImage);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        signout = findViewById(R.id.signout);
        button = findViewById(R.id.sign_in_button);

        signin_container = findViewById(R.id.signin_container);
        signinscreen = findViewById(R.id.signinscreen);
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        System.out.println("Google Signin Option::"+gso.toString());


        googleSignInClient = GoogleSignIn.getClient(this,gso);
        System.out.println("Google Client::"+googleSignInClient.toString());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Button Clicked Successfully");
                signIn();
            }
        });

    }
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        System.out.println("INTENT SEND SUCCESSFULLY");
    }
    private void HandleSigninGoogle(Task<GoogleSignInAccount> task) {
        displayProgressDialog();
        System.out.println("response::"+task.getResult());
        /* if (task.isSuccessful()){*/
//        hideProgressDialog();
        GoogleSignInAccount googleAccount = null;
        try {
            googleAccount = task.getResult(ApiException.class);
            System.out.println("Google Account Results::"+googleAccount.toString());
        } catch (ApiException e) {
            e.printStackTrace();
            Log.d(TAG,"Errror ::"+e.toString());
            System.out.println("Exception1111 ::"+e.toString());
        }
        if (googleAccount!=null){
            if (signin_container.getVisibility()== View.VISIBLE){
                signin_container.setVisibility(View.GONE);
            }
            if (signinscreen.getVisibility()==View.GONE){
                signinscreen.setVisibility(View.VISIBLE);
            }
            System.out.println("Google USERNAME::"+googleAccount.getDisplayName());
            System.out.println("Google Email::"+googleAccount.getEmail());
            System.out.println("Google Photo::"+googleAccount.getPhotoUrl());

            usename = googleAccount.getDisplayName();
            mail = googleAccount.getEmail();
            pic_uri=googleAccount.getPhotoUrl();
            Glide.with(this).load(pic_uri).into(profileImage);
            name.setText(usename);
            email.setText(mail);

        }
        //}
        else {
            hideProgressDialog();
            Toast.makeText(this, "Sorry the Logging in Failed", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == RC_SIGN_IN) {
            System.out.println("Data ::"+data.toString());
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
         HandleSigninGoogle(task);
        }
    }

    private void displayProgressDialog() {
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

    }
    private void hideProgressDialog() {
        pDialog.dismiss();
    }

}
