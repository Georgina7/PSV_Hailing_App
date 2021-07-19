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

    private SignInButton btnSignIn;
    GoogleSignInClient googleSignInClient;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Assign Variable
        btnSignIn = findViewById(R.id.sign_in_button);

        //Initialize sign in options
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("917593455964-1p2degt72t1ljnuadg1f6cakgie0pkp3.apps.googleusercontent.com")
                .build();

        //Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Initialize sign in intent
                Intent intent = googleSignInClient.getSignInIntent();
                //Start Activity for result
                startActivityForResult(intent,100);
            }
        });

        //Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        //Initialize firebase user
        mCurrentUser = mAuth.getCurrentUser();

        if(mCurrentUser != null){
            //When user already signed in redirect to profile activity
            startActivity(new Intent(LoginActivity.this,
                    ProfileActivity.class)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        mCountryCodePicker = findViewById(R.id.countryCodePicker);
        mPhoneNumber = findViewById(R.id.p_number);
        mLoginMessage = findViewById(R.id.messageText);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check condition
        if(requestCode == 100){
            //Initialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            //Check condition
            if(signInAccountTask.isSuccessful()){
                //When google sign in is successful
                displayToast("Google sign in successful");
                try{
                    //Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask
                            .getResult(ApiException.class);
                    //Check condition
                    if(googleSignInAccount != null){
                        //When sign in account is not equal to null
                        //Initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                , null);

                        //Check credential
                        mAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                       //Check condition
                                       if(task.isSuccessful()){
                                           //When task is successful
                                           //Redirect to profile activity
                                           startActivity(new Intent(LoginActivity.this,
                                                   ProfileActivity.class)
                                           .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                                           displayToast("Firebase authentication successful");
                                       }else{
                                           ///When task is unsuccessful
                                           displayToast("Authentication failed : " + task.getException().getMessage());
                                       }
                                    }
                                });
                    }
                }catch (ApiException e){
                    e.getMessage();
                }
            }else{
                displayToast("Didn't work" + signInAccountTask.getException());
            }
        }
    }

    private void displayToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
        if(mCurrentUser != null){
            sendUserToMain();
        }

    }

    private void sendUserToMain() {
        Intent mainIntent = new Intent(LoginActivity.this,PassengerMapActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}
