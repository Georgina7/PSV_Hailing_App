package com.georgina.psvhailingapp;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DriverMapsFragment extends Fragment implements RoutingListener {

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;

    private RecyclerView activeTripsRecyclerView;
    private ActiveTripsAdapter adapter;
    private ArrayList<String> tripIDs;
    private ArrayList<Trip> activeTripsData;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //polyline object
    private List<Polyline> polylines=null;
    protected LatLng trip_src, trip_des;

    com.google.android.gms.location.LocationRequest locationRequest;
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            //super.onLocationResult(locationResult);
            if(locationResult == null){
                return;
            }
            databaseReference = firebaseDatabase.getReference("Locations");
            GeoFire geoFire = new GeoFire(databaseReference);
            for(Location location: locationResult.getLocations()){
                Log.d("onLocationResult", location.toString());
                geoFire.removeLocation(mCurrentUser.getUid());
                geoFire.setLocation(mCurrentUser.getUid(), new GeoLocation(location.getLatitude(), location.getLongitude()));
            }
        }
    };

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
            mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            }else{
                mMap.setMyLocationEnabled(true);
            }

        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_maps, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        if(getActivity().getIntent() != null){
            if(getActivity().getIntent().getStringExtra("SELECTED_TRIP_SOURCE") != null){
                setPolyline();
            }
//            String source = getActivity().getIntent().getStringExtra("SELECTED_TRIP_SOURCE");
//            Toast.makeText(getContext(), source, Toast.LENGTH_SHORT).show();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();

        client = LocationServices.getFusedLocationProviderClient(getContext());
        getCurrentLocation();

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        tripIDs = new ArrayList<>();
        activeTripsData = new ArrayList<Trip>();
        activeTripsRecyclerView = view.findViewById(R.id.recycler_active_driver_trips);
        activeTripsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActiveTripsAdapter(activeTripsData, getContext(), tripIDs);
        activeTripsRecyclerView.setAdapter(adapter);
        initializeRouteData();

        return view;
    }

    private void initializeRouteData() {
        DatabaseReference tripsRef = firebaseDatabase.getReference("Trips");
        tripsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                tripIDs.clear();
                activeTripsData.clear();
                Iterator<DataSnapshot> trips = snapshot.getChildren().iterator();
                while (trips.hasNext()){
                    DataSnapshot trip = trips.next();
                    if(trip.child("status").getValue().equals("pending") || trip.child("status").getValue().equals("started") && trip.child("driverID").getValue().equals(mCurrentUser.getUid())){
                        //Log.d("Trips", trip.getValue().toString());
                        activeTripsData.add(trip.getValue(Trip.class));
                        tripIDs.add(trip.getKey());

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        if (mCurrentUser == null){
            Intent intent = new Intent(getContext(),LoginActivity.class);
            startActivity(intent);
        }else{
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            getLastLocation();
                checkSettingsAndStartLocationUpdates();
            } else {
                Log.d("Permissions", "Ask the user to turn on location");
            }

        }
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
        else {
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                                    .title("Your Location");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                            googleMap.addMarker(markerOptions);
                        }
                    });

                }
            });
        }
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Settings of device are satisfied and we can start location updates
                startLocationUpdates();
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(getActivity(), 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            return;
        }
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }
    private void stopLocationUpdates(){
        client.removeLocationUpdates(locationCallback);
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

    public void setPolyline(){
        String source = getActivity().getIntent().getStringExtra("SELECTED_TRIP_SOURCE");
        String destination = getActivity().getIntent().getStringExtra("SELECTED_TRIP_DEST");
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(source, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert addressList != null;
        Address sourceAddress = addressList.get(0);
        trip_src = new LatLng(sourceAddress.getLatitude(), sourceAddress.getLongitude());
        List<Address> addressList1 = null;
        try {
            addressList1 = geocoder.getFromLocationName(destination, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert addressList1 != null;
        Address destAddress = addressList1.get(0);
        trip_des = new LatLng(destAddress.getLatitude(), destAddress.getLongitude());
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(trip_src, trip_des)
                .key(getString(R.string.maps_api_key))  //also define your api key here.
                .build();
        routing.execute();

    }


    @Override
    public void onRoutingFailure(RouteException e) {
        e.printStackTrace();
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(getContext(),"Finding Route...",Toast.LENGTH_LONG).show();

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
                polyOptions.color(getResources().getColor(R.color.pwd_color_accent));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

            }
            else {

            }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("PickUp Location");
        mMap.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        mMap.addMarker(endMarker);

        Log.d("PolyLine","Already Set");
    }

    @Override
    public void onRoutingCancelled() {
        setPolyline();
    }
}