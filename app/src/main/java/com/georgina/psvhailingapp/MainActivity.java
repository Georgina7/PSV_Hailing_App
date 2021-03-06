package com.georgina.psvhailingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.textfield.TextInputLayout;
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

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout mFullName, mEmail, mPhoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private Button mCreateAccount;
//    private SignInButton btnSignIn;
//    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFullName = findViewById(R.id.full_name);
        mEmail = findViewById(R.id.email);
        mPhoneNumber = findViewById(R.id.contact);
        mCreateAccount = findViewById(R.id.create_account);

        //Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        //Initialize firebase user
        mCurrentUser = mAuth.getCurrentUser();
        mPhoneNumber.getEditText().setText(getIntent().getStringExtra("Contact"));

        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        //Assign Variable
        //btnSignIn = findViewById(R.id.sign_in_button);

//        //Initialize sign in options
//        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
//                GoogleSignInOptions.DEFAULT_SIGN_IN
//        ).requestIdToken("917593455964-1p2degt72t1ljnuadg1f6cakgie0pkp3.apps.googleusercontent.com")
//                .build();

        //Initialize sign in client
//        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, googleSignInOptions);
//        btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Initialize sign in intent
//                Intent intent = mGoogleSignInClient.getSignInIntent();
//                //Start Activity for result
//                startActivityForResult(intent,100);
//            }
//        });



        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        // Build a GoogleSignInClient with the options specified by gso.
//        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //findViewById(R.id.sign_in_button).setOnClickListener((View.OnClickListener) this);
    }

