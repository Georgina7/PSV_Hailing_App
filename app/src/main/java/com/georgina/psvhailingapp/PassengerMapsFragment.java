package com.georgina.psvhailingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class PassengerMapsFragment extends Fragment {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    private FirebaseAuth mAuth;
    private String driver_id;
    private TextView mMatatuPlate;
    private TextView mDriverNumber;
    private TextView mStart;
    private TextView mDestination;
    private TextView mDriverName;
    private ImageView mHeaderArrow;
    private ConstraintLayout mRoutesBottomSheet;
    private LinearLayout mHeaderLayout;
    private BottomSheetBehavior mBottomSheetBehavior;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference driverDatabaseReference;
    private DatabaseReference user_driverDatabaseReference;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_maps, container, false);
        mAuth = FirebaseAuth.getInstance();
        mMatatuPlate = view.findViewById(R.id.no_plate);
        mDriverNumber = view.findViewById(R.id.driver_number);
        mStart = view.findViewById(R.id.Start);
        mDestination = view.findViewById(R.id.destination);
        mDriverName = view.findViewById(R.id.driver_name);
        mRoutesBottomSheet = view.findViewById(R.id.available_routes_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mRoutesBottomSheet);
        mBottomSheetBehavior.setHideable(false);
        mHeaderLayout = view.findViewById(R.id.header_layout);
        mHeaderArrow = view.findViewById(R.id.arrow);
        driver_id = "lWzaj102lsZEupT5WERAQS3GmUB2";
        firebaseDatabase = FirebaseDatabase.getInstance();
        driverDatabaseReference = firebaseDatabase.getReference("Users").child("Driver").child(driver_id);
        user_driverDatabaseReference = firebaseDatabase.getReference("Users").child(driver_id);
        getAvailableRoutes();
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        Toast toast = Toast.makeText(getContext(),driver_id,Toast.LENGTH_SHORT);
        toast.show();
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
            mBottomSheetBehavior.setHideable(false);

        }
        client = LocationServices.getFusedLocationProviderClient(getContext());
        getCurrentLocation();

        mHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {
                mHeaderArrow.setRotation(slideOffset * 180);
            }
        });
        return view;
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
        else {
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                            mBottomSheetBehavior.setHideable(false);
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                                    .title("Your Location");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                            googleMap.addMarker(markerOptions);
                        }
                    });

                }
            });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 44){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }
    private void getAvailableRoutes(){
        driverDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                DriverDetails driverDetails = snapshot.getValue(DriverDetails.class);
                mMatatuPlate.setText(driverDetails.getMatatuPlate());
                String Routes = driverDetails.getRoutes();
                String[] routes = Routes.split("-");
                mStart.setText(routes[0]);
                mDestination.setText(routes[1]);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
        user_driverDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                mDriverNumber.setText(user.getNumber());
                mDriverName.setText(user.getFullName());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}