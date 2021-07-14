package com.georgina.psvhailingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    private EditText mPhoneNumber;
    private CountryCodePicker mCountryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mCountryCodePicker = findViewById(R.id.countryCodePicker);
        mPhoneNumber = findViewById(R.id.p_number);

    }

    public void getPhoneNumber(){
        String PhoneNumber = mCountryCodePicker.getFullNumber() + mPhoneNumber.getText().toString();
    }
}
