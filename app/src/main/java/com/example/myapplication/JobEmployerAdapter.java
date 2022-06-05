package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JobEmployerAdapter extends RecyclerView.Adapter<JobEmployerAdapter.ViewHolder> {
    List<Job> list;

    Object selectedItem;
    Context context;
    public JobEmployerAdapter(List<Job> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.job_employer_layout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Job job = list.get(position);
        Location location = job.getLocation();
        Log.d("testing:", location.getLatitude() + " " + location.getLongitude());
        holder.jobTitle.setText(job.getJobTitle());
        holder.jobEmail.setText(job.getEmployerEmail());
        holder.jobDesc.setText(job.getDescription());
        holder.jobHourlyRate.setText(String.valueOf(job.getCompensation()));
        holder.jobLatitude.setText("latitude: " + location.getLatitude());
        holder.jobLongitude.setText("longitude: " + location.getLongitude());

        //TODO set up the buttons to do stuff and the drop down, its not done here but below in ViewHolder class
//        holder.jobSeeApplications.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ViewApplications.class);
//                startActivity(intent);
//            }
//        });
        //holder.jobSuggestionFilter
        //holder.jobSuggestionButton
        //holder needs the rest of the spots filled in.
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView jobTitle;
        TextView jobEmail;
        TextView jobDesc;
        TextView jobHourlyRate;
        TextView jobLatitude;
        TextView jobLongitude;
        Button jobSeeApplications;
        Spinner jobSuggestionFilter;
        Button jobSuggestionButton;


        List<Employee> list;
        List<Employee> listDistance;

        FirebaseDatabase firebaseDB;
        DatabaseReference firebaseDBRef;
        RecyclerView recyclerViewDistance;
        RecyclerView recyclerViewRating;
        JobEmployeeAdapter adapter;
        JobEmployeeAdapter adapterDistance;

        SeekBar distanceSeekBar;
        TextView distanceTextView;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobEmployerLayoutJobTitle);
            jobEmail = itemView.findViewById(R.id.jobEmployerLayoutEmployerEmail);
            jobDesc = itemView.findViewById(R.id.jobEmployerLayoutDescription);
            jobHourlyRate = itemView.findViewById(R.id.jobEmployerLayoutHourlyRate);
            jobLatitude = itemView.findViewById(R.id.jobEmployerLayoutLatitude);
            jobLongitude = itemView.findViewById(R.id.jobEmployerLayoutLongitude);
            jobSeeApplications = itemView.findViewById(R.id.jobEmployerLayoutCurrentAppsButton);
            jobSuggestionFilter = (Spinner) itemView.findViewById(R.id.jobEmployerLayoutSpinner);
            jobSuggestionButton = itemView.findViewById(R.id.jobEmployerLayoutSuggestButton);


            firebaseDB = FirebaseUtils.connectFirebase();
            firebaseDBRef = firebaseDB.getReference(FirebaseUtils.USERS_COLLECTION);


            listDistance = new ArrayList<>();
            recyclerViewDistance = itemView.findViewById(R.id.employeeJobsRecyclerViewDistance);
            recyclerViewDistance.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            adapterDistance = new JobEmployeeAdapter(listDistance);
            recyclerViewDistance.setAdapter(adapterDistance);
            //recyclerViewDistance.setVisibility(View.GONE);


            list = new ArrayList<>();
            recyclerViewRating = itemView.findViewById(R.id.employeeJobsRecyclerViewRating);
            recyclerViewRating.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            adapter = new JobEmployeeAdapter(list);
            recyclerViewRating.setAdapter(adapter);
            //recyclerViewRating.setVisibility(View.GONE);

            distanceSeekBar = itemView.findViewById(R.id.jobEmployerLayoutSeekBar);
            distanceTextView = itemView.findViewById(R.id.jobEmployerLayoutSeekBarTextView);




            jobSeeApplications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.getContext().startActivity(new Intent(view.getContext(), ViewApplications.class));
                }
            });

            String[] spinnerArray = new String[] {
                    "Rating",
                    "Distance",
                    "Search"
            };

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(itemView.getContext(),
                    android.R.layout.simple_spinner_dropdown_item, spinnerArray);

            jobSuggestionFilter.setAdapter(spinnerAdapter);

            jobSuggestionFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    selectedItem = adapterView.getItemAtPosition(position);
                    Toast.makeText(itemView.getContext(),selectedItem.toString() + " Selected", Toast.LENGTH_SHORT).show();

                    if (selectedItem.equals("Search")) {
                        recyclerViewDistance.setVisibility(View.GONE);
                        recyclerViewRating.setVisibility(View.GONE);
                        distanceSeekBar.setVisibility(View.GONE);
                        distanceTextView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    selectedItem = "No Item Selected";

                }
            });

            distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    distanceTextView.setText(String.valueOf("Distance: " + i + " km"));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            jobSuggestionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(view.getContext(), selectedItem.toString(), Toast.LENGTH_SHORT).show();

                    if (selectedItem == null) {
                        Toast.makeText(itemView.getContext(), "No selected Item", Toast.LENGTH_SHORT).show();
                    }
                    else if (selectedItem.equals("Rating")) {
                        //Toast.makeText(itemView.getContext(), "Rating Selected", Toast.LENGTH_SHORT).show();

                        recyclerViewDistance.setVisibility(View.GONE);
                        recyclerViewRating.setVisibility(View.VISIBLE);

                        distanceSeekBar.setVisibility(View.GONE);
                        distanceTextView.setVisibility(View.GONE);

                        firebaseDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                list.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    //snapshot.getValue();

                                    String firstName = snapshot.child("firstName").getValue(String.class);
                                    String lastName = snapshot.child("lastName").getValue(String.class);
                                    String rating = String.valueOf(snapshot.child("average_rating").getValue(Double.class));
                                    if (rating == null) {
                                        rating = "-1";
                                    }
                                    String email = snapshot.child("email").getValue(String.class);

                                    //check the location and use the locaiton method from Location class.
                                    //snapshot.child("")

                                    Employee employee = new Employee(firstName, lastName, rating, email);
                                    if (!employee.getEmail().equals(Session.getEmail())) {

                                        list.add(employee);
                                        list.sort(Comparator.comparing(Employee::getDoubleRating));
                                    }

                                }


                                //show the highest rated first
                                Collections.reverse(list);






                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                    }
                    else if (selectedItem.equals("Distance")) {
                        //Toast.makeText(itemView.getContext(), "Distance Selected", Toast.LENGTH_SHORT).show();

                        String locationLat = jobLatitude.getText().toString();
                        String locationLong = jobLongitude.getText().toString();

                        String latitudeStr = locationLat.split(" ")[1];
                        String longitudeStr = locationLong.split(" ")[1];

                        //Log.d("testing1: ",  + " test.");

                        double latitude = Double.parseDouble(latitudeStr);
                        double longitude = Double.parseDouble(longitudeStr);

                        Location location = new Location(latitude, longitude);

                        recyclerViewDistance.setVisibility(View.VISIBLE);
                        recyclerViewRating.setVisibility(View.GONE);

                        distanceSeekBar.setVisibility(View.VISIBLE);
                        distanceTextView.setVisibility(View.VISIBLE);


                        firebaseDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                listDistance.clear();

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    //snapshot.getValue();

                                    String firstName = snapshot.child("firstName").getValue(String.class);
                                    String lastName = snapshot.child("lastName").getValue(String.class);
                                    String rating = String.valueOf(snapshot.child("average_rating").getValue(Double.class));
                                    String email = snapshot.child("email").getValue(String.class);


                                    if (rating == null) {
                                        rating = "Unknown Rating";
                                    }

                                    //check the location and use the locaiton method from Location class.
                                    double latitude = 0, longitude = 0;
                                    if (snapshot.hasChild("user location (latitude)")) {
                                        latitude = snapshot.child("user location (latitude)").getValue(Double.class);

                                    }
                                    if (snapshot.hasChild("user location (longitude)")) {
                                        longitude = snapshot.child("user location (longitude)").getValue(Double.class);

                                    }
                                    Location employeeLocation = new Location(latitude, longitude);

                                    Employee employee = new Employee(firstName, lastName, rating, email);
                                    if (!employee.getEmail().equals(Session.getEmail())) {

                                        //todo figure out why this bug happens where the list is empty
                                        //i may need to implement a seperate list for distance.

                                        int desiredDistance = distanceSeekBar.getProgress();

                                        double distanceToEmployee = location.getHaversineDistance(employeeLocation);
                                        if (location.withinDistance(desiredDistance, distanceToEmployee)) {

                                            //setting the number of significant digits
                                            distanceToEmployee = Math.round(distanceToEmployee * 100.0) / 100.0;

                                            
                                            employee.setDistance(distanceToEmployee);
                                            listDistance.add(employee);
                                        }
                                        //list.add(employee);
                                    }

                                }

                                adapterDistance.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //FirebaseDatabase firebaseDB = FirebaseUtils.connectFirebase();
                        //DatabaseReference firebaseDBRef = firebaseDB.getReference(FirebaseUtils.USERS_COLLECTION);

                        //have a doubly nested drop down recylcer view.







                        //need to add functionality for checking against each person

//                        double latOn = 49.64476;
//                        double longOn = -83.56784;
//                        Location locationOther = new Location(latOn, longOn);
//                        Log.d("testing: ", location.getHaversineDistance(locationOther) + " ");





                    }
                    else if (selectedItem.equals("Search")) {
                        //Toast.makeText(itemView.getContext(), "Search Selected", Toast.LENGTH_SHORT).show();

                        recyclerViewDistance.setVisibility(View.GONE);
                        recyclerViewRating.setVisibility(View.GONE);

                        Intent intent = new Intent(view.getContext(), JobSearch.class);

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        if (editor != null) {
                            editor.putString("searchEmployerEmail", jobEmail.getText().toString()).apply();
                            editor.putString("searchJobTitle", jobTitle.getText().toString()).apply();
                            editor.putString("searchDescription", jobDesc.getText().toString()).apply();
                            editor.putString("searchHourlyRate", jobHourlyRate.getText().toString()).apply();
                            editor.commit();
                        }

                        view.getContext().startActivity(intent);

                    }

                }
            });

        }
    }
}

