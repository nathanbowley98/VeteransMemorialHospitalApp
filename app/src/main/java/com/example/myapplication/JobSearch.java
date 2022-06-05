package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

import java.util.ArrayList;

public class JobSearch extends Activity {

    private EditText searchJobEmailET;
    private EditText searchJobTitleET;
    private EditText searchJobDescriptionET;
    private EditText searchJobHourlyRateET;
    private Button searchJobButton;

    private RecyclerView recyclerView;
    private ViewJobAdapter viewJobAdapter;

    private TextView jobLayoutJobTitle;
    private TextView jobLayoutEmployerEmail;
    private TextView jobLayoutDescription;
    private TextView jobLayoutHourlyRate;
    private TextView jobLayoutLatitude;
    private TextView jobLayoutLongitude;
    private Button jobLayoutApply;
    private Button jobLayoutViewOnMap;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private FirebaseDatabase firebaseDB;
    private DatabaseReference jobsRef;

    //stored in order of employerEmail, jobTitle, description, hourlyRate
    private String[] inputs = new String[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_search);
        init();

        firebaseDB = FirebaseUtils.connectFirebase();
        jobsRef = firebaseDB.getReference().child(FirebaseUtils.JOBS_COLLECTION);

        //citation based on code from Dhrumils lab presentation on march 2nd in this course csci3130
        FirebaseRecyclerOptions<Job> options = new FirebaseRecyclerOptions.Builder<Job>()
                .setQuery(FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL)
                        .getReference().child("jobs"), Job.class)
                .build();

        viewJobAdapter = new ViewJobAdapter(options);
        recyclerView.setAdapter(viewJobAdapter);
        //end citation

        setSearchButtonListener();

    }

    /**
     * init method that links the parts of the job_search.xml file to the JobSearch.java fields.
     * @author: Nathanael Bowley
     */
    private void init() {

        searchJobEmailET = findViewById(R.id.searchEmployerEmail);
        searchJobTitleET = findViewById(R.id.searchJobTitle);
        searchJobDescriptionET = findViewById(R.id.searchDescription);
        searchJobHourlyRateET = findViewById(R.id.searchHourlyRate);
        searchJobButton = findViewById(R.id.searchJobButton);

        recyclerView = findViewById(R.id.searchJobRecyclerView);

        //textviews for results

        jobLayoutJobTitle = recyclerView.findViewById(R.id.jobLayoutJobTitle);
        jobLayoutEmployerEmail = recyclerView.findViewById(R.id.jobLayoutEmployerEmail);
        jobLayoutDescription = recyclerView.findViewById(R.id.jobLayoutDescription);
        jobLayoutHourlyRate = recyclerView.findViewById(R.id.jobLayoutHourlyRate);
        jobLayoutLatitude = recyclerView.findViewById(R.id.jobLayoutLatitude);
        jobLayoutLongitude = recyclerView.findViewById(R.id.jobLayoutLongitude);

        jobLayoutApply = recyclerView.findViewById(R.id.jobLayoutApply);
        jobLayoutViewOnMap = recyclerView.findViewById(R.id.jobLayoutViewOnMap);

        recyclerView.setLayoutManager(new WrapLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        String employerEmail = null;
        String jobTitle = null;
        String description = null;
        String hourlyRate = null;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        boolean hasJobEmailText = sharedPreferences.contains("searchEmployerEmail");
        boolean hasJobTitle = sharedPreferences.contains("searchJobTitle");
        boolean hasDescription = sharedPreferences.contains("searchDescription");
        boolean hasHourlyRate = sharedPreferences.contains("searchHourlyRate");

        if (hasJobEmailText || hasJobTitle || hasDescription || hasHourlyRate) {
            employerEmail = sharedPreferences.getString("searchEmployerEmail", null);
            jobTitle = sharedPreferences.getString("searchJobTitle", null);
            description = sharedPreferences.getString("searchDescription", null);
            hourlyRate = sharedPreferences.getString("searchHourlyRate", null);
            searchJobEmailET.setText(employerEmail);
            searchJobTitleET.setText(jobTitle);
            searchJobDescriptionET.setText(description);
            searchJobHourlyRateET.setText(hourlyRate);
        }

    }

    /**
     * setSearchButtonListener method that gets the contents of the 4 editText boxes and stores them
     * inside of the String[] array to determine if any inputs are to be checked, if all are left
     * blank then no search will be conducted, however if even one is non empty then
     * searchJobs method will be called.
     * @author: Nathanael Bowley
     */
    private void setSearchButtonListener() {
        searchJobButton.setOnClickListener(view -> {
            inputs[0] = searchJobEmailET.getText().toString();
            inputs[1] = searchJobTitleET.getText().toString();
            inputs[2] = searchJobDescriptionET.getText().toString();
            inputs[3] = searchJobHourlyRateET.getText().toString();

            putSearchIntoSharedPreferences(inputs);

            boolean searchableString = false;
            for (int i = 0; i< inputs.length; i++) {
                if (!inputs[i].trim().isEmpty()) {
                    searchableString = true;
                }
                else {
                    inputs[i] = null;
                }
            }

            if (searchableString) {
                searchJobs(inputs);
            }
        });
    }

    /**
     * putSearchIntoSharedPreferences method that puts the current searched strings
     * into the shared preferences for later retrieval.
     * @param inputs String[] array of the inputs from the user to be saved
     * @author: Nathanael Bowley
     */
    private void putSearchIntoSharedPreferences(String[] inputs) {
        if (editor != null) {
            editor.putString("searchEmployerEmail", inputs[0]).apply();
            editor.putString("searchJobTitle", inputs[1]).apply();
            editor.putString("searchDescription", inputs[2]).apply();
            editor.putString("searchHourlyRate", inputs[3]).apply();
            editor.commit();
        }
    }

    /**
     * searchJobs method that takes in an array of Strings as the searchPreferences and searches
     * the firebase database to see if any of the chosen names or properties chosen match what the
     * which then shows the corresponding searched inputs on the screen.
     * user entered in.
     * @param searchPreferences String[] array that is used as the search preferences of the user
     * @author: Nathanael Bowley
     */
    private void searchJobs(String[] searchPreferences) {



        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //citation based on code from csci3130 winter tutorial on march 2nd, 2022.
                int childrenCount = (int) snapshot.getChildrenCount();
                if (snapshot.exists() && childrenCount > 0) {

                    //searches among each child in jobs

                    //pretraverse and hide all
                    ViewJobAdapter.setJobArrayList(new ArrayList<Job>(10));
                    ArrayList<ViewJobAdapter.JobViewHolder> viewHolders = ViewJobAdapter.getHolderArrayList();
                    for (int i = 0; i< viewHolders.size(); i++) {

                        if (i >= childrenCount) {
                            viewHolders.remove(i);
                        }
                        else {
                            viewJobAdapter.onBindViewHolder(viewHolders.get(i), true);
                        }

                    }


                    int currPosition = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        Job job = dataSnapshot.getValue(Job.class);

                        String employerEmail = job.getEmployerEmail();
                        String jobTitle = job.getJobTitle();
                        String description = job.getDescription();
                        String compensation = String.valueOf(job.getCompensation());

                        String[] currentJob = {employerEmail, jobTitle, description, compensation};

                        boolean saveJob = false;
                        for (int i = 0; i<currentJob.length; i++) {
                            if (searchPreferences[i] != null && searchPreferences[i].equals(currentJob[i])) {
                                saveJob = true;
                            }
                        }

                        //if we want to save the job then we will can get the location and then output this job.
                        if (saveJob) {

                            //I need to implement a algorithm to shift the ones I want up I think
                            viewJobAdapter.onBindViewHolder(viewHolders.get(currPosition),currPosition, job);

                            currPosition++;

                        }

                    }
                    //end of citation

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error:", error.getDetails());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        viewJobAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewJobAdapter.startListening();
    }


}
