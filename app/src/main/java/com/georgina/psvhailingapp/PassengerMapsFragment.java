package com.georgina.psvhailingapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PassengerMapsFragment extends Fragment implements RoutingListener {

    private static final String TAG = " ";
    private static final int RESULT_CANCELED = 1;
    private static final int RESULT_CANCELED1 = 2;
    private static final int RESULT_OK1 = 1;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private Button mSearch;
    private TextInputLayout mFrom;
    private TextInputLayout mWhere;
    public static String EXTRA_SOURCE = "source";
    public static String EXTRA_DEST = "dest";
    //    private TextView mDestination;
//    private TextView mStart;
    private ImageView mHeaderArrow;
    private ConstraintLayout mRoutesBottomSheet;
    private LinearLayout mHeaderLayout;
    private LinearLayout mInputLayout;
    private BottomSheetBehavior mBottomSheetBehavior;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference driverDatabaseReference, locationDatabaseReference;
    private DatabaseReference user_driverDatabaseReference;
    private RecyclerView routesRecyclerView;
    private DriverRouteAdapter adapter;
    private ArrayList<String> routesList;
    private static int AUTOCOMPLETE_REQUEST_CODE_FROM;
    private static int AUTOCOMPLETE_REQUEST_CODE_WHERE;
    //polyline object
    private List<Polyline> polylines=null;
    protected LatLng trip_src, trip_des;

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
        mCurrentUser = mAuth.getCurrentUser();
        mRoutesBottomSheet = view.findViewById(R.id.available_routes_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mRoutesBottomSheet);
        mBottomSheetBehavior.setHideable(false);
        mHeaderLayout = view.findViewById(R.id.header_layout);
        mHeaderArrow = view.findViewById(R.id.arrow);
        mInputLayout = view.findViewById(R.id.input_location);
        mSearch = view.findViewById(R.id.btn_search);
        mFrom = view.findViewById(R.id.from);
        mWhere = view.findViewById(R.id.where_to);

        client = LocationServices.getFusedLocationProviderClient(getContext());
        getCurrentLocation();



        Places.initialize(getContext(), getString(R.string.google_maps_key));
        PlacesClient placesClient = Places.createClient(getContext());

        firebaseDatabase = FirebaseDatabase.getInstance();
        driverDatabaseReference = firebaseDatabase.getReference("Drivers");
        locationDatabaseReference = firebaseDatabase.getReference("Locations");

        routesRecyclerView = view.findViewById(R.id.recycler_routes);
        routesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        routesList = new ArrayList<>();
        adapter = new DriverRouteAdapter(routesList, getContext());
        routesRecyclerView.setAdapter(adapter);
        initializeRouteData();

        mSearch.setVisibility(View.GONE);
        mFrom.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.isFocused()) {
                    mSearch.setVisibility(View.VISIBLE);
                    AUTOCOMPLETE_REQUEST_CODE_FROM = 1;

                    // Set the fields to specify which types of place data to
                    // return after the user has made a selection.
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                    RectangularBounds bounds = RectangularBounds.newInstance(
                            new LatLng(-33.880490, 151.184363),
                            new LatLng(-33.858754, 151.229596));
                    // Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .setTypeFilter(TypeFilter.GEOCODE)
                            .setLocationBias(bounds)
                            .setCountries(Arrays.asList("KE"))
                            .build(getContext());
                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_FROM);
                }
            }
        });
        mWhere.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.isFocused()) {
                    mSearch.setVisibility(View.VISIBLE);

                    AUTOCOMPLETE_REQUEST_CODE_WHERE = 2;

                    // Set the fields to specify which types of place data to
                    // return after the user has made a selection.
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                    RectangularBounds bounds = RectangularBounds.newInstance(
                            new LatLng(-33.880490, 151.184363),
                            new LatLng(-33.858754, 151.229596));
                    // Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .setTypeFilter(TypeFilter.ESTABLISHMENT)
                            .setLocationBias(bounds)
                            .setCountries(Arrays.asList("KE"))
                            .build(getActivity());

                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_WHERE);

                }
            }
        });


        driverDatabaseReference = firebaseDatabase.getReference("Drivers");
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
            mBottomSheetBehavior.setHideable(false);

        }

        mHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    mInputLayout.setVisibility(View.GONE);
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    mInputLayout.setVisibility(View.VISIBLE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_FROM) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                mFrom.getEditText().setText(place.getName());
                if(checkInputsPresent()){
                    setPolyline();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.

            }
            return;
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_WHERE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                mWhere.getEditText().setText(place.getName());
                if(checkInputsPresent()){
                    setPolyline();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        } else {
            Place place = Autocomplete.getPlaceFromIntent(data);
            Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            mWhere.getEditText().setText(place.getName());
        }
    }

    private void initializeRouteData() {
        driverDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                routesList.clear();
                Iterator<DataSnapshot> routes = snapshot.getChildren().iterator();
                while (routes.hasNext()){
                    DataSnapshot route = routes.next();
                    Log.d("Routes", route.toString());
                    if(route.child("availability").getValue().equals("active") &&
                            route.child("status").getValue().equals("enabled")){
                        routesList.add(route.child("routes").getValue().toString());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
//        driverDatabaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
//                DriverDetails driverDetails = snapshot.getValue(DriverDetails.class);
//                    list.add(driverDetails);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
//                DriverDetails driverDetails = snapshot.getValue(DriverDetails.class);
//                list.add(driverDetails);
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
//                DriverDetails driverDetails = snapshot.getValue(DriverDetails.class);
//                list.add(driverDetails);
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });

//        list.add(new DriverDetails("DL-1234567","KBC 778C","Madaraka",4,"active"));
        //  adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Small Change", Toast.LENGTH_SHORT);
    }

    @Override
    public void onStart(){
        super.onStart();
        if (mCurrentUser == null){
            Intent intent = new Intent(getContext(),LoginActivity.class);
            startActivity(intent);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        } else {
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                            mBottomSheetBehavior.setHideable(false);
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d("My Location", location.toString());
                            locationDatabaseReference.child(mCurrentUser.getUid()).setValue(location);
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                                    .title("Your Location");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                            googleMap.addMarker(markerOptions);

                            mSearch.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String source = mFrom.getEditText().getText().toString();
                                    String destination = mWhere.getEditText().getText().toString();
//                                    Geocoder geocoder = new Geocoder(getContext());

                                    if (!validateSource() || !validateDestination()) {
                                        return;
                                    }
                                    else {
//                                        List<Address> addressList = null;
//                                        try {
//                                            addressList = geocoder.getFromLocationName(source, 1);
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                        assert addressList != null;
//                                        Address sourceAddress = addressList.get(0);
//                                        LatLng sLatlng = new LatLng(sourceAddress.getLatitude(), sourceAddress.getLongitude());
//                                        MarkerOptions markerOptions1 = new MarkerOptions().position(sLatlng)
//                                                .title(source);
//                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sLatlng, 20));
//                                        mMap.addMarker(markerOptions1);
//
//                                        List<Address> addressList1 = null;
//                                        try {
//                                            addressList1 = geocoder.getFromLocationName(destination, 1);
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                        assert addressList1 != null;
//                                        Address destAddress = addressList1.get(0);
//                                        LatLng dLatlng = new LatLng(destAddress.getLatitude(), destAddress.getLongitude());
//                                        MarkerOptions markerOptions2 = new MarkerOptions().position(dLatlng)
//                                                .title(destination);
//                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dLatlng, 20));
//                                        mMap.addMarker(markerOptions2);

                                        Intent bookingIntent = new Intent(getContext(),SelectTimeandDateActivity.class);
                                        bookingIntent.putExtra(EXTRA_SOURCE,source);
                                        bookingIntent.putExtra(EXTRA_DEST,destination);
                                        bookingIntent.putExtra("Activity", "PassengerMap");
                                        startActivity(bookingIntent);
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private boolean validateSource(){
        String routes = mFrom.getEditText().getText().toString().trim();
        if(routes.isEmpty()){
            mFrom.setError(getString(R.string.empty_field));
            return false;
        }
        else {
            mFrom.setError(null);
            return true;
        }

    }

    private boolean validateDestination(){
        String routes =  mWhere.getEditText().getText().toString().trim();
        if(routes.isEmpty()){
            mWhere.setError(getString(R.string.empty_field));
            return false;
        }
        else {
            mWhere.setError(null);
            return true;
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

    public boolean checkInputsPresent(){
        String source = mFrom.getEditText().getText().toString().trim();
        String dest = mWhere.getEditText().getText().toString().trim();
        if(source.isEmpty() || dest.isEmpty()){
            return false;
        }else{
            return true;
        }

    }

    public void setPolyline(){
        String source = mFrom.getEditText().getText().toString();
        String destination = mWhere.getEditText().getText().toString();
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(source, 1);
            assert addressList != null;
            Address sourceAddress = addressList.get(0);
            trip_src = new LatLng(sourceAddress.getLatitude(), sourceAddress.getLongitude());
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

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(trip_src, trip_des)
                .key(getString(R.string.maps_api_key))  //also define your api key here.
                .build();
        routing.execute();
        Log.d("Driver and PWD Loc", trip_src.toString() + trip_des.toString());
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
//        MarkerOptions markerOptions1 = new MarkerOptions().position(trip_src)
//                .title("Source");
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
        setPolyline();
    }
}