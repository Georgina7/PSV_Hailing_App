package com.georgina.psvhailingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;

public class DriverProfileActivity extends AppCompatActivity {

    //Initialize variables
    private TextInputLayout mFullName;
    private TextInputLayout mEmail;
    private TextInputLayout mContact;
    private ImageView mProfilePicture;
    private Button mUpdateProfileBtn;
    private User user;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private String selectedImagePath;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        selectedImagePath = "";

        //Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        //Initialize firebase user
        firebaseUser = firebaseAuth.getCurrentUser();

        user = new User();

        mFullName = findViewById(R.id.edit_profile_fullname);
        mEmail = findViewById(R.id.edit_profile_email);
        mContact = findViewById(R.id.edit_profile_contact);
        mProfilePicture = findViewById(R.id.edit_profile_image);
        mUpdateProfileBtn = findViewById(R.id.btn_update_profile);

        mUpdateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getEditText().getText().toString();
                String fullName = mFullName.getEditText().getText().toString();
                String phoneNumber = mContact.getEditText().getText().toString();
                String status = "enabled";
                String profileImgPath = selectedImagePath;
                firebaseDatabase = FirebaseDatabase.getInstance();
                User user = new User(fullName, email, phoneNumber, profileImgPath, status);
                databaseReference = firebaseDatabase.getReference("Users").child(firebaseUser.getUid());
                databaseReference.setValue(user);
                Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        //Check condition
        if (firebaseUser != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(firebaseUser.getUid());
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        DataSnapshot dataSnapshot = task.getResult();
                        user.setEmail(String.valueOf(dataSnapshot.child("email").getValue()));
                        user.setFullName(String.valueOf(dataSnapshot.child("fullName").getValue()));
                        user.setNumber(String.valueOf(dataSnapshot.child("number").getValue()));
                        user.setProfileImagePath(String.valueOf(dataSnapshot.child("profileImagePath").getValue()));
                        if(firebaseUser.getDisplayName() != null && user.getFullName() == null){
                            mFullName.getEditText().setText(firebaseUser.getDisplayName());
                        }else{
                            mFullName.getEditText().setText(user.getFullName());
                            mEmail.getEditText().setText(user.getEmail());
                            mContact.getEditText().setText(user.getNumber());
                            if(!user.getProfileImagePath().isEmpty()){
                                mProfilePicture.setImageBitmap(BitmapFactory.decodeFile(user.getProfileImagePath()));
                                mProfilePicture.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }
            });

        }
    }

    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data != null){
                Uri selectedImageUri = data.getData();
                if(selectedImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        mProfilePicture.setImageBitmap(bitmap);
                        mProfilePicture.setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selectedImageUri);
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }

        return filePath;
    }
}