package com.georgina.psvhailingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PassengerTripMapsFragment extends Fragment {

    private BottomSheetBehavior mBottomSheetBehavior;
    private ConstraintLayout mTripBottomSheet;
    private RelativeLayout currentTripContainer;
    private TextView driverName, matatuPlate, callDriver, tripCancelledMsg;
    private CircleImageView driverProfile;
    private Button pay, cancel;
    public String driverID;

    FusedLocationProviderClient client;
    private FirebaseDatabase firebaseDatabase;
    SupportMapFragment mapFragment;
    private ArrayList<LatLng> locationArrayList;

    private String trip_key;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         *
         */

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_trip_maps, container, false);
        mTripBottomSheet = view.findViewById(R.id.current_trip_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mTripBottomSheet);
        currentTripContainer = view.findViewById(R.id.current_trip_container);
        tripCancelledMsg = view.findViewById(R.id.trip_cancelled_message);
        driverName = view.findViewById(R.id.match_driver_name);
        driverProfile = view.findViewById(R.id.match_driver_profile);
        callDriver = view.findViewById(R.id.call_driver);
        matatuPlate = view.findViewById(R.id.match_driver_matatu_plate);
        pay = view.findViewById(R.id.make_payment_button);
        cancel = view.findViewById(R.id.cancel_ride_button);

        trip_key = getActivity().getIntent().getStringExtra("TripKey");

        firebaseDatabase = FirebaseDatabase.getInstance();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext(), R.style.Theme_DialogBoxPWD);
                dialogBuilder.setTitle("Confirm");
                dialogBuilder.setMessage("Are you sure you want to cancel trip?");
                dialogBuilder.setBackground(getContext().getResources().getDrawable(R.drawable.alert_dialog_box));
                dialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference tripRef = firebaseDatabase.getReference("Trips").child(trip_key).child("status");
                        tripRef.setValue("cancelled - pwd");
                        Intent intent = new Intent(getContext(), PassengerMapActivity.class);
                        startActivity(intent);
                        Toast.makeText(getContext(), "Trip Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Just close dialog
                    }
                });
                dialogBuilder.show();

            }
        });

        DatabaseReference tripRef = firebaseDatabase.getReference("Trips").child(trip_key);
        tripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("status").getValue().equals("cancelled - driver")){
                    currentTripContainer.setVisibility(View.GONE);
                    tripCancelledMsg.setVisibility(View.VISIBLE);
                }else{
                    if(snapshot.child("status").getValue().equals("started")) {
                        cancel.setVisibility(View.GONE);
                    }
                    driverID = snapshot.child("driverID").getValue().toString();
                    DatabaseReference driverRef = firebaseDatabase.getReference("Drivers").child(driverID);
                    driverRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            matatuPlate.setText(snapshot.child("matatuPlate").getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //Add code to get distance and calculate distance
                    DatabaseReference userRef = firebaseDatabase.getReference("Users").child(driverID);
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            driverName.setText(snapshot.child("fullName").getValue().toString());
                            callDriver.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + snapshot.child("number").getValue().toString()));
                                    startActivity(intent);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        locationArrayList = new ArrayList<>();
        client = LocationServices.getFusedLocationProviderClient(getContext());
        Places.initialize(getContext(), getString(R.string.google_maps_key));
        markRoute();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.trip_google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        return view;
    }

    private void markRoute() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }else {
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                            LatLng pwdLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d("My Location", location.toString());
                            MarkerOptions markerOptions = new MarkerOptions().position(pwdLoc)
                                    .title("Your Location");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pwdLoc, 18));
                            googleMap.addMarker(markerOptions);
                            DatabaseReference locRef = firebaseDatabase.getReference("Locations").child(driverID);
                            locRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    LatLng driverLoc = new LatLng(snapshot.child("l").child("0").getValue(Double.class), snapshot.child("l").child("1").getValue(Double.class));
                                    Marker driverMarker =  googleMap.addMarker(new MarkerOptions().position(driverLoc).title("Driver Here"));
//                                    String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+
//                                            pwdLoc +"&destination="+
//                                            driverLoc +
//                                            "&key=AIzaSyDG4LfDl2SkDlvsZcVx3TEc5fhVBQqVUQw";
//                                    //new GetDirectionsTask().execute(url);
//                                    OkHttpClient okHttpClient = new OkHttpClient();
//                                    Request request = new Request.Builder().url(url).build();
//                                    okHttpClient.newCall(request).enqueue(new Callback() {
//                                        @Override
//                                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                        @Override
//                                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                                            if(response.isSuccessful()){
//                                                try{
//                                                    final String myResponse = response.body().toString();
//
//                                                    getActivity().runOnUiThread(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            Log.d("Response", myResponse);
//
//                                                        }
//                                                    });
//
//                                                }catch (Exception e){
//                                                   e.printStackTrace();
//                                                }finally {
//                                                    response.body().close();
//                                                }
//
//                                            }
//                                        }
//                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });

                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 44){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //getCurrentLocation();
            }
        }
    }

    public void makePayment(View view) {
    }
}