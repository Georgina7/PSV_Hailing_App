package com.georgina.psvhailingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText mFullName, mEmail, mPhoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private Task<Void> databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFullName = findViewById(R.id.full_name);
        mEmail = findViewById(R.id.email);
        mPhoneNumber = findViewById(R.id.contact);

        //Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        //Initialize firebase user
        mCurrentUser = mAuth.getCurrentUser();
//        String user_id = mCurrentUser.getUid();
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference().child("Users").child("Passengers").child(user_id);



        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //findViewById(R.id.sign_in_button).setOnClickListener((View.OnClickListener) this);
    }

    protected void onStart(){
        super.onStart();
        if(mCurrentUser != null){
            sendUserToMain();
        }
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);
    }
    private void sendUserToMain(){
        Intent mainIntent = new Intent(MainActivity.this,PassengerMapActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    public void verifyWithPhone(View view) {

        String pNumber = mPhoneNumber.getText().toString();
        String email = mEmail.getText().toString();
        String fullName = mFullName.getText().toString();
        String ccp = "+254";
        String PhoneNumber = ccp + pNumber;
        if(pNumber.isEmpty() || email.isEmpty() || fullName.isEmpty() ){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.fill_all_blanks,Toast.LENGTH_SHORT);
            toast.show();
        }
        else{

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(PhoneNumber)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(MainActivity.this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {
                                    VerifyCodeActivity verify = new VerifyCodeActivity();
                                    verify.signInWithPhoneAuthCredential(phoneAuthCredential);

                                }

                                @Override
                                public void onVerificationFailed(@NotNull FirebaseException e) {
                                    Toast errorToast = Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG);
                                    errorToast.show();
                                }

                                @Override
                                public void onCodeSent(@NotNull String s, PhoneAuthProvider.@NotNull ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    new android.os.Handler().postDelayed(
                                            () -> {
                                                Intent codeIntent = new Intent(getApplicationContext(), VerifyCodeActivity.class);
                                                codeIntent.putExtra("AuthCredentials", s);
                                                codeIntent.putExtra("phoneNumber",PhoneNumber);
                                                codeIntent.putExtra("fullName", fullName);
                                                codeIntent.putExtra("email", email);
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

}