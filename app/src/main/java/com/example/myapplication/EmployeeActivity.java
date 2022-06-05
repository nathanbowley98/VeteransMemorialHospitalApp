package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * EmployeeActivity class that manages the EmployeeActivity events.
 * @authors: Nathanael Bowley,
 *          John Corsten,
 *          Nathan Horne,
 *          Ted Graveson,
 *          Hongzheng Ding,
 *          Tianhao Jia,
 *          Saher Anwar Ziauddin
 * @course: CSCI3130 @ Dalhousie University.
 * @semester: Winter 2022
 * @group: Group 4
 * @clientTA: Disha Malik
 */
public class EmployeeActivity extends AppCompatActivity {


    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;

    TextView loginDisplay;
    Button logoutButton;
    Button searchButton, offersButton;

    Button openMap, reviewEmployer;
    Button jobs;

    Button showJobsOnMapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        Toast.makeText(getApplicationContext(), "Employee Activity", Toast.LENGTH_LONG).show();
        connectFirebase();
        jobs = findViewById(R.id.jobs);
        openMap = findViewById(R.id.mapbutton);
        loginDisplay = (TextView) findViewById(R.id.employeeLoginDisplay);
        logoutButton = (Button) findViewById(R.id.employeeLogoutButton);
        searchButton = (Button) findViewById(R.id.employeeSearchButton);
        offersButton = (Button) findViewById(R.id.viewOffers);
        reviewEmployer = (Button) findViewById(R.id.makeAReview_employee);
        showJobsOnMapButton = (Button) findViewById(R.id.showJobsOnMapButton);

        offersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeeActivity.this, ViewOffers.class);
                startActivity(intent);
            }
        });

        reviewEmployer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeeActivity.this, BrowseColleagues.class);
                startActivity(intent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeeActivity.this, JobSearch.class);
                startActivity(intent);
            }
        });


        Bundle extras = getIntent().getExtras();


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean hasLogIn = sharedPreferences.contains(Session.LOGIN);
        boolean state = false;
        if (hasLogIn) {
            state = sharedPreferences.getBoolean(Session.LOGIN, false);
        }

        //this shouldn't be possible so that means that the user is in the wrong spot
        if (!state) {
            //DO NOT REMOVE THIS IS FOR US-3 ACCEPTANCE TEST FUNCTIONALITY.
            Intent intent = new Intent(EmployeeActivity.this, RegisterUser.class);
            startActivity(intent);
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session.logout();
            }
        });


        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployeeActivity.this, MapsActivity.class));
            }
        });
        jobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployeeActivity.this,JobsActivity.class));
            }
        });

        showJobsOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployeeActivity.this, JobsMapActivity.class));
            }
        });
    }

    /**
     * logout method removes credentials added to SharedPreferences. Will take user to MainActivity
     * instead of EmployeeActivity on applications start.
     * @author Nathan Horne and Nathanael Bowley (hash functionality)
     */
    private void logoutAndChangeLoginState()
    {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String userHash = sharedPref.getString("Key_hash", "INVALID HASH");

        if (!userHash.equals("INVALID HASH")) {
            firebaseDBRef.child(userHash).getRef().child("loginState").setValue(false);
        }

        editor.remove("Key_email");
        editor.remove("Key_password");
        editor.remove("Key_type");
        editor.remove("Key_hash");
        editor.apply();


        startActivity( new Intent( EmployeeActivity.this, MainActivity.class));
    }

    /**
     * connectFirebase method that acts to connect the firebase using the firebase url
     * @author: everyone
     */
    private void connectFirebase(){
        firebaseDB = FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL);
        firebaseDBRef = firebaseDB.getReference("users");

    }
}