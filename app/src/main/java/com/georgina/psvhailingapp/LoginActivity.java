package com.georgina.psvhailingapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private String loggedInUserId;

//    private SignInButton btnSignIn;
//    GoogleSignInClient googleSignInClient;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        //Initialize firebase user
        mCurrentUser = mAuth.getCurrentUser();

        mCountryCodePicker = findViewById(R.id.countryCodePicker);
        mPhoneNumber = findViewById(R.id.p_number);
        mLoginMessage = findViewById(R.id.messageText);

        if(getIntent().getExtras() != null){
            loggedInUserId = getIntent().getStringExtra("UserId");
        }else{
            loggedInUserId = "";
        }

    }

    public void Next(View view) {
        String pNumber = mPhoneNumber.getText().toString();
        String ccp = "+254";
        PhoneNumber = ccp + pNumber;
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
                            .setCallbacks(mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
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
                                                codeIntent.putExtra("UserId", loggedInUserId);
                                                startActivity(codeIntent);
                                            },
                                            10000
                                    );
                                }
                            })// OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);

        }

    }

    protected void onStart(){
        super.onStart();
        if(mCurrentUser != null && loggedInUserId.isEmpty()){
            checkIfUserIsDriver();
        }else{
            //Toast.makeText(getApplicationContext(), loggedInUserId, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendUserToMain() {
        Intent mainIntent = new Intent(LoginActivity.this,PassengerMapActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void sendDriverToMain(){
        Intent mainIntent = new Intent(LoginActivity.this,DriverMapActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void checkIfUserIsDriver(){

        String user_id = mCurrentUser.getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Drivers");
        DatabaseReference driver_idRef = database.child(user_id);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    sendDriverToMain();
                }
                else{
                    sendUserToMain();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        driver_idRef.addListenerForSingleValueEvent(eventListener);
    }

}
