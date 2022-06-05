package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateJob extends AppCompatActivity {

    private FirebaseDatabase firebaseDB;
    private DatabaseReference jobsRef;
    private String selectedCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_job);

        Button createJobBtn = findViewById(R.id.createJobSubmitButton);
        TextView email = findViewById(R.id.createJobEmail);
        email.setText(Session.getEmail());

        Spinner jobCategorySpinner = findViewById(R.id.categorySpinner);
        String[] jobCategories = getResources().getStringArray(R.array.categories);
        @SuppressLint("ResourceType") ArrayAdapter<String> jobCategoryAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, jobCategories);
        jobCategoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        jobCategorySpinner.setAdapter(jobCategoryAdapter);

        createJobBtn.setOnClickListener(view -> {
            selectedCategory = jobCategorySpinner.getSelectedItem().toString();
            firebaseDB = FirebaseUtils.connectFirebase();
            jobsRef = firebaseDB.getReference().child(FirebaseUtils.JOBS_COLLECTION);
            Job job = createJob();
            if (job != null) {
                pushJob(job, jobsRef);
                Toast.makeText(CreateJob.this,"Success", Toast.LENGTH_SHORT).show();
                Intent newIntent = new Intent(CreateJob.this, EmployerActivity.class);
                startActivity(newIntent);
            }
        });


    }

    /**
     * createJob method that takes the EditText contents from create_job.xml and creates a Job
     * object to be returned.
     * @return Job object if input is valid, otherwise NULL.
     * @author: John Corsten
     * @refactorer: Nathanael Bowley
     */
    protected Job createJob() {
        if(validateInput()) {

            EditText jobEmailEditText = findViewById(R.id.createJobEmail);
            EditText jobTitleEditText = findViewById(R.id.createJobTitle);
            EditText jobDescEditText = findViewById(R.id.createJobDescription);
            EditText jobHourlyRateEditText = findViewById(R.id.createJobHourlyRate);

            // Dummy values to be used until location functionality is added in another user story
            double longitude = -63.6370;
            double latitude = 44.6930;

            Location location = new Location(latitude, longitude);

            String jobEmail = jobEmailEditText.getText().toString();
            String jobTitle = jobTitleEditText.getText().toString();
            String jobDesc = jobDescEditText.getText().toString();
            String userHash = Session.getUserID();
            double jobHourlyRate;

            try {
                jobHourlyRate = Integer.parseInt(jobHourlyRateEditText.getText().toString());
            } catch (NumberFormatException e) {
                jobHourlyRate = 0;
            }

            Job job = new Job(jobEmail, jobTitle, jobDesc, location, userHash, selectedCategory);
            job.setCompensation(jobHourlyRate);
            return job;
        }
        else {
            Toast.makeText(CreateJob.this,"Invalid Input", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    protected boolean pushJob(Job job, DatabaseReference jobsRef) {
        //Push unique job details under "userID" node in jobs
        //userID needs to be mapped to logged in user

        // Stores job in job node on realtime database, filed under the hash corresponding to the user
        // that created the job
        //jobsRef.child(getUserHash()).push().setValue(job);
        jobsRef.push().setValue(job);

        return true;
    }

    protected boolean validateInput() {
        EditText jobTitle = findViewById(R.id.createJobTitle);
        EditText jobDesc = findViewById(R.id.createJobDescription);
        EditText wage = findViewById(R.id.createJobHourlyRate);

        boolean validTitle =  validateTitle(jobTitle.getText().toString());
        boolean validDesc = validateJobDescription(jobDesc.getText().toString());
        boolean validWage = validateWage(wage.getText().toString());

        return validTitle && validDesc && validWage;

    }

    protected static boolean validateTitle(String jobTitle) {
        Pattern fnPattern = Pattern.compile("^.{1,200}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = fnPattern.matcher(jobTitle.trim());

        return matcher.matches();
    }

    protected static boolean validateJobDescription(String desc) {
        Pattern fnPattern = Pattern.compile("^.{1,500}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = fnPattern.matcher(desc.trim());

        return matcher.matches();
    }

    protected static boolean validateWage(String wage) {
        Pattern fnPattern = Pattern.compile("^[0-9]{1,20}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = fnPattern.matcher(wage.trim());

        return matcher.matches();
    }

    /**
     * Method validates a given longitude or latitude (provided in degrees)
     * @param coordinate
     * @return
     * @author John Corsten
     */
    protected static boolean validateLongLat(double coordinate){
        // Valid longitudes and latitudes are both between -180 degrees and 180 degrees
        if (coordinate >= 180 || coordinate <= - 180){
            return false;
        }
        return true;
    }

}
