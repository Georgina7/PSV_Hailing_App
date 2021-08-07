package com.georgina.psvhailingapp;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.TimeZone;

public class SelectTimeandDateActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private TextInputLayout mDate;
    private TextInputLayout mTime;
    private TextInputLayout mInfo;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_timeand_date);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDate = findViewById(R.id.date);
        mTime = findViewById(R.id.time);
        mInfo = findViewById(R.id.info);

        //Calendar for Date Picker
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(getString(R.string.utc)));
        calendar.clear();
        long today = MaterialDatePicker.todayInUtcMilliseconds();
        calendar.setTimeInMillis(today);
        //Constraints for calendar
        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
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
        final int[] hour = new int[1];
        int minute = 0;
        //Showing the Date Picker
        mDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText(R.string.select_date);
                builder.setSelection(today);
                builder.setCalendarConstraints(constraintBuilder.build());
                MaterialDatePicker materialDatePicker = builder.build();
                materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER");
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        mDate.getEditText().setText(materialDatePicker.getHeaderText());
                    }
                });
            }
        });

        //Showing Time Picker
        mTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(SelectTimeandDateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hour[0] = hourOfDay;
                        minute = minute;

                        Calendar calendar1 = Calendar.getInstance();
                        //setting hour and minute
                        calendar1.set(0,0,0, hour[0],minute);
                        mTime.getEditText().setText(DateFormat.format(getString(R.string.date_format),calendar1));
                    }
                },12,0,false);
                //Show the previous selected time
                timePickerDialog.updateTime(hour[0],minute);
                timePickerDialog.show();
            }
        });
    }
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
        if (!validateDate() | !validateTime() | !validateInfo()){
            return;
        }
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
    private boolean validateDate(){
        String date = mDate.getEditText().getText().toString().trim();
        if(date.isEmpty()){
            mDate.setError(getString(R.string.empty_field));
            return false;
        }
        else {
            mDate.setError(null);
            return true;
        }
    }
    private boolean validateTime(){
        String time = mTime.getEditText().getText().toString().trim();
        if(time.isEmpty()){
            mTime.setError(getString(R.string.empty_field));
            return false;
        }
        else {
            mTime.setError(null);
            return true;
        }
    }

}