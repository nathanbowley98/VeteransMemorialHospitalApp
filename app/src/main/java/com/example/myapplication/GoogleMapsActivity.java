package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityGoogleMapsBinding;
import com.google.android.gms.tasks.Task;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityGoogleMapsBinding binding; // bypasses the need to use findViewById()
    private static final Integer REQUEST_CODE = 111;
    private FusedLocationProviderClient client;
    private SupportMapFragment mapFragment;
    private Button setLocationBtn;

    private boolean isPermissionGranted = false;
    private LatLng currentUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGoogleMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkLocationPermissions();
        initMap();

        client = new FusedLocationProviderClient(this);
        getCurrentLocation();

        setLocationBtn = binding.setLocationBtn;
        setLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUserLocation != null){
                    // go to registration page
                    Intent intent = new Intent(GoogleMapsActivity.this, RegisterUser.class);
                    intent.putExtra("user location", currentUserLocation);
                    startActivity(intent);
                } else{
                    Toast.makeText(getApplicationContext(), "Please get your location first!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initMap() {
 //       if (isPermissionGranted) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            // manages life cycle of the map
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
 //       }
    }

    private void checkLocationPermissions() {
        final String fineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
        final String coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;

        boolean isFineLocationGranted = ContextCompat
                .checkSelfPermission(this, fineLocation) == PackageManager.PERMISSION_GRANTED;
        boolean isCoarseLocationGranted = ContextCompat
                .checkSelfPermission(this, coarseLocation) == PackageManager.PERMISSION_GRANTED;

        isPermissionGranted = isFineLocationGranted && isCoarseLocationGranted;
    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION},111);
            return;
        }

        // get current location of the user and set a marker there
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(location -> {
            if(location != null){
               mapFragment.getMapAsync(googleMap -> {
                   LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                   MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You're here");
                   googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                   googleMap.addMarker(markerOptions).showInfoWindow();
               });
               currentUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            } else{
                Toast.makeText(this, "Permission denied by user", Toast.LENGTH_LONG);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION},111);
            return;
        }
        mMap.setMyLocationEnabled(true);

    }
}