package com.georgina.psvhailingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PassengerTripActivity extends AppCompatActivity {

    private TextView matatuPlate,driverName,driverTimeEstimate,driverContact;

    private String trip_key;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_trip);
        trip_key = getIntent().getStringExtra("TripKey");
        Toast.makeText(getApplicationContext(), trip_key, Toast.LENGTH_SHORT).show();

        getSupportFragmentManager().beginTransaction().replace(R.id.pwd_trip_map_fragment_container,new PassengerTripMapsFragment()).commit();

        matatuPlate = findViewById(R.id.match_driver_matatu_plate);
        driverName = findViewById(R.id.match_driver_name);
        driverTimeEstimate = findViewById(R.id.driver_estimate_arrival_time);
        driverContact = findViewById(R.id.call_driver);

        String driverID = getIntent().getStringExtra("DriverID");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Drivers").child(driverID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matatuPlate.setText(snapshot.child("licenceNo").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = firebaseDatabase.getReference("Users").child(driverID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                driverName.setText(snapshot.child("fullName").getValue().toString());
                String contact = snapshot.child("number").getValue().toString();
                String profileImage = snapshot.child("profileImagePath").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = firebaseDatabase.getReference("Trips").child(trip_key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Find a way to calculate time estimate using distance stored in the trip instance
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void backToMain(View view) {
        Intent intent = new Intent(PassengerTripActivity.this, PassengerMapActivity.class);
        startActivity(intent);
    }

    public void cancelTrip(View view) {
        DatabaseReference tripRef = firebaseDatabase.getReference("Trips").child(trip_key).child("status");
        tripRef.setValue("canceled");
        Intent intent = new Intent(PassengerTripActivity.this, PassengerMapActivity.class);
        startActivity(intent);
    }

    public void makePayment(View view) {
    }
}