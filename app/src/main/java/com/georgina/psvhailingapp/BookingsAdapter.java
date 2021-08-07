package com.georgina.psvhailingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder>{

    private ArrayList<Trip> tripsData;
    private Context myContext;

    BookingsAdapter(ArrayList<Trip> mTripsData, Context context){
        this.tripsData = mTripsData;
        this.myContext = context;
    }

    @NonNull
    @NotNull
    @Override
    public BookingsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(myContext).inflate(R.layout.booking_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BookingsAdapter.ViewHolder holder, int position) {
        Trip currentTrip = tripsData.get(position);
        holder.bindTo(currentTrip);
    }

    @Override
    public int getItemCount() {
        return tripsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mPWDName, mPWDContact, mDateTime, mSource, mDestination;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mPWDName = itemView.findViewById(R.id.pwd_name_text_view);
            mPWDContact = itemView.findViewById(R.id.pwd_contact_text_view);
            mDateTime = itemView.findViewById(R.id.pwd_time_date_text_view);
            mSource = itemView.findViewById(R.id.pwd_source_text_view);
            mDestination = itemView.findViewById(R.id.pwd_destination_text_view);

        }

        public void bindTo(Trip currentTrip){
            String pwdID = currentTrip.getPwdID();
            mPWDName.setText("Maria Booku");
            mPWDContact.setText("+255 8989 9899");
            mDateTime.setText(currentTrip.getDate() + currentTrip.getTime());
            mSource.setText(currentTrip.getSource());
            mDestination.setText(currentTrip.getDestination());
        }
    }



}
