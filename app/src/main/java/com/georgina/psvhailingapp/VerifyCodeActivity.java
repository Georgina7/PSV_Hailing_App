package com.georgina.psvhailingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerifyCodeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String mAuthVerificationId;
    private TextView mMessageText;
    private EditText mCode;
    private String mPhoneNumber;
    private TextView mTextViewNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);
        mTextViewNo = findViewById(R.id.textView_number);
        mMessageText = findViewById(R.id.messageText);
        mCode = findViewById(R.id.v_code);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mAuthVerificationId = getIntent().getStringExtra("AuthCredentials");
        mPhoneNumber = getIntent().getStringExtra("phoneNumber");
        mTextViewNo.setText(mPhoneNumber);
    }

    public void Verify(View view) {
        String code = mCode.getText().toString();
        if(code.isEmpty()){
            mMessageText.setText(R.string.blank_number);
            mMessageText.setVisibility(View.VISIBLE);
        }
        else{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId,code);
            signInWithPhoneAuthCredential(credential);
        }
    }
    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyCodeActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mCurrentUser = task.getResult().getUser();
                            checkIfAccountIsCreated(mCurrentUser.getUid());

                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mMessageText.setVisibility(View.VISIBLE);
                                mMessageText.setText(R.string.error_verifying);
                            }
                        }
                    }
                });
    }
    protected void onStart(){
        super.onStart();
        if(mCurrentUser != null){
            checkIfAccountIsCreated(mCurrentUser.getUid());
        }
    }

    public void checkIfAccountIsCreated(String user_id){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference driver_idRef = database.child(user_id);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    checkIfUserIsDriver();
                }
                else{
                    sendUserToAccountCreation();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        driver_idRef.addListenerForSingleValueEvent(eventListener);
    }

    private void sendUserToAccountCreation(){
        Intent mainIntent = new Intent(VerifyCodeActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void sendUserToMain() {
        Intent mainIntent = new Intent(VerifyCodeActivity.this,PassengerMapActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void sendDriverToMain(){
        Intent mainIntent = new Intent(VerifyCodeActivity.this,DriverMapActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void checkIfUserIsDriver(){

        String user_id = mCurrentUser.getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference driver_idRef = database.child("Driver").child(user_id);
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

//    private void sendUserToMain() {
//        Intent mainIntent = new Intent(getApplicationContext(),PassengerMapActivity.class);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(mainIntent);
//        finish();
//    }

//    public void storeUserData(String user_id){
//        //Toast.makeText(getApplicationContext(), user_id, Toast.LENGTH_SHORT).show();
//        String email = getIntent().getStringExtra("email");
//        String fullName = getIntent().getStringExtra("fullName");
//        String phoneNumber = getIntent().getStringExtra("phoneNumber");
//        String profileImgPath = "";
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        User user = new User(fullName, email, phoneNumber, profileImgPath);
//        databaseReference = firebaseDatabase.getReference("Users").child(user_id);
//        databaseReference.setValue(user);
//    }




}
