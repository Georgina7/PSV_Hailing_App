package com.georgina.psvhailingapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

public class DriverMapsFragment extends Fragment {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
            mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            }
            mMap.setMyLocationEnabled(true);
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_maps, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        client = LocationServices.getFusedLocationProviderClient(getContext());
        getCurrentLocation();

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
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
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
}