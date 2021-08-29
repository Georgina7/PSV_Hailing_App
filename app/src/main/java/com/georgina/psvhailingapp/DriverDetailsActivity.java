package com.georgina.psvhailingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverDetailsActivity extends AppCompatActivity {
    private TextInputLayout mRoutes;
    private TextInputLayout mSeatsAvailable;
    private TextInputLayout mLicenceNo;
    private TextInputLayout mMatatuNoPlate;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);
        mRoutes = findViewById(R.id.routes);
        mSeatsAvailable = findViewById(R.id.number_seats_available);
        mMatatuNoPlate = findViewById(R.id.text_number_plate);
        mLicenceNo = findViewById(R.id.text_licence_no);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
    }
    @Override
    public void onStart(){
        super.onStart();
        if (mCurrentUser == null){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }
    private boolean validateSeatsAvailable(){
        int seatsAvailable = Integer.parseInt(mSeatsAvailable.getEditText().getText().toString().trim());
        if(seatsAvailable == 0){
            mSeatsAvailable.setError(getString(R.string.empty_field));
            return false;
        }
        else if(seatsAvailable < 0){
            mSeatsAvailable.setError("Seats cannot be less than 0");
            return false;
        }
        else {
            mSeatsAvailable.setError(null);
            return true;
        }
    }
    private boolean validateRoutes(){
        String routes = mRoutes.getEditText().getText().toString().trim();
        if(routes.isEmpty()){
            mRoutes.setError(getString(R.string.empty_field));
            return false;
        }
        else {
            mRoutes.setError(null);
            return true;
        }

    }
    private boolean validateNumberPlate(){
        String matatuNoPlate = mMatatuNoPlate.getEditText().getText().toString().trim();
        if(matatuNoPlate.isEmpty()){
            mMatatuNoPlate.setError(getString(R.string.empty_field));
            return false;
        }
        else if(matatuNoPlate.length() != 8){
            mMatatuNoPlate.setError(getString(R.string.invalid_field));
            return false;
        }
        else {
            mMatatuNoPlate.setError(null);
            return true;
        }
    }
    private boolean validateLicenceNumber(){
        String licenceNo = mLicenceNo.getEditText().getText().toString().trim();
        if(licenceNo.isEmpty()){
            mLicenceNo.setError(getString(R.string.empty_field));
            return false;
        }
        else if(licenceNo.length() != 10){
            mLicenceNo.setError(getString(R.string.invalid_field));
            return false;
        }
        else {
            mLicenceNo.setError(null);
            return true;
        }
    }

    public void Update(View view) {
        if (!validateLicenceNumber() | !validateNumberPlate() | !validateRoutes() | !validateSeatsAvailable()) {
            return;
        }


        String user_id = mCurrentUser.getUid();

        updateDriverDetails(user_id);
        sendToDriverMapActivity();


    }
    private void updateDriverDetails(String user_id){

        String availability = "active";
        String status ="enabled";
        String licence_number = mLicenceNo.getEditText().getText().toString();
        String matatu_plate = mMatatuNoPlate.getEditText().getText().toString();
        String routes = mRoutes.getEditText().getText().toString();
        Integer seats_available = Integer.parseInt(mSeatsAvailable.getEditText().getText().toString());

        firebaseDatabase = FirebaseDatabase.getInstance();
        DriverDetails driverDetails = new DriverDetails(licence_number,matatu_plate,routes,seats_available, availability,status);
        databaseReference = firebaseDatabase.getReference("Drivers").child(user_id);
        databaseReference.setValue(driverDetails);
    }
    private void sendToDriverMapActivity(){
        Intent intent = new Intent(DriverDetailsActivity.this,DriverMapActivity.class);
        startActivity(intent);
        finish();
    }
}