package com.georgina.psvhailingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;

public class SingleTripHistoryPWDActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_trip_history_pwdactivity);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBarPWDTripDetails);

        topAppBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SingleTripHistoryPWDActivity.this, PWDTripReportsActivity.class );
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }
}