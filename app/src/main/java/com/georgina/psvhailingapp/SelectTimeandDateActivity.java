package com.georgina.psvhailingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class SelectTimeandDateActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private TextInputLayout mInfo;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private NumberPicker mSeats;
    private long today;
    private CalendarConstraints.Builder constraintBuilder;
    private String source, dest, activity;
    private ArrayList<String> driversList;
    public int seats_available;
    public String closestDriver;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_timeand_date);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        //mDate = findViewById(R.id.date);
        //mTime = findViewById(R.id.time);
        mInfo = findViewById(R.id.info);
        mSeats = findViewById(R.id.no_seats);
        mSeats.setMaxValue(50);
        mSeats.setMinValue(0);

        driversList = new ArrayList<>();

        //Calendar for Date Picker
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(getString(R.string.utc)));
        calendar.clear();
        today = MaterialDatePicker.todayInUtcMilliseconds();
        calendar.setTimeInMillis(today);
        //Constraints for calendar
        constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointForward.now());
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SelectTimeandDateActivity.this, SelectDropoffActivity.class );
                        startActivity(intent);
                        finish();                    }
                }
        );

        //Date Picker
//        onClickDate();
//        final int[] hour = new int[1];
//        int minute = 0;
        //Showing the Date Picker
//        mDate.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if(view.isFocused()){
//                    MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
//                    builder.setTitleText(R.string.select_date);
//                    builder.setSelection(today);
//                    builder.setCalendarConstraints(constraintBuilder.build());
//                    MaterialDatePicker materialDatePicker = builder.build();
//                    materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER");
//                    materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
//                        @Override
//                        public void onPositiveButtonClick(Object selection) {
//                            mDate.getEditText().setText(materialDatePicker.getHeaderText());
//                        }
//                    });
//                }
//            }
//        });

        //Showing Time Picker
//        onClickTime();
//        mTime.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View timeView, boolean b) {
//                if(timeView.isFocused()){
//                    TimePickerDialog timePickerDialog = new TimePickerDialog(SelectTimeandDateActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        hour[0] = hourOfDay;
//                        minute = minute;
//
//                        Calendar calendar1 = Calendar.getInstance();
//                        //setting hour and minute
//                        calendar1.set(0,0,0, hour[0],minute);
//                        mTime.getEditText().setText(DateFormat.format(getString(R.string.date_format),calendar1));
//                    }
//                },12,0,false);
//                //Show the previous selected time
//                timePickerDialog.updateTime(hour[0],minute);
//                timePickerDialog.show();
//                }
//            }
//        });
    }

//    private void onClickDate() {
//        mDate.getEditText().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
//                builder.setTitleText(R.string.select_date);
//                builder.setSelection(today);
//                builder.setCalendarConstraints(constraintBuilder.build());
//                MaterialDatePicker materialDatePicker = builder.build();
//                materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER");
//                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
//                    @Override
//                    public void onPositiveButtonClick(Object selection) {
//                        mDate.getEditText().setText(materialDatePicker.getHeaderText());
//                    }
//                });
//            }
//        });
//    }

