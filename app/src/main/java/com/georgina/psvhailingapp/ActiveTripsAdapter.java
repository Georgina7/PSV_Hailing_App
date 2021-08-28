package com.georgina.psvhailingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActiveTripsAdapter extends RecyclerView.Adapter<ActiveTripsAdapter.ViewHolder>{

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ArrayList<Trip> activeTrips;
    private Context myContext;

    ActiveTripsAdapter(ArrayList<Trip> mTripsData, Context context) {
        this.activeTrips = mTripsData;
        this.myContext = context;

    }

    @NonNull
    @Override
    public ActiveTripsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new ActiveTripsAdapter.ViewHolder(LayoutInflater.from(myContext).inflate(R.layout.driver_active_trip_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull ActiveTripsAdapter.ViewHolder holder, int position) {
        Trip availableTrip = activeTrips.get(position);
        holder.bindTo(availableTrip);

    }

    @Override
    public int getItemCount() {
        return activeTrips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView riderName, callRider;

        public ViewHolder(View itemView) {
            super(itemView);
            riderName = itemView.findViewById(R.id.active_trip_pwd_name);
            callRider = itemView.findViewById(R.id.call_rider);
        }

        public void bindTo(Trip currentTrip) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Users").child(currentTrip.getPwdID());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    riderName.setText(snapshot.child("fullName").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
