package com.georgina.psvhailingapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.ViewHolder>{
    private ArrayList<String> stopsData;
    private Context myContext;

    private int checkedPosition = 0;

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
        private ImageView mChecked;

        public ViewHolder(View itemView) {
            super(itemView);
            mStop = itemView.findViewById(R.id.stop_point);
            mChecked = itemView.findViewById(R.id.checked_stop);
        }

        public void bindTo(String currentStop) {
            if (checkedPosition == -1) {
                mChecked.setVisibility(View.GONE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    mChecked.setVisibility(View.VISIBLE);
                } else {
                    mChecked.setVisibility(View.GONE);
                }
            }

            mStop.setText(currentStop);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mChecked.setVisibility(View.VISIBLE);
                    Toast.makeText(myContext, currentStop, Toast.LENGTH_SHORT).show();
                    if (checkedPosition != getAdapterPosition()) {
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();
                    }
                }
            });

        }
    }

    public String getSelected() {
        if (checkedPosition != -1) {
            return stopsData.get(checkedPosition);
        }
        return null;
    }
}
