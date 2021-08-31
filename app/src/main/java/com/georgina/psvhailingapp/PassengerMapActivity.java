package com.georgina.psvhailingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class PassengerMapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawer;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    DatabaseReference databaseReference;
    DatabaseReference databaseDriverReference;
    FirebaseDatabase firebaseDatabase;
    User user;
    DriverDetails driverDetails;
    private NavigationView navigationView;
    private TextView profileFullName;
    private Button viewProfileBtn;
    private CircleImageView circleImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_map);
        //Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        //Initialize firebase user
        mCurrentUser = mAuth.getCurrentUser();
        mDrawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        firebaseDatabase = FirebaseDatabase.getInstance();
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        circleImageView = header.findViewById(R.id.profile_image);
        profileFullName = header.findViewById(R.id.profile_fullname);
        viewProfileBtn = header.findViewById(R.id.profile_btn);

        viewProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PassengerMapActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        if(mCurrentUser != null){
            //Toast.makeText(getApplicationContext(), mCurrentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
//            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Users").child(mCurrentUser.getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    //Toast.makeText(getApplicationContext(), user.getFullName(), Toast.LENGTH_LONG).show();
                    profileFullName.setText(user.getFullName());
                    Toast.makeText(PassengerMapActivity.this,user.getProfileImagePath(),Toast.LENGTH_SHORT).show();
                    Glide.with(PassengerMapActivity.this).load(user.getProfileImagePath()).
                            apply(new RequestOptions().override(600, 200)).into(circleImageView);
//                    File imgFile = new  File("/storage/emulated/0/bluetooth/OtherIMG_2414.jpg");
//
//                    if(imgFile.exists()){
//
//                       // Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//
////                        circleImageView.setImageBitmap(myBitmap);
//                        Bitmap b = BitmapFactory.decodeByteArray(imgFile.getAbsolutePath() , 0, imgFile.getAbsolutePath().length());
//                        circleImageView.setImageBitmap(Bitmap.createScaledBitmap(b, 120, 120, false));
//
//                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
                }
            });
           // profileFullName.setText(user.getFullName());
        }

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PassengerMapsFragment()).commit();
            navigationView.setCheckedItem(R.id.map);
        }
    }
    public void openDrawer(View view) {
        mDrawer.openDrawer(GravityCompat.START);
    }
    public void onBackPressed(){
        if(mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
        }
    }


    @Override
    public void onStart(){
        super.onStart();
        if (mCurrentUser == null){
           sendUserToLogin();
        }
        else {
            checkIfUserIsDisabled();
        }
    }

    private void checkIfUserIsDisabled() {
        databaseReference = firebaseDatabase.getReference("Users").child(mCurrentUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot1) {
                user = snapshot1.getValue(User.class);
                String status = user.getStatus();
                String disabled = "disabled";
                if(status.matches(disabled)) {
                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(PassengerMapActivity.this, R.style.Theme_DialogBoxPWD);
                    dialogBuilder.setTitle("Alert!");
                    dialogBuilder.setMessage("Your account has been disabled");
                    dialogBuilder.setBackground(getResources().getDrawable(R.drawable.alert_dialog_box));
                    dialogBuilder.setNeutralButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseAuth.getInstance().signOut();
                            sendUserToLogin();
                        }
                    });
                    dialogBuilder.show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(), "Logout Successful", Toast.LENGTH_SHORT).show();
        finish();
        sendUserToLogin();
    }

    private void sendUserToLogin() {
        Intent mainIntent = new Intent(PassengerMapActivity.this,LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    public void sendToDriverDetails(View view) {
        checkIfUserIsDriver();
    }
    private void checkIfUserIsDriver(){

        String user_id = mCurrentUser.getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Drivers");
        DatabaseReference driver_idRef = database.child(user_id);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    checkIfDriverIsDisabled();
                }
                else{
                    Intent driverIntent = new Intent(PassengerMapActivity.this,DriverDetailsActivity.class );
                    startActivity(driverIntent);
//                  finish();
                }
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {}
        };
        driver_idRef.addListenerForSingleValueEvent(eventListener);
    }
    private void checkIfDriverIsDisabled() {
        databaseDriverReference = firebaseDatabase.getReference("Drivers").child(mCurrentUser.getUid());
        databaseDriverReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot1) {
                driverDetails = snapshot1.getValue(DriverDetails.class);
                String status = driverDetails.getStatus();
                String disabled = "disabled";
                if(status.matches(disabled)){
                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(PassengerMapActivity.this,R.style.Theme_DialogBoxPWD);
                    dialogBuilder.setTitle("Alert!");
                    dialogBuilder.setMessage("Your account has been disabled as a driver!!");
                    dialogBuilder.setBackground(getResources().getDrawable(R.drawable.alert_dialog_box));
                    dialogBuilder.setNeutralButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialogBuilder.show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PassengerMapsFragment()).commit();
                break;
            case R.id.trip_reports:
                Intent intent = new Intent(PassengerMapActivity.this, PWDTripReportsActivity.class);
                startActivity(intent);
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}