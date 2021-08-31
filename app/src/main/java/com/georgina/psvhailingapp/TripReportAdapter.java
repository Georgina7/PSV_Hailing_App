package com.georgina.psvhailingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TripReportAdapter extends RecyclerView.Adapter<TripReportAdapter.ViewHolder>{

    private ArrayList<Trip> tripReports;
    private Activity myContext;
    private ArrayList<String> tripIDs;
    private String sourceActivity;

    TripReportAdapter(ArrayList<Trip> mTripsData, Activity context, ArrayList<String> tripIDs, String sourceActivity) {
        this.tripReports = mTripsData;
        this.myContext = context;
        this.tripIDs = tripIDs;
        this.sourceActivity = sourceActivity;
    }

    @NonNull
    @Override
    public TripReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TripReportAdapter.ViewHolder(LayoutInflater.from(myContext).inflate(R.layout.trips_report_item_pwd,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TripReportAdapter.ViewHolder holder, int position) {
        Trip availableTrip = tripReports.get(position);
        String currentTripID = tripIDs.get(position);
        holder.bindTo(availableTrip, currentTripID);
    }

    @Override
    public int getItemCount() {
        return tripReports.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView destination, date, status;

        public ViewHolder(View itemView) {
            super(itemView);
            destination = itemView.findViewById(R.id.destination_report);
            date = itemView.findViewById(R.id.trip_date_report);
            status = itemView.findViewById(R.id.trip_status_report);

        }

        public void bindTo(Trip currentTrip, String currentTripID) {
            destination.setText(currentTrip.getDestination());
            date.setText(currentTrip.getDate_time());
            if(currentTrip.getStatus().equals("pending")){
                status.setTextColor(Color.rgb(64,224,208));
                status.setText(currentTrip.getStatus());
            }else if(currentTrip.getStatus().equals("completed")){
                status.setTextColor(Color.GREEN);
                status.setText(currentTrip.getStatus());
            }else if(currentTrip.getStatus().equals("started")){
                status.setTextColor(Color.GREEN);
                status.setText(currentTrip.getStatus());
            }else {
                status.setTextColor(Color.RED);
                status.setText("Cancelled");
            }


            if(sourceActivity.equals("PWD")){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(currentTrip.getStatus().equals("pending") || currentTrip.getStatus().equals("started")){
                            Intent intent = new Intent(myContext, PassengerTripActivity.class);
                            intent.putExtra("TripKey", currentTripID);
                            myContext.startActivity(intent);
                        }else{
                            Intent intent = new Intent(myContext, SingleTripHistoryPWDActivity.class);
                            intent.putExtra("Status", currentTrip.getStatus());
                            intent.putExtra("DriverID", currentTrip.getDriverID());
                            intent.putExtra("Source", currentTrip.getSource());
                            intent.putExtra("Destination", currentTrip.getDestination());
                            intent.putExtra("Date", currentTrip.getDate_time());
                            //Toast.makeText(myContext, "Trip Clicked", Toast.LENGTH_SHORT).show();
                            myContext.startActivity(intent);
                        }

                    }
                });
            }else{
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(currentTrip.getStatus().equals("pending") || currentTrip.getStatus().equals("started")){
                            Intent intent = new Intent(myContext, DriverMapActivity.class);
                            intent.putExtra("TripKey", currentTripID);
                            intent.putExtra("SELECTED_TRIP_SOURCE", currentTrip.getSource());
                            intent.putExtra("SELECTED_TRIP_DEST", currentTrip.getDestination());
                            myContext.startActivity(intent);
                        }else{
                            Intent intent = new Intent(myContext, DriverTripDetailsActivity.class);
                            intent.putExtra("Status", currentTrip.getStatus());
                            intent.putExtra("PWDID", currentTrip.getPwdID());
                            intent.putExtra("Source", currentTrip.getSource());
                            intent.putExtra("Destination", currentTrip.getDestination());
                            intent.putExtra("Date", currentTrip.getDate_time());
                            //Toast.makeText(myContext, "Trip Clicked", Toast.LENGTH_SHORT).show();
                            myContext.startActivity(intent);
                        }
                    }
                });
            }
        }
    }
}