//    private void onClickTime() {
//        mTime.getEditText().setOnClickListener(new View.OnClickListener() {
//            final int[] hour = new int[1];
//            int minute = 0;
//            @Override
//            public void onClick(View v) {
//                TimePickerDialog timePickerDialog = new TimePickerDialog(SelectTimeandDateActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        hour[0] = hourOfDay;
//                        minute = minute;
//
//                        Calendar calendar1 = Calendar.getInstance();
//                        //setting hour and minute
//                        calendar1.set(0,0,0, hour[0],minute);
//                        mTime.getEditText().setText(DateFormat.format(getString(R.string.date_format),calendar1));
//                    }
//                },12,0,false);
//                //Show the previous selected time
//                timePickerDialog.updateTime(hour[0],minute);
//                timePickerDialog.show();
//            }
//        });
//    }

    @Override
    public void onStart(){
        super.onStart();
        if (mCurrentUser == null){
            Intent intent = new Intent(SelectTimeandDateActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }
    //Onclick button to proceed
    public void proceed(View view) {
        if (!validateInfo()){
            return;
        }
        else{
            findDriver();
            //Toast.makeText(SelectTimeandDateActivity.this,"Data has been added",Toast.LENGTH_SHORT).show();
        }
    }

    private void findDriver() {
        Intent i = getIntent();
        activity = i.getStringExtra("Activity");
        if(activity.equals("SelectDropOff")){
            source = i.getStringExtra("Source");
            dest = i.getStringExtra("Destination");
        }else{
            source = i.getStringExtra(PassengerMapsFragment.EXTRA_SOURCE);
            dest = i.getStringExtra(PassengerMapsFragment.EXTRA_DEST);
        }

        checkForRoute(source, dest);

    }

    private void checkForRoute(String source, String dest) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Routes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Iterator<DataSnapshot> routes = snapshot.getChildren().iterator();
                //Toast.makeText(getApplicationContext(), "Total Routes: " + snapshot.getChildrenCount(),Toast.LENGTH_SHORT).show();
                String route_key = "";
                while (routes.hasNext()){
                    DataSnapshot route = routes.next();
                    if(route.hasChild(source) && route.hasChild(dest)){
                        route_key = route.getKey();
                    }
                }
                if(!route_key.isEmpty()){
                    checkForDriver(route_key);
                }else{
                    Toast.makeText(getApplicationContext(), "No driver registered on this route", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void checkForDriver(String route_key) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Drivers");
        String finalRoute_key = route_key;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Iterator<DataSnapshot> drivers = snapshot.getChildren().iterator();
                driversList.clear();
                Boolean seatsAvailable = true;
                while (drivers.hasNext()){
                    DataSnapshot driver = drivers.next();
                    if(driver.child("routes").getValue().equals(finalRoute_key)
                        && driver.child("status").getValue().equals("enabled")
                        && driver.child("availability").getValue().equals("active")){
                        seats_available = Integer.valueOf(String.valueOf(driver.child("seats").getValue()));
                        if(seats_available >= mSeats.getValue()){
                            driversList.add(driver.getKey());
                        }
                        seatsAvailable = false;
                    }else{
                        Log.d("Other", finalRoute_key);
                    }
                }

                if(driversList.size() == 0){
                    //Toast.makeText(getApplicationContext(), "No driver registered on this route", Toast.LENGTH_LONG).show();
                    if(!seatsAvailable){
                        Toast.makeText(getApplicationContext(), "Seats requested are unavailable", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "No driver registered on this route", Toast.LENGTH_LONG).show();
                    }
                }else{
                    findClosestDriver(driversList);
                }

            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private void findClosestDriver(ArrayList<String> driversList) {
        DatabaseReference driverRef = firebaseDatabase.getReference("Drivers");
        driverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterator<DataSnapshot> drivers = snapshot.getChildren().iterator();
                int no_of_trips = 0;
                while (drivers.hasNext()){
                    DataSnapshot driver = drivers.next();
                    for(int counter = 0; counter < driversList.size(); counter++){
                        if(driversList.get(counter).equals(driver.getKey())){
                            no_of_trips = no_of_trips + 1;
                        }
                    }
                }

                Log.d("Number of Trips", "Ni: " + no_of_trips);

                if(seats_available > no_of_trips){
                    databaseReference = firebaseDatabase.getReference("Locations");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            LatLng userLoc = new LatLng(snapshot.child(mCurrentUser.getUid()).child("latitude").getValue(Double.class),
                                    snapshot.child(mCurrentUser.getUid()).child("longitude").getValue(Double.class));
                            Log.d("UserLoc", userLoc.toString());
                            float smallestDist = -1;
                            for(int counter = 0; counter < driversList.size(); counter++){
                                snapshot.child(driversList.get(counter)).child("l");
                                LatLng driverLoc = new LatLng(snapshot.child(driversList.get(counter)).child("l").child("0").getValue(Long.class),
                                        snapshot.child(driversList.get(counter)).child("l").child("1").getValue(Long.class));
                                Log.d("DriverLoc", driverLoc.toString());
                                float[] distance = new float[1];
                                Location.distanceBetween(userLoc.latitude, userLoc.longitude,
                                        driverLoc.latitude,
                                        driverLoc.longitude, distance);
                                Log.d("Distance", Float.toString(distance[0]));
                                if(smallestDist == -1 || distance[0] < smallestDist){
                                    closestDriver = driversList.get(counter);
                                    smallestDist = distance[0];
                                }
                            }

                            addBookingDetails(closestDriver);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Seats requested are unavailable", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //addBookingDetails();
        //Log.d("Closest Driver", closestDriver.get(0));
    }

    private void addBookingDetails(String closestDriver) {
        //Log.d("Closest Driver", closestDriver);
        String driver_id = closestDriver;
        String info = mInfo.getEditText().getText().toString();
        String status = "pending";
        int seat = mSeats.getValue();
        String date = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date());
        Trip trip = new Trip(mCurrentUser.getUid(),driver_id, source, dest, date, info, status, seat);
        databaseReference = firebaseDatabase.getReference("Trips");
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(trip);

        Log.d("Seats Ziko", "Ni " + seats_available);

//        DatabaseReference seatsAllocated = firebaseDatabase.getReference("Drivers").child(driver_id);
//        seats_available = seats_available - seat;
//        seatsAllocated.child("seats").setValue(seats_available);

        Intent intent = new Intent(SelectTimeandDateActivity.this, PassengerTripActivity.class);
        intent.putExtra("TripKey", key);
        intent.putExtra("DriverID", driver_id);
        startActivity(intent);
        //Toast.makeText(getApplicationContext(), source + " - " + dest,Toast.LENGTH_SHORT).show();
    }

//    private void getSeatsAvailable(String driver_id) {
//        DatabaseReference seatsAllocated = firebaseDatabase.getReference("Drivers").child(driver_id);
//        seatsAllocated.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                }
//                else {
//                    available = Integer.valueOf(String.valueOf(task.getResult().child("seats").getValue()));
//                    setSeats_available();
//
//                }
//            }
//
//          });
//
//        updateSeatsAvailable(driver_id);
//
//    }
//
//    private void updateSeatsAvailable(String driver_id) {
//        int available_seats = getSeats_available() - mSeats.getValue();
//        DatabaseReference seatsAllocated = firebaseDatabase.getReference("Drivers").child(driver_id);
//        seatsAllocated.child("seats").setValue(available_seats);
//    }

    private void sendUserToMain(){
        Intent intent = new Intent(SelectTimeandDateActivity.this, PassengerMapActivity.class);

    }

    //Validation of Input Fields
    private boolean validateInfo(){
        String info = mInfo.getEditText().getText().toString().trim();
        if(info.isEmpty()){
            mInfo.setError(getString(R.string.empty_field));
            return false;
        }
        else {
            mInfo.setError(null);
            return true;
        }
    }
//    private boolean validateDate(){
//        String date = mDate.getEditText().getText().toString().trim();
//        if(date.isEmpty()){
//            mDate.setError(getString(R.string.empty_field));
//            return false;
//        }
//        else {
//            mDate.setError(null);
//            return true;
//        }
//    }
//    private boolean validateTime(){
//        String time = mTime.getEditText().getText().toString().trim();
//        if(time.isEmpty()){
//            mTime.setError(getString(R.string.empty_field));
//            return false;
//        }
//        else {
//            mTime.setError(null);
//            return true;
//        }
//    }

}