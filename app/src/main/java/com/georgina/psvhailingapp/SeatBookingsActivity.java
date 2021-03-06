package com.georgina.psvhailingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class SeatBookingsActivity extends AppCompatActivity {

    private RecyclerView tripsRecyclerView;
    private ArrayList<Trip> tripsData;
    private ArrayList<String> tripIDs;
    private TripReportAdapter tripReportAdapter;
    private String sourceActivity;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_bookings);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBarDriverTripHistory);
        topAppBar.setBackgroundColor(getResources().getColor(R.color.psv_color_accent));
        topAppBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SeatBookingsActivity.this, DriverMapActivity.class );
                        startActivity(intent);
                        finish();
                    }
                }
        );

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Trips");

        tripsRecyclerView = findViewById(R.id.recycler_seat_bookings);
        tripsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        tripsData = new ArrayList<>();
        tripIDs = new ArrayList<>();
        sourceActivity = "Driver";
        tripReportAdapter = new TripReportAdapter(tripsData, SeatBookingsActivity.this, tripIDs, sourceActivity);
        tripsRecyclerView.setAdapter(tripReportAdapter);
        //mDatabase = FirebaseDatabase.getInstance().getReference("Trips");
        initializeData();
    }

    private void initializeData() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                tripsData.clear();
                tripIDs.clear();
                Iterator<DataSnapshot> trips = snapshot.getChildren().iterator();
                while (trips.hasNext()){
                    DataSnapshot trip = trips.next();
                    if(trip.child("driverID").getValue().equals(mCurrentUser.getUid()))
                    {
                        tripsData.add(trip.getValue(Trip.class));
                        tripIDs.add(trip.getKey());
                    }
                }
                Collections.reverse(tripsData);
                Collections.reverse(tripIDs);
                tripReportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}