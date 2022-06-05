package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ViewApplications extends Activity {

    private TextView applicationEmail;
    private TextView applicationName;

    private Button applicationAccept;
    private Button applicationIgnore;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private TextView employeeEmailET;
    private TextView employeeNameET;
    private Button ignore;
    private Button accept;

    private FirebaseDatabase firebaseDB;
    private DatabaseReference appsRef;
    private Button homeButton;

    //stored in order of employerEmail, jobTitle, description, hourlyRate
    private String[] inputs = new String[4];


    private RecyclerView recyclerView;
    applicationAdapter adapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the
    // Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.applications_page);
        homeButton = (Button) findViewById(R.id.applicationsToEmployer);

        // Create a instance of the database and get
        // its reference
        mbase = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.jobApplicationsRecyclerView);

        // To display the Recycler view linearly
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // It is a class provide by the FirebaseUI to make a
        // query in the database to fetch appropriate data
        FirebaseRecyclerOptions<Application> options
                = new FirebaseRecyclerOptions.Builder<Application>()
                .setQuery(mbase.child("applications").child(Session.getUserID()), Application.class)
                .build();


        // Connecting object of required Adapter class to
        // the Adapter class itself
        adapter = new applicationAdapter(options);
        // Connecting Adapter class with the Recycler view*/
        recyclerView.setAdapter(adapter);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewApplications.this, EmployerActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void pushApplication(DatabaseReference application, Application app) {
        application.push().setValue(app);
    }

    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }


}
