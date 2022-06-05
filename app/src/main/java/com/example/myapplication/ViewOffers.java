package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewOffers extends Activity {

    private TextView offerEmail;
    private TextView offerName;

    private Button offerAccept;
    private Button offerIgnore;

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
    OfferAdapter adapter; // Create Object of the Adapter class
    DatabaseReference mbase; // Create object of the
    // Firebase Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_offers_page);

        homeButton = (Button) findViewById(R.id.offersToEmployee);

        // Create a instance of the database and get
        // its reference
        mbase = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.jobOffersRecyclerView);

        // To display the Recycler view linearly
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // It is a class provide by the FirebaseUI to make a
        // query in the database to fetch appropriate data
        FirebaseRecyclerOptions<Offer> options
                = new FirebaseRecyclerOptions.Builder<Offer>()
                .setQuery(mbase.child("offers").child(Session.getUserID()), Offer.class)
                .build();

        // Connecting object of required Adapter class to
        // the Adapter class itself
        adapter = new OfferAdapter(options);
        // Connecting Adapter class with the Recycler view*/
        recyclerView.setAdapter(adapter);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewOffers.this, EmployeeActivity.class);
                startActivity(intent);
            }
        });
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