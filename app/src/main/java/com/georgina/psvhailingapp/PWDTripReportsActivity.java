package com.georgina.psvhailingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;

public class PWDTripReportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwdtrip_reports);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBarPWDTrips);

        topAppBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PWDTripReportsActivity.this, PassengerMapActivity.class );
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }
}