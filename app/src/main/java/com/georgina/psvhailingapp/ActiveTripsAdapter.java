package com.georgina.psvhailingapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
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
    private ArrayList<String> tripIDs;

    ActiveTripsAdapter(ArrayList<Trip> mTripsData, Context context, ArrayList<String> tripIDs) {
        this.activeTrips = mTripsData;
        this.myContext = context;
        this.tripIDs = tripIDs;
    }

    @NonNull
    @Override
    public ActiveTripsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new ActiveTripsAdapter.ViewHolder(LayoutInflater.from(myContext).inflate(R.layout.driver_active_trip_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull ActiveTripsAdapter.ViewHolder holder, int position) {
        Trip availableTrip = activeTrips.get(position);
        String currentTripId = tripIDs.get(position);
        holder.bindTo(availableTrip, currentTripId
        );

    }

    @Override
    public int getItemCount() {
        return activeTrips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView riderName, callRider;
        private Button cancelTrip, startTrip, confirmPayment;
        private LinearLayout buttonContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            riderName = itemView.findViewById(R.id.active_trip_pwd_name);
            callRider = itemView.findViewById(R.id.call_rider);
            cancelTrip = itemView.findViewById(R.id.cancel_driver_button);
            startTrip = itemView.findViewById(R.id.start_trip_button);
            confirmPayment = itemView.findViewById(R.id.confirm_payment_btn);
            buttonContainer = itemView.findViewById(R.id.linear_container);
        }

        public void bindTo(Trip currentTrip, String currentTripId) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Users").child(currentTrip.getPwdID());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    riderName.setText("Ride with " + snapshot.child("fullName").getValue().toString());
                    callRider.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + snapshot.child("number").getValue().toString()));
                            myContext.startActivity(intent);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if(currentTrip.getStatus().equals("started")){
                buttonContainer.setVisibility(View.GONE);
                confirmPayment.setVisibility(View.VISIBLE);
                confirmPayment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(myContext, R.style.Theme_DialogBox);
                        dialogBuilder.setTitle("Confirm");
                        dialogBuilder.setMessage("Ensure rider has paid before ending trip");
                        dialogBuilder.setBackground(myContext.getResources().getDrawable(R.drawable.alert_dialog_box));
                        dialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference tripRef = firebaseDatabase.getReference("Trips").child(currentTripId).child("status");
                                tripRef.setValue("completed");
                                Toast.makeText(myContext, "Trip Completed Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Just close dialog
                            }
                        });
                        dialogBuilder.show();

                    }
                });
            }else{
                cancelTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(myContext, R.style.Theme_DialogBox);
                        dialogBuilder.setTitle("Confirm");
                        dialogBuilder.setMessage("Are you sure you want to cancel trip?");
                        dialogBuilder.setBackground(myContext.getResources().getDrawable(R.drawable.alert_dialog_box));
                        dialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference tripRef = firebaseDatabase.getReference("Trips").child(currentTripId).child("status");
                                tripRef.setValue("cancelled - driver");
                                Toast.makeText(myContext, "Trip Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Just close dialog
                            }
                        });
                        dialogBuilder.show();

                    }
                });

                startTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(myContext, R.style.Theme_DialogBox);
                        dialogBuilder.setTitle("Confirm");
                        dialogBuilder.setMessage("Are you sure you want to start trip?");
                        dialogBuilder.setBackground(myContext.getResources().getDrawable(R.drawable.alert_dialog_box));
                        dialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference tripRef = firebaseDatabase.getReference("Trips").child(currentTripId).child("status");
                                tripRef.setValue("started");
                                Intent intent = ((Activity) myContext).getIntent();
                                intent.putExtra("SELECTED_TRIP_SOURCE", currentTrip.getSource());
                                intent.putExtra("SELECTED_TRIP_DEST", currentTrip.getDestination());
                                ((Activity)myContext).finish();
                                myContext.startActivity(intent);
                            }
                        });
                        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Just close dialog
                            }
                        });
                        dialogBuilder.show();
                    }

                });


            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = ((Activity) myContext).getIntent();
                    intent.putExtra("SELECTED_TRIP_SOURCE", currentTrip.getSource());
                    intent.putExtra("SELECTED_TRIP_DEST", currentTrip.getDestination());
                    ((Activity)myContext).finish();
                    myContext.startActivity(intent);
                }
            });

        }
    }
}
