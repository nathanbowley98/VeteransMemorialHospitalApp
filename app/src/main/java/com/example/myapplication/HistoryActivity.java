package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvEmail;
    private View jobs;
    private View history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        String uid = Session.getUserID();
        String email = Session.getEmail();
        String name = Session.getFName();
        String lastName = Session.getLName();
        history = findViewById(R.id.history);
        jobs = findViewById(R.id.jobs);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tv_email);
        tvName.setText(name+" "+ lastName);
        tvEmail.setText(email);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HistoryActivity.this,MyPaymentsActivity.class));
            }
        });
        jobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HistoryActivity.this,MyJobsActivity.class));
            }
        });
    }
}