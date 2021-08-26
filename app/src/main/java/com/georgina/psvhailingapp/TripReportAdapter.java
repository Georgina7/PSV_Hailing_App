package com.georgina.psvhailingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
    private Context myContext;

    TripReportAdapter(ArrayList<Trip> mTripsData, Context context) {
        this.tripReports = mTripsData;
        this.myContext = context;

    }

    @NonNull
    @Override
    public TripReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TripReportAdapter.ViewHolder(LayoutInflater.from(myContext).inflate(R.layout.trips_report_item_pwd,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TripReportAdapter.ViewHolder holder, int position) {
        Trip availableTrip = tripReports.get(position);
        holder.bindTo(availableTrip);
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

        public void bindTo(Trip currentTrip) {
            destination.setText(currentTrip.getDestination());
            date.setText(currentTrip.getDate_time());
            if(currentTrip.getStatus().equals("pending")){
                status.setTextColor(Color.rgb(64,224,208));
            }else if(currentTrip.getStatus().equals("completed")){
                status.setTextColor(Color.GREEN);
            }else if(currentTrip.getStatus().equals("cancelled")){
                status.setTextColor(Color.RED);
            }
            status.setText(currentTrip.getStatus());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(myContext, SingleTripHistoryPWDActivity.class);
                    Toast.makeText(myContext, "Trip Clicked", Toast.LENGTH_SHORT).show();
                    myContext.startActivity(intent);
                    //This intent doesn't work
                }
            });

        }
    }
}
