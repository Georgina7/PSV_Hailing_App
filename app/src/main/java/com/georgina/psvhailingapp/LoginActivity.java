package com.georgina.psvhailingapp;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText mPhoneNumber;
    private CountryCodePicker mCountryCodePicker;
    private String PhoneNumber;
    private TextView mLoginMessage;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mCountryCodePicker = findViewById(R.id.countryCodePicker);
        mPhoneNumber = findViewById(R.id.p_number);
        mLoginMessage = findViewById(R.id.messageText);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
    }


    public void Next(View view) {
        String pNumber = mPhoneNumber.getText().toString();
        String ccp = "+254";
        PhoneNumber = ccp + mPhoneNumber.getText().toString();
        if(pNumber.isEmpty()){
            mLoginMessage.setText(R.string.blank_number);
            mLoginMessage.setVisibility(View.VISIBLE);
        }
        else{


            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(PhoneNumber)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)// OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);

        }

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {
                VerifyCodeActivity verify = new VerifyCodeActivity();
                verify.signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NotNull FirebaseException e) {
                mLoginMessage.setText(R.string.verification_failed);
                mLoginMessage.setVisibility(View.VISIBLE);
                mLoginMessage.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NotNull String s, PhoneAuthProvider.@NotNull ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                new android.os.Handler().postDelayed(
                        () -> {
                            Intent codeIntent = new Intent(LoginActivity.this, VerifyCodeActivity.class);
                            codeIntent.putExtra("AuthCredentials", s);
                            codeIntent.putExtra("phoneNumber",PhoneNumber);
                            startActivity(codeIntent);
                        },
                        10000
                );
            }
        };
    }
//
//    protected void onStart(){
//        super.onStart();
//        if(mCurrentUser != null){
//            sendUserToMain();
//        }
//    }
//
//    private void sendUserToMain() {
//        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(mainIntent);
//        finish();
//        mAuth.signOut();
//    }


}
