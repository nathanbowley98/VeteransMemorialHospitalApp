package com.example.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// FirebaseRecyclerAdapter is a class provided by
// FirebaseUI. it provides functions to bind, adapt and show
// database contents in a Recycler View
public class applicationAdapter extends FirebaseRecyclerAdapter<
        Application, applicationAdapter.applicationsViewholder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public applicationAdapter(@NonNull FirebaseRecyclerOptions<Application> options) {
        super(options);
    }

    // Function to bind the view in Card view(here
    // "person.xml") iwth data in
    // model class(here "person.class")
    @Override
    protected void
    onBindViewHolder(@NonNull applicationsViewholder holder,
                     int position, @NonNull Application model) {

        // Add firstname from model class (here
        // "person.class")to appropriate view in Card
        // view (here "person.xml")
        holder.employeeEmail.setText(model.getEmployeeEmail());
        holder.description.setText(model.getDescription());
    }

    // Function to tell the class about the Card view (here
    // "person.xml")in
    // which the data will be shown
    @NonNull
    @Override
    public applicationsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_application, parent, false);
        return new applicationAdapter.applicationsViewholder(view);
    }


    // Sub Class to create references of the views in Crad
    // view (here "person.xml")
    class applicationsViewholder extends RecyclerView.ViewHolder {
        TextView employeeEmail;
        TextView description;
        Button accept, ignore;

        public applicationsViewholder(@NonNull View itemView) {
            super(itemView);
            accept = (Button) itemView.findViewById(R.id.acceptApplication);
            ignore = (Button) itemView.findViewById(R.id.ignoreApplication);
            employeeEmail = itemView.findViewById(R.id.jobApplicationEmail);
            description = itemView.findViewById(R.id.appDescription);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase firebaseDB = FirebaseUtils.connectFirebase();
                    DatabaseReference appRef = firebaseDB.getReference().child("applications").child(Session.getUserID());
                    DatabaseReference usersRef = firebaseDB.getReference().child("users");
                    appRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                if (dataSnapshot.child("employeeEmail").getValue().toString().equals(employeeEmail.getText().toString()) && dataSnapshot.child("description").getValue().toString().equals(description.getText().toString())){

                                    Application app = dataSnapshot.getValue(Application.class);
                                    Offer offer = new Offer(Session.getEmail(), false,dataSnapshot.child("description").getValue().toString());
                                    app.setAccepted(true);
                                    app.setEmployerEmail(Session.getEmail());
                                    app.setPaid(false);

                                    FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL)
                                            .getReference()
                                            .child("applications").child(Session.getUserID()).child(dataSnapshot.getKey()).setValue(app);

                                    usersRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot user : snapshot.getChildren()) {
                                                if (user.child("email").getValue().toString().equals(dataSnapshot.child("employeeEmail").getValue().toString())){
                                                    FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL)
                                                            .getReference()
                                                            .child("offers").child(user.getKey()).child(dataSnapshot.getKey()).setValue(offer);
                                                    Colleague employer_col = new Colleague(Session.getEmail(), Session.getFName() + " " + Session.getLName());
                                                    Colleague employee_col = new Colleague(user.child("email").getValue().toString(), user.child("firstName").getValue().toString() + " " + user.child("lastName").getValue().toString());
                                                    FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL)
                                                            .getReference()
                                                            .child("colleagues").child(user.getKey()).child(Session.getUserID()).setValue(employer_col);

                                                    FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL)
                                                            .getReference()
                                                            .child("colleagues").child(Session.getUserID()).child(user.child("hash").getValue().toString()).setValue(employee_col);
                                                }
                                                }
                                            }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            ignore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase firebaseDB = FirebaseUtils.connectFirebase();
                    DatabaseReference appRef = firebaseDB.getReference().child("applications").child(Session.getUserID());
                    appRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                if (dataSnapshot.child("employeeEmail").getValue().toString().equals(employeeEmail.getText().toString()) && dataSnapshot.child("description").getValue().toString().equals(description.getText().toString())){

                                    FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL)
                                            .getReference()
                                            .child("applications").child(Session.getUserID()).child(dataSnapshot.getKey()).setValue(null);
                                }

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }


    }
}