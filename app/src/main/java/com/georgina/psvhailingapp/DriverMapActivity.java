package com.georgina.psvhailingapp;

import android.content.DialogInterface;
import android.content.Intent;
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

import static android.widget.Toast.LENGTH_SHORT;

public class DriverMapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private NavigationView navigationView;
    User user;
    DriverDetails driverDetails;
    private TextView profileFullName;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseDriverReference;
    private Button viewProfileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        //Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        //Initialize firebase user
        mCurrentUser = mAuth.getCurrentUser();
        mDrawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        profileFullName = header.findViewById(R.id.profile_fullname);
        viewProfileBtn = header.findViewById(R.id.profile_btn);

        viewProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverMapActivity.this, DriverProfileActivity.class);
                startActivity(intent);
            }
        });

        if(mCurrentUser != null){
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Users").child(mCurrentUser.getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    //Toast.makeText(getApplicationContext(), user.getFullName(), Toast.LENGTH_LONG).show();
                    profileFullName.setText(user.getFullName());
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Fail to get data.", LENGTH_SHORT).show();
                }
            });
        }

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new DriverMapsFragment()).commit();
            navigationView.setCheckedItem(R.id.map);
        }
    }
    @Override
    public void onStart(){
        super.onStart();
        if (mCurrentUser == null){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
        else{
            checkIfDriverIsDisabled();
        }
    }

    private void checkIfDriverIsDisabled() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseDriverReference = firebaseDatabase.getReference("Drivers").child(mCurrentUser.getUid());
        databaseDriverReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot1) {
                driverDetails = snapshot1.getValue(DriverDetails.class);
                String status = driverDetails.getStatus().toString();
//                Toast toast = Toast.makeText(DriverMapActivity.this,status, LENGTH_SHORT);
//                toast.show();
                String disabled = "disabled";
                if(status.matches(disabled)){
                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(DriverMapActivity.this,R.style.Theme_DialogBox);
                    dialogBuilder.setTitle("Alert!");
                    dialogBuilder.setMessage("Your account has been disabled as a driver!!");
                    dialogBuilder.setBackground(getResources().getDrawable(R.drawable.alert_dialog_box));
                    dialogBuilder.setNeutralButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sendDriverToPassengerSide();
                        }
                    });
                    dialogBuilder.show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Fail to get data.", LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new DriverMapsFragment()).commit();
                break;
            case R.id.driver_details:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new DriverDetailsFragment()).commit();
                break;
            case R.id.driver_trips:
                Intent intent = new Intent(this,SeatBookingsActivity.class);
                startActivity(intent);
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openDrawer(View view) {
        mDrawer.openDrawer(GravityCompat.START);
    }
    public void onBackPressed(){
        if(mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
        }
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(), "Logout Successful", LENGTH_SHORT).show();
        finish();
        sendUserToLogin();
    }

    private void sendDriverToPassengerSide(){
        Intent intent = new Intent(DriverMapActivity.this,PassengerMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToLogin() {
        Intent mainIntent = new Intent(DriverMapActivity.this,LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}