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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PassengerTripMapsFragment extends Fragment implements RoutingListener{

    private BottomSheetBehavior mBottomSheetBehavior;
    private ConstraintLayout mTripBottomSheet;
    private RelativeLayout currentTripContainer;
    private TextView driverName, matatuPlate, callDriver, tripCancelledMsg;
    private CircleImageView driverProfile;
    private Button pay, cancel;
    public String driverID, source, destination, driver_name;

    FusedLocationProviderClient client;
    private FirebaseDatabase firebaseDatabase;
    SupportMapFragment mapFragment;
    private ArrayList<LatLng> locationArrayList;

    private GoogleMap mMap;
    private String trip_key;
    protected LatLng trip_src, trip_des;
    private List<Polyline> polylines=null;

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
                        //source will be driver current loc once trip starts
                        //remove marker for pwd
                    }
                    source = snapshot.child("source").getValue().toString();
                    destination = snapshot.child("destination").getValue().toString();
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
                            driver_name = snapshot.child("fullName").getValue().toString();
                            //driverName.setText(snapshot.child("fullName").getValue().toString());
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
//                            LatLng pwdLoc = new LatLng(location.getLatitude(), location.getLongitude());
//                            Log.d("My Location", location.toString());
//                            MarkerOptions markerOptions = new MarkerOptions().position(pwdLoc)
//                                    .title("Your Location");
//                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pwdLoc, 18));
//                            googleMap.addMarker(markerOptions);

                            Geocoder geocoder = new Geocoder(getContext());
                            List<Address> addressList = null;
                            try {
                                addressList = geocoder.getFromLocationName(source, 1);
                                assert addressList != null;
                                Address sourceAddress = addressList.get(0);
                                trip_src = new LatLng(sourceAddress.getLatitude(), sourceAddress.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions().position(trip_src)
                                        .title("PickUp Location");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(trip_src, 18));
                                googleMap.addMarker(markerOptions);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            List<Address> addressList1 = null;
                            try {
                                addressList1 = geocoder.getFromLocationName(destination, 1);
                                assert addressList1 != null;
                                Address destAddress = addressList1.get(0);
                                trip_des = new LatLng(destAddress.getLatitude(), destAddress.getLongitude());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            DatabaseReference locRef = firebaseDatabase.getReference("Locations").child(driverID);
                            locRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    LatLng driverLoc = new LatLng(snapshot.child("l").child("0").getValue(Double.class), snapshot.child("l").child("1").getValue(Double.class));
                                    Marker driverMarker =  googleMap.addMarker(new MarkerOptions().position(driverLoc).title("Driver Here"));
                                    float[] distance = new float[1];
                                    Location.distanceBetween(trip_src.latitude, trip_src.longitude,
                                            driverLoc.latitude,
                                            driverLoc.longitude, distance);
                                    DecimalFormat df = new DecimalFormat("0.0");
                                    float dist_km = distance[0]/1000;
                                    //Log.d("Distance", Float.toString(distance[0]));
                                    String duration = "";
                                    if(dist_km < 0.1){
                                        duration = " has arrived";
                                    }else if(dist_km < 1.0){
                                        duration = " is less than 1km away";
                                    }else{
                                        duration = " is " + df.format(dist_km) + "km away";
                                    }
                                    driverName.setText(driver_name + duration);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //Log.d("Driver and PWD Loc", trip_src.toString() + trip_des.toString());
                            startRouting();

                        }
                    });

                }
            });
        }
    }

    private void startRouting() {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(trip_src, trip_des)
                .key(getString(R.string.maps_api_key))  //also define your api key here.
                .build();
        routing.execute();
        //Log.d("Driver and PWD Loc", routing.toString());
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


    @Override
    public void onRoutingFailure(RouteException e) {
        e.printStackTrace();
//        if(e != null){
//            Log.d("Driver and PWD Loc", e.toString());
//        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(trip_src, 16));
        //mMap.addMarker(markerOptions1);

        CameraUpdate center = CameraUpdateFactory.newLatLng(trip_src);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;

        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.psv_color_accent));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);
                //Log.d("Driver and PWD Loc", polyline.toString());
            }
            else {

            }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location");
        mMap.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        mMap.addMarker(endMarker);

    }

    @Override
    public void onRoutingCancelled() {
        //Log.d("Driver and PWD Loc Cancel", "Canceled");
    }
}