package com.georgina.psvhailingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingleTripHistoryPWDActivity extends AppCompatActivity {

    private TextView source, destination, status, date;
    private CircleImageView driverProfileImg;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_trip_history_pwdactivity);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBarPWDTripDetails);

        topAppBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SingleTripHistoryPWDActivity.this, PWDTripReportsActivity.class );
                        startActivity(intent);
                        finish();
                    }
                }
        );

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(getIntent().getStringExtra("DriverID"));

        source = findViewById(R.id.from_detail);
        destination = findViewById(R.id.to_detail);
        status = findViewById(R.id.trip_status_detail);
        date = findViewById(R.id.trip_date_detail);
        driverProfileImg = findViewById(R.id.driver_profile_image_detail);

        source.setText(getIntent().getStringExtra("Source"));
        destination.setText(getIntent().getStringExtra("Destination"));
        date.setText(getIntent().getStringExtra("Date"));
        String trip_status = getIntent().getStringExtra("Status");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String driver_name = snapshot.child("fullName").getValue().toString();
                if(trip_status.equals("cancelled")){
                    status.setText("You Cancelled");
                    status.setTextColor(Color.RED);
                }else if(trip_status.equals("completed")){
                    status.setText("Your Ride with " + driver_name);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DBError", error.toString());

            }
        });

    }
}