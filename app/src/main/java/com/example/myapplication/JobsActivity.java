package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.util.MapUtils;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JobsActivity  extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private RecyclerView recyclerview;
    private JobAdapter adapter;
    public List<Job> list = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);
        firebaseDB = FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL);
        firebaseDBRef = firebaseDB.getReference("jobs");
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JobAdapter(list);
        recyclerview.setAdapter(adapter);
        adapter.setOnItemClickListener(new JobAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Job job = list.get(position);
                Intent intent = new Intent(JobsActivity.this,JobDetailActivity.class);
                intent.putExtra("job",job);
                startActivity(intent);
            }
        });
        init();
    }

    private void init() {
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
    private void getJobs() {
        firebaseDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Job job = snapshot.getValue(Job.class);
                    if (mLastLocation!=null) {
                        double m = gps2m(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                job.getLocation().getLatitude(), job.getLocation().getLongitude());
                        if (  m<10000) {
                            list.add(job);
                        }
                    }else{
                        list.add(job);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION},111);
            return;
        }
         mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        getJobs();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    private final double EARTH_RADIUS = 6378137.0;

    private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {

        double radLat1 = (lat_a * Math.PI / 180.0);

        double radLat2 = (lat_b * Math.PI / 180.0);

        double a = radLat1 - radLat2;

        double b = (lng_a - lng_b) * Math.PI / 180.0;

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)

                + Math.cos(radLat1) * Math.cos(radLat2)

                * Math.pow(Math.sin(b / 2), 2)));

        s = s * EARTH_RADIUS;

        s = Math.round(s * 10000) / 10000;

        return s;

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