//    protected void onStart(){
//        super.onStart();
//        if(mCurrentUser != null){
//            checkIfUserIsDriver();
//        }
//
//        // Check for existing Google Sign In account, if the user is already signed in
//        // the GoogleSignInAccount will be non-null.
////        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        //updateUI(account);
//    }

    private boolean validateName(){
        String name = mFullName.getEditText().getText().toString().trim();
        if(name.isEmpty()){
            mFullName.setError("This field is empty");
            return false;
        }
        else {
            mFullName.setError(null);
            return true;
        }

    }

    private boolean validateEmail(){
        String email = mEmail.getEditText().getText().toString().trim();
        if(email.isEmpty()){
            mEmail.setError("This field is empty");
            return false;
        }
        else {
            mEmail.setError(null);
            return true;
        }

    }

    private boolean validateContact(){
        String contact = mPhoneNumber.getEditText().getText().toString().trim();
        if(contact.isEmpty()){
            mPhoneNumber.setError("This field is empty");
            return false;
        }
        else {
            mPhoneNumber.setError(null);
            return true;
        }

    }

    public void createAccount() {
        if (!validateName() | !validateEmail() | !validateContact()) {
            return;
        }
        String email = mEmail.getEditText().getText().toString();
        String fullName = mFullName.getEditText().getText().toString();
        String phoneNumber = mPhoneNumber.getEditText().getText().toString();
        String status = "enabled";
        String profileImgPath = "";
        firebaseDatabase = FirebaseDatabase.getInstance();
        User user = new User(fullName, email, phoneNumber, profileImgPath, status);
        databaseReference = firebaseDatabase.getReference("Users").child(mCurrentUser.getUid());
        databaseReference.setValue(user);
        Toast.makeText(getApplicationContext(),"Account Created Successfully", Toast.LENGTH_SHORT).show();
        sendUserToMain();
    }

    private void sendUserToMain(){
        Intent mainIntent = new Intent(MainActivity.this,PassengerMapActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

//    private void sendDriverToMain(){
//        Intent mainIntent = new Intent(MainActivity.this,DriverMapActivity.class);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(mainIntent);
//        finish();
//    }
//    private void checkIfUserIsDriver(){
//
//        String user_id = mCurrentUser.getUid();
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users");
//        DatabaseReference driver_idRef = database.child("Driver").child(user_id);
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()) {
//                    sendDriverToMain();
//                }
//                else{
//                    sendUserToMain();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        };
//        driver_idRef.addListenerForSingleValueEvent(eventListener);
//    }


//    public void verifyWithPhone(View view) {
//
//        String pNumber = mPhoneNumber.getEditText().getText().toString();
//        String email = mEmail.getEditText().getText().toString();
//        String fullName = mFullName.getEditText().getText().toString();
//        String ccp = "+254";
//        String PhoneNumber = ccp + pNumber;
//        if(pNumber.isEmpty() || email.isEmpty() || fullName.isEmpty() ){
//            Toast toast = Toast.makeText(getApplicationContext(), R.string.fill_all_blanks,Toast.LENGTH_SHORT);
//            toast.show();
//        }
//        else{
//
//            PhoneAuthOptions options =
//                    PhoneAuthOptions.newBuilder(mAuth)
//                            .setPhoneNumber(PhoneNumber)       // Phone number to verify
//                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                            .setActivity(MainActivity.this)                 // Activity (for callback binding)
//                            .setCallbacks(mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                                @Override
//                                public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {
//                                    VerifyCodeActivity verify = new VerifyCodeActivity();
//                                    verify.signInWithPhoneAuthCredential(phoneAuthCredential);
//
//                                }
//
//                                @Override
//                                public void onVerificationFailed(@NotNull FirebaseException e) {
//                                    Toast errorToast = Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG);
//                                    errorToast.show();
//                                }
//
//                                @Override
//                                public void onCodeSent(@NotNull String s, PhoneAuthProvider.@NotNull ForceResendingToken forceResendingToken) {
//                                    super.onCodeSent(s, forceResendingToken);
//                                    new android.os.Handler().postDelayed(
//                                            () -> {
//                                                Intent codeIntent = new Intent(getApplicationContext(), VerifyCodeActivity.class);
//                                                codeIntent.putExtra("AuthCredentials", s);
//                                                codeIntent.putExtra("phoneNumber",PhoneNumber);
//                                                codeIntent.putExtra("fullName", fullName);
//                                                codeIntent.putExtra("email", email);
//                                                startActivity(codeIntent);
//                                            },
//                                            10000
//                                    );
//                                }
//                            })// OnVerificationStateChangedCallbacks
//                            .build();
//            PhoneAuthProvider.verifyPhoneNumber(options);
//
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        //Check condition
//        if(requestCode == 100){
//            //Initialize task
//            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn
//                    .getSignedInAccountFromIntent(data);
//
//            //Check condition
//            if(signInAccountTask.isSuccessful()){
//                //When google sign in is successful
//                displayToast("Google sign in successful");
//                try{
//                    //Initialize sign in account
//                    GoogleSignInAccount googleSignInAccount = signInAccountTask
//                            .getResult(ApiException.class);
//                    //Check condition
//                    if(googleSignInAccount != null){
//                        //When sign in account is not equal to null
//                        //Initialize auth credential
//                        AuthCredential authCredential = GoogleAuthProvider
//                                .getCredential(googleSignInAccount.getIdToken()
//                                        , null);
//
//                        //Check credential
//                        mAuth.signInWithCredential(authCredential)
//                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
//                                        //Check condition
//                                        if(task.isSuccessful()){
//                                            //When task is successful
//                                            //Redirect to profile activity
//                                            startActivity(new Intent(MainActivity.this,
//                                                    PassengerMapActivity.class)
//                                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//
//                                            displayToast("Firebase authentication successful");
//                                        }else{
//                                            ///When task is unsuccessful
//                                            displayToast("Authentication failed : " + task.getException().getMessage());
//                                        }
//                                    }
//                                });
//                    }
//                }catch (ApiException e){
//                    e.getMessage();
//                }
//            }else{
//                displayToast("Didn't work" + signInAccountTask.getException());
//            }
//        }
//    }

//    private void displayToast(String message){
//        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//    }
//
//
//    public void sendUserToLogin(View view) {
//        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//
//    }


}