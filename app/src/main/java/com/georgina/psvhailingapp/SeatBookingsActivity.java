package com.georgina.psvhailingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SeatBookingsActivity extends AppCompatActivity {

    private RecyclerView bookingsRecyclerView;
    private ArrayList<Trip> tripsData;
    private BookingsAdapter bookingsAdapter;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_bookings);

        bookingsRecyclerView = findViewById(R.id.recycler_seat_bookings);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        tripsData = new ArrayList<>();
        bookingsAdapter = new BookingsAdapter(tripsData, getApplicationContext());
        bookingsRecyclerView.setAdapter(bookingsAdapter);
        //mDatabase = FirebaseDatabase.getInstance().getReference("Trips");
        initializeData();
    }

    private void initializeData() {
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Trip object and use the values to update the UI
//                tripsData.clear();
//
//                //Trip trip = dataSnapshot.getValue(Trip.class);
//                Log.d("TripData","We got it");
//                // ..
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w("loadPost:onCancelled", databaseError.toException());
//            }
//        };

        tripsData.clear();
        tripsData.add(new Trip("Whjhffjhd", "hdvchc", "Madaraka", "Tamberi", "12/12/12", "2:00p.m", "In a wheelchair", "Pending", 1));
        bookingsAdapter.notifyDataSetChanged();
       Toast.makeText(getApplicationContext(), "Small Change", Toast.LENGTH_SHORT);

    }
}