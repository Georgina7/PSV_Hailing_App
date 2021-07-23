package com.georgina.psvhailingapp;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverDetailsFragment extends Fragment {
    private TextInputLayout mRoutes;
    private TextInputLayout mSeatsAvailable;
    private TextInputLayout mLicenceNo;
    private TextInputLayout mMatatuNoPlate;
    private Button mUpdate;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_details, container, false);
        mRoutes =view.findViewById(R.id.routes);
        mSeatsAvailable =view.findViewById(R.id.number_seats_available);
        mMatatuNoPlate =view.findViewById(R.id.text_number_plate);
        mLicenceNo =view.findViewById(R.id.text_licence_no);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mUpdate = (Button)view.findViewById(R.id.btn_update);

        String user_id = mCurrentUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child("Driver").child(user_id);

        getDriverData();

        mUpdate.setOnClickListener(v -> {
            if (!validateLicenceNumber() | !validateNumberPlate() | !validateRoutes() | !validateSeatsAvailable()) {
                return;
            }
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getContext(),R.style.Theme_DialogBox);
            dialog.setTitle("Update Details?");
            dialog.setMessage("This action will update your details");
            dialog.setBackground(getResources().getDrawable(R.drawable.alert_dialog_box, getActivity().getTheme()));
            dialog.setPositiveButton(R.string.confirm, (dialog1, which) -> {
                updateDriverDetails(user_id);
                MaterialAlertDialogBuilder confirmation = new MaterialAlertDialogBuilder(getContext(),R.style.Theme_DialogBox);
                confirmation.setMessage("Details Successfully Updated!");
                confirmation.setBackground(getResources().getDrawable(R.drawable.alert_dialog_box, getActivity().getTheme()));
                confirmation.setPositiveButton("OK", (dialogInterface, which1) -> dialogInterface.dismiss());
                confirmation.show();
            });
            dialog.setNegativeButton("Dismiss", (dialog12, which) -> dialog12.dismiss());
            dialog.show();
        });
        return view;
    }
    private boolean validateSeatsAvailable(){
        int seatsAvailable = Integer.parseInt(mSeatsAvailable.getEditText().getText().toString());
        if(seatsAvailable == 0){
            mSeatsAvailable.setError(getString(R.string.invalid_field));
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
        else if(licenceNo.length() > 10){
            mLicenceNo.setError(getString(R.string.invalid_field));
            return false;
        }
        else if(licenceNo.length() < 10){
            mLicenceNo.setError(getString(R.string.invalid_field));
            return false;
        }
        else {
            mLicenceNo.setError(null);
            return true;
        }
    }
    private void updateDriverDetails(String user_id){

        String licence_number = mLicenceNo.getEditText().getText().toString();
        String matatu_plate = mMatatuNoPlate.getEditText().getText().toString();
        String routes = mRoutes.getEditText().getText().toString();
        int seats_available = Integer.parseInt(mSeatsAvailable.getEditText().getText().toString());

        firebaseDatabase = FirebaseDatabase.getInstance();
        DriverDetails driverDetails = new DriverDetails(licence_number,matatu_plate,routes,seats_available);
        databaseReference = firebaseDatabase.getReference("Users").child("Driver").child(user_id);
        databaseReference.setValue(driverDetails);
    }
    private void getDriverData() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DriverDetails driverDetails = snapshot.getValue(DriverDetails.class);
                mLicenceNo.getEditText().setText(driverDetails.getLicenceNo());
                mMatatuNoPlate.getEditText().setText(driverDetails.getMatatuPlate());
                mRoutes.getEditText().setText(driverDetails.getRoutes());
                mSeatsAvailable.getEditText().setText(String.valueOf(driverDetails.getSeats()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}