package com.georgina.psvhailingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.ViewHolder>{
    private ArrayList<String> stopsData;
    private Context myContext;

    StopsAdapter(ArrayList<String> mStopsData, Context context) {
        this.stopsData = mStopsData;
        this.myContext = context;

    }

    @NonNull
    @Override
    public StopsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StopsAdapter.ViewHolder(LayoutInflater.from(myContext).inflate(R.layout.stop_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull StopsAdapter.ViewHolder holder, int position) {
        String availableStop = stopsData.get(position);
        holder.bindTo(availableStop);
    }

    @Override
    public int getItemCount() {
        return stopsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mStop;

        public ViewHolder(View itemView) {
            super(itemView);
            mStop = itemView.findViewById(R.id.stop_point);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }

        public void bindTo(String currentStop) {
            mStop.setText(currentStop);
        }
    }
}
