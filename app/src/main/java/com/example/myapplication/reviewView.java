package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class reviewView extends AppCompatActivity {

    private EditText inputReviewNumber;
    private Button submitReview;

    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;

    private RecyclerView recyclerView;
    viewColleagueAdapter adapter; // Create Object of the Adapter class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_view);

        submitReview = (Button) findViewById(R.id.submitReview);
        inputReviewNumber = (EditText) findViewById(R.id.inputReviewNumber);

        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Session.getReviewingUserEmail();
                firebaseDB = FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL);
                firebaseDBRef = firebaseDB.getReference("users");
                final User[] um = new User[1];
                final String[] key = new String[1];
                firebaseDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot datasnapshot: snapshot.getChildren()){
                            um[0] = datasnapshot.getValue(User.class);
                            key[0] = datasnapshot.getKey();
                            Log.d("loop",um[0].getEmail());
                            if (um[0].getEmail().equalsIgnoreCase(email)){
                                um[0].addReview(Double.valueOf(inputReviewNumber.getText().toString()));
                                break;
                            }
                        }
                        firebaseDBRef.child(key[0]).setValue(um[0]);
                        if (Session.getUserType().equalsIgnoreCase("employer")) {
                            Intent intent = new Intent(reviewView.this, VeteranActivity.class);
                            startActivity(intent);
                        }
                        else if (Session.getUserType().equalsIgnoreCase("employee")){
                            Intent intent = new Intent(reviewView.this, VolunteerActivity.class);
                            startActivity(intent);
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
