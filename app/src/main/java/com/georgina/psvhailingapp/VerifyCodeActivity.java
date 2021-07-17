package com.georgina.psvhailingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyCodeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
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
                            Toast toast = Toast.makeText(getApplicationContext(),"This has worked",Toast.LENGTH_SHORT);
                            toast.show();
                            sendUserToMain();

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
            sendUserToMain();
        }
    }

    private void sendUserToMain() {
        Intent mainIntent = new Intent(VerifyCodeActivity.this,PassengerMapActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }




}
