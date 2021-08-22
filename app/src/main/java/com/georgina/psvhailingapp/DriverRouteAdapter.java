package com.georgina.psvhailingapp;

// FirebaseRecyclerAdapter is a class provided by
// FirebaseUI. it provides functions to bind, adapt and show
// database contents in a Recycler View
//

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class DriverRouteAdapter extends RecyclerView.Adapter<DriverRouteAdapter.ViewHolder>{
        private ArrayList<String> routeData;
        private Context myContext;

        DriverRouteAdapter(ArrayList<String> mRouteData, Context context) {
        this.routeData = mRouteData;
        this.myContext = context;

    }

    @NonNull
    @NotNull
    @Override
    public DriverRouteAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(myContext).inflate(R.layout.routes_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DriverRouteAdapter.ViewHolder holder, int position) {
        String availableRoute = routeData.get(position);
        holder.bindTo(availableRoute);
    }

    @Override
    public int getItemCount() {
        return routeData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mStart,mDest;

        public ViewHolder(View inflate) {
            super(inflate);
            mStart = inflate.findViewById(R.id.start_place);
            mDest = inflate.findViewById(R.id.destination_place);
        }

        public void bindTo(String availableRoute) {
            String [] locations = availableRoute.split("-");
            mStart.setText(locations[0]);
            mDest.setText(locations[1]);
        }
    }
}
//public class DriverRouteAdapter extends RecyclerView.Adapter<DriverRouteAdapter.ViewHolder> {
//    private ArrayList<DriverDetails> routeData;
//    private Context myContext;
//
//    DriverRouteAdapter(ArrayList<DriverDetails> mRouteData, Context context) {
//        this.routeData = mRouteData;
//        this.myContext = context;
//    }
//
//    @NonNull
//    @Override
//    public DriverRouteAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        return new ViewHolder(LayoutInflater.from(myContext).inflate(R.layout.routes_list, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull @NotNull DriverRouteAdapter.ViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return routeData.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public ViewHolder(View inflate) {
//        }
//    }
//}
//
//    @Override
//    protected void onBindViewHolder(@NonNull @NotNull DriverRouteAdapter.ViewHolder holder, int position, @NonNull @NotNull DriverDetails driverDetails) {
//        holder.matatuPlate.setText(driverDetails.getMatatuPlate());
//        holder.seats.setText(driverDetails.getSeats());
//        holder.licenceNo.setText(driverDetails.getLicenceNo());
//        holder.routes.setText(driverDetails.getRoutes());
//        holder.status.setText(driverDetails.getAvailability());
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public DriverRouteAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.routes_list,parent,false);
//        return new DriverRouteAdapter.ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull @NotNull BookingsAdapter.ViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder
//    {
//        TextView matatuPlate,licenceNo,routes,status,seats;
//        public ViewHolder(@NonNull @NotNull View itemView) {
//            super(itemView);
//            matatuPlate= itemView.findViewById(R.id.no_plate);
//            licenceNo = itemView.findViewById(R.id.driver_name);
//            routes = itemView.findViewById(R.id.start_place);
//            status = itemView.findViewById(R.id.destination_place);
//            seats = itemView.findViewById(R.id.driver_number);
//        }
//    }



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