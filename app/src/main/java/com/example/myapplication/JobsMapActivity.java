package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class JobsMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 111;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDB = FirebaseUtils.connectFirebase();
        firebaseDBRef = firebaseDB.getReference(FirebaseUtils.JOBS_COLLECTION);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * COPIED FROM MapsActivity.java
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                        .getLongitude());

                Toast.makeText(JobsMapActivity.this, currentLocation.toString(), Toast.LENGTH_LONG).show();
                //add pin at user's location
                placeMarkerOnMap(currentLocation);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));

                //Read database to get all jobs
                getJobsFromDatabase();

            }
        }
    }

    private void getJobsFromDatabase(){
        //Executed after the jobs have been read and placed in an array from the database
        readData(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<Job> list) {
                //Place jobs on map
                placeJobMarkersOnMap(list);
            }
        });
    }

    private void readData(FirebaseCallback firebaseCallback) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Job> list = new ArrayList<Job>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Job job = snapshot.getValue(Job.class);
                    list.add(job);
                }
                firebaseCallback.onCallback(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        firebaseDBRef.addListenerForSingleValueEvent(valueEventListener);
    }

    /*
    The interface is used to immediately get the job results from the Firebase without needing an
    adapter. Received from:
    https://stackoverflow.com/questions/47847694/how-to-return-datasnapshot-value-as-a-result-of-a-method/47853774
     */
    private interface FirebaseCallback {
        void onCallback( ArrayList<Job> list);
    }

    private void placeJobMarkersOnMap(ArrayList<Job> jobList) {
        for (int i = 0; i < jobList.size(); i++)
        {
            //For each job that has a valid location, call the placeJobMarker function with the job information
            if (jobList.get(i).getLocation() != null)
            {
                placeJobMarker(jobList.get(i));
            }
        }
    }

    private String getAddress( LatLng latLng ) {

        Geocoder geocoder = new Geocoder( this );
        String addressText = "";
        List<Address> addresses = null;
        Address address = null;
        try {

            addresses = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 );

            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressText += (i == 0)?address.getAddressLine(i):("\n" + address.getAddressLine(i));
                }
            }
        } catch (IOException e ) {
        }
        return addressText;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    protected void placeMarkerOnMap(LatLng location) {
        MarkerOptions currentLocationMarker = new MarkerOptions().position(location);

        String titleStr = getAddress(location);
        currentLocationMarker.title(titleStr).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.addMarker(currentLocationMarker);
    }

    protected void placeJobMarker(Job job) {
        //Get current user location
        double latitude = mLastLocation.getLatitude();
        double longitude = mLastLocation.getLongitude();
        com.example.myapplication.Location currLocation = new com.example.myapplication.Location(latitude, longitude);

        //Get distance between current user location and specified job
        double distance = currLocation.getHaversineDistance(job.getLocation());

        //If the distance is within 10km, place green marker on the map
        if (job.getLocation().withinDistance(10.0, distance))
        {
            LatLng jobLocation = new LatLng(job.getLocation().getLatitude(), job.getLocation().getLongitude());
            String titleStr = job.getJobTitle();

            //Get distance to one decimal place
            double distanceToOneDecimal = Math.round(distance*10.0)/10.0;

            //Add information popup when the marker is clicked
            String snip = job.getDescription() + " (Distance: " + distanceToOneDecimal + "km)";

            Marker greenJob = mMap.addMarker(new MarkerOptions()
                    .position(jobLocation)
                    .title(titleStr)
                    .snippet(snip)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        //Otherwise if the distance is outside 10km, place yellow marker on the map
        else
        {
            LatLng jobLocation = new LatLng(job.getLocation().getLatitude(), job.getLocation().getLongitude());
            String titleStr = job.getJobTitle();

            //Get distance to one decimal place
            double distanceToOneDecimal = Math.round(distance*10.0)/10.0;

            //Add information popup when the marker is clicked
            String snip = job.getDescription() + " (Distance: " + distanceToOneDecimal + "km)";

            Marker yellowJob = mMap.addMarker(new MarkerOptions()
                    .position(jobLocation)
                    .title(titleStr)
                    .snippet(snip)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        //On marker click, show a popup with job information
        marker.showInfoWindow();
        return false;
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

    }
}
