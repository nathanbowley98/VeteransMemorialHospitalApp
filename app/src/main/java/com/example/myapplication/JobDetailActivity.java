package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

public class JobDetailActivity extends AppCompatActivity {

    private Job jobs;
    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvCom;
    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        jobs = (Job)getIntent().getSerializableExtra("job");
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
        tvCom = findViewById(R.id.tvCom);
        tvEmail = findViewById(R.id.tvEmail);
        tvTitle.setText(jobs.getJobTitle());
        tvDesc.setText(jobs.getDescription());
        tvCom.setText("Compensation:"+jobs.getCompensation());
        tvEmail.setText("EmployerEmail:"+jobs.getEmployerEmail());
    }
}