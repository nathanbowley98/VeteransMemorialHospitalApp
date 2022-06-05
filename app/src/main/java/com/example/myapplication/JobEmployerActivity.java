package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JobEmployerActivity extends Activity {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private TextView noJobsTextView;
    private RecyclerView recyclerview;
    private JobEmployerAdapter adapter;
    public static List<Job> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_employer);
        firebaseDB = FirebaseUtils.connectFirebase();
        firebaseDBRef = firebaseDB.getReference(FirebaseUtils.JOBS_COLLECTION);
        noJobsTextView = findViewById(R.id.employerJobsNoJobs);
        recyclerview = findViewById(R.id.employerJobsRecyclerView);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JobEmployerAdapter(list);
        recyclerview.setAdapter(adapter);

        init();
    }


    public void init() {

        getJobs();



    }

    private void getJobs() {
        firebaseDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Job job = snapshot.getValue(Job.class);
                    if (job.getEmployerEmail().equals(Session.getEmail())) {
                        list.add(job);
                    }

                }
                if (list.isEmpty()) {
                    noJobsTextView.setVisibility(View.VISIBLE);
                }
                else {
                    noJobsTextView.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}





