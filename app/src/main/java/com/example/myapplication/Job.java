package com.example.myapplication;



import android.app.AppComponentFactory;

import java.io.Serializable;

public class Job implements Serializable {

    private String employerEmail;
    private String jobTitle;
    private String description;
    private String userHash;
    private double compensation = 0;
    private String category;
    private Location location;

    /**
     * REQUIRED FOR JOBSEARCH TO WORK. No args constructor
     */
    public Job() {
        this.location = new Location(0,0);
    }


    /**
     * Default constructor for AppCompatActivity. All Activities must have a default constructor
     * for API 27 and lower devices or when using the default
     * {@link AppComponentFactory}.
     */
    public Job(String employerEmail, String jobTitle, String description, Location location, String userHash, String category) {
        this.employerEmail = employerEmail;
        this.jobTitle = jobTitle;
        this.description = description;
        this.location = location;
        this.userHash = userHash;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }

    public void setEmployerEmail(String employerEmail) {
        this.employerEmail = employerEmail;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHash() {
        return userHash;
    }

    public void setHash(String hash) {
        this.userHash = hash;
    }

    public double getCompensation() {
        return compensation;
    }

    public void setCompensation(double compensation) {
        this.compensation = compensation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
