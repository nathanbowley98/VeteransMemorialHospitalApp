package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * EmployerActivity class that manages the EmployerActivity events.
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
public class EmployerActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;

    TextView loginDisplay;
    Button logoutButton, createJobButton, searchButton, viewApplications;
    Button openmaps, payButton, yourJobsButton, reviewEmployee;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer);

        openmaps = findViewById(R.id.check);
        loginDisplay = (TextView) findViewById(R.id.employerLoginDisplay);
        logoutButton = (Button) findViewById(R.id.employerLogoutButton);
        createJobButton = (Button) findViewById(R.id.createJob);
        searchButton = (Button) findViewById(R.id.employerSearchButton);
        viewApplications = (Button) findViewById(R.id.employerApplications);
        reviewEmployee = (Button) findViewById(R.id.makeAReview_employer);
        yourJobsButton = (Button) findViewById(R.id.employerYourJobsButton);
        payButton = (Button) findViewById(R.id.employerPayButton);
//
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
            Intent intent = new Intent(EmployerActivity.this, RegisterUser.class);
            startActivity(intent);
        }

//        Colleague newColl = new Colleague("john@dal.ca","john");
//        Colleague newColl2 = new Colleague("joe@dal.ca","joe");
//        connectFirebase();
//        FirebaseDatabase.getInstance().getReference().child("colleagues").child(Session.getUserID()).child("userID").getRef().setValue(newColl);
//        FirebaseDatabase.getInstance().getReference().child("colleagues").child(Session.getUserID()).child("userID2").getRef().setValue(newColl2);

        reviewEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(EmployerActivity.this, BrowseColleagues.class);
                startActivity(newIntent);
            }
        });

        createJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(EmployerActivity.this, CreateJob.class);
                startActivity(newIntent);
            }
        });



        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session.logout();
            }
        });

        viewApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployerActivity.this, ViewApplications.class);
                startActivity(intent);
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployerActivity.this, JobSearch.class);
                startActivity(intent);
            }
        });

        openmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployerActivity.this, MapsActivity.class));
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployerActivity.this, SendPayment.class));
            }
        });


        yourJobsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployerActivity.this, JobEmployerActivity.class));
            }
        });
        findViewById(R.id.employerHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmployerActivity.this, HistoryActivity.class));
            }
        });
    }

    /**
     * logout method removes credentials added to SharedPreferences. Will take user to MainActivity
     * instead of EmployeeActivity on applications start.
     * @author Nathan Horne
     */
    private void logout()
    {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String userHash = sharedPref.getString("Key_hash", "INVALID HASH");

        if (!userHash.equals("INVALID HASH")) {
            connectFirebase();
            firebaseDBRef.child(userHash).getRef().child("loginState").setValue(false);
        }

        editor.remove("Key_email");
        editor.remove("Key_password");
        editor.remove("Key_type");
        editor.apply();


        startActivity( new Intent( EmployerActivity.this, MainActivity.class));
    }

    /**
     * logout method removes credentials added to SharedPreferences. Will take user to MainActivity
     * instead of EmployeeActivity on applications start.
     * @author Nathan Horne and Nathanael Bowley (hash functionality)
     */
    private void connectFirebase(){
        firebaseDB = FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL);
        firebaseDBRef = firebaseDB.getReference("users");

    }
}
