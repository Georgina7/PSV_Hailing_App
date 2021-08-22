package com.georgina.psvhailingapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class SelectPickupActivity extends AppCompatActivity {

    private RecyclerView stopsRecyclerView;
    private ArrayList<String> stopsData;
    private StopsAdapter stopsAdapter;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private String route;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pickup);

        Toolbar topAppBar = findViewById(R.id.topAppBar2);

        topAppBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SelectPickupActivity.this,PassengerMapActivity.class );
                        startActivity(intent);
                        //finish();
                    }
                }
        );

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Routes");

        route = getIntent().getStringExtra("Route");

        stopsRecyclerView = findViewById(R.id.recycler_pickup_stops);
        stopsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        stopsData = new ArrayList<>();
        stopsAdapter = new StopsAdapter(stopsData, getApplicationContext());
        stopsRecyclerView.setAdapter(stopsAdapter);
        initializeStopsData();

        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("Route"), Toast.LENGTH_SHORT).show();
    }

    private void initializeStopsData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stopsData.clear();
                Iterator<DataSnapshot> stops = snapshot.child(route).getChildren().iterator();
                while (stops.hasNext()){
                    DataSnapshot stop = stops.next();
                    stopsData.add(stop.getKey().toString());
                }
                stopsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}