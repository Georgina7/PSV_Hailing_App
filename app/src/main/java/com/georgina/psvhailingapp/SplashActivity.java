package com.georgina.psvhailingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class SplashActivity extends AppCompatActivity {

    private Handler handler;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        //Initialize firebase user
        mCurrentUser = mAuth.getCurrentUser();
        try {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){ }
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCurrentUser != null){
                    checkIfUserIsDriver();
                }
                else{
                    sendUserToLogin();
                }

            }
        },1000);
    }
    private void checkIfUserIsDriver(){

        String user_id = mCurrentUser.getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference driver_idRef = database.child("Driver").child(user_id);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    sendDriverToMain();
                }
                else{
                    sendUserToMain();
                }
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {}
        };
        driver_idRef.addListenerForSingleValueEvent(eventListener);
    }
    public void sendUserToLogin() {
        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
    private void sendUserToMain(){
        Intent mainIntent = new Intent(SplashActivity.this,PassengerMapActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void sendDriverToMain(){
        Intent mainIntent = new Intent(SplashActivity.this,DriverMapActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}