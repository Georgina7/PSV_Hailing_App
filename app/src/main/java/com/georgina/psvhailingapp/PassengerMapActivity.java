package com.georgina.psvhailingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class PassengerMapActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    User user;
    GoogleSignInClient googleSignInClient;
    private NavigationView navigationView;
    private TextView profileFullName;
    private Button viewProfileBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_map);
        //Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        //Initialize firebase user
        mCurrentUser = mAuth.getCurrentUser();

        user = new User();

        mDrawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
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
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Users").child(mCurrentUser.getUid());
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        DataSnapshot dataSnapshot = task.getResult();
                        user.setFullName(String.valueOf(dataSnapshot.child("fullName").getValue()));
                        Toast.makeText(getApplicationContext(), user.getFullName(), Toast.LENGTH_LONG).show();
                        profileFullName.setText(user.getFullName());
                        //Log.d("firebase", user.getNumber());
                    }
                }
            });

            profileFullName.setText(mCurrentUser.getDisplayName());
        }

        //Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(PassengerMapActivity.this,
                GoogleSignInOptions.DEFAULT_SIGN_IN);

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
        Intent intent = new Intent(getApplicationContext(),DriverDetailsActivity.class );
        startActivity(intent);
        //finish();
    }
}