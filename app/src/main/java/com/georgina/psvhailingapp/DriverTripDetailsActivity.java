package com.georgina.psvhailingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverTripDetailsActivity extends AppCompatActivity {

    private TextView pwdName, date, source, destination;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_trip_details);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBarDriverTripHistoryDetail);
        topAppBar.setBackgroundColor(getResources().getColor(R.color.psv_color_accent));
        topAppBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DriverTripDetailsActivity.this, SeatBookingsActivity.class );
                        startActivity(intent);
                        finish();
                    }
                }
        );

        firebaseDatabase = FirebaseDatabase.getInstance();

        pwdName = findViewById(R.id.pwd_name_text_view);
        date = findViewById(R.id.pwd_time_date_text_view);
        source = findViewById(R.id.pwd_source_text_view);
        destination = findViewById(R.id.pwd_destination_text_view);

        date.setText(getIntent().getStringExtra("Date"));
        source.setText(getIntent().getStringExtra("Source"));
        destination.setText(getIntent().getStringExtra("Destination"));
        if(getIntent().getStringExtra("Status").equals("cancelled - driver")){
            pwdName.setText("You Cancelled");
            pwdName.setTextColor(Color.RED);
        }else if(getIntent().getStringExtra("Status").equals("cancelled - pwd")){
            pwdName.setText("Rider Cancelled");
            pwdName.setTextColor(Color.RED);
        }else{
            DatabaseReference userRef = firebaseDatabase.getReference("Users").child(getIntent().getStringExtra("PWDID"));
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pwdName.setText("Your ride with " + snapshot.child("fullName").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //pwdName.setText("Your ride with " + getIntent().getStringExtra());
        }

    }
}