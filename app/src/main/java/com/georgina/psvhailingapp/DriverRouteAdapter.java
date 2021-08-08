package com.georgina.psvhailingapp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import org.jetbrains.annotations.NotNull;

// FirebaseRecyclerAdapter is a class provided by
// FirebaseUI. it provides functions to bind, adapt and show
// database contents in a Recycler View

public class DriverRouteAdapter extends FirebaseRecyclerAdapter<DriverDetails,DriverRouteAdapter.ViewHolder> {

    public DriverRouteAdapter(@NonNull @NotNull FirebaseRecyclerOptions<DriverDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull DriverRouteAdapter.ViewHolder holder, int position, @NonNull @NotNull DriverDetails driverDetails) {
        holder.matatuPlate.setText(driverDetails.getMatatuPlate());
        holder.seats.setText(driverDetails.getSeats());
        holder.licenceNo.setText(driverDetails.getLicenceNo());
        holder.routes.setText(driverDetails.getRoutes());
        holder.status.setText(driverDetails.getAvailability());
    }

    @NonNull
    @NotNull
    @Override
    public DriverRouteAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.routes_list,parent,false);
        return new DriverRouteAdapter.ViewHolder(v);
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView matatuPlate,licenceNo,routes,status,seats;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            matatuPlate= itemView.findViewById(R.id.no_plate);
            licenceNo = itemView.findViewById(R.id.driver_name);
            routes = itemView.findViewById(R.id.Start);
            status = itemView.findViewById(R.id.destination);
            seats = itemView.findViewById(R.id.driver_number);
        }
    }
}


//
//public class DriverRouteAdapter extends RecyclerView.Adapter<DriverDetails,DriverRouteAdapter.ViewHolder>{
//
//    Context context;
//    ArrayList<DriverDetails> list;
//    LayoutInflater layoutInflater;
//
//    public DriverRouteAdapter(Context context, ArrayList<DriverDetails> list) {
//        this.context = context;
//        layoutInflater = LayoutInflater.from(context);
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(context).inflate(R.layout.routes_list,parent,false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(DriverDetails.class,DriverRouteAdapter.ViewHolder holder, int position) {
//
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder{
//
//        public ViewHolder(@NonNull @NotNull View itemView) {
//            super(itemView);
//            matatuPlate= itemView.findViewById(R.id.no_plate);
//            licenceNo = itemView.findViewById(R.id.driver_name);
//            routes = itemView.findViewById(R.id.Start);
//            status = itemView.findViewById(R.id.destination);
//            seats = itemView.findViewById(R.id.driver_number);
//        }
//    }
//
//
//
//
//}