package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPaymentsActivity extends AppCompatActivity  {
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private RecyclerView recyclerview;
    private PaymentAdapter adapter;
    public List<Payment> list = new ArrayList<>();
    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);
        firebaseDB = FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL);
        firebaseDBRef = firebaseDB.getReference("payments");
         email = Session.getEmail();
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentAdapter(list);
        recyclerview.setAdapter(adapter);
        getJobs();
    }


    private void getJobs() {
        firebaseDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Payment payment = snapshot.getValue(Payment.class);
                    if (payment.getEmployer().equals(email)){
                        list.add(payment);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}
