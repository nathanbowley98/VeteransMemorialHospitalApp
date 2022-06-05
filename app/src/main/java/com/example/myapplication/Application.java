package com.example.myapplication;

public class Application {
    private String employeeEmail;
    private boolean accepted;
    private boolean seen;
    private String description;
    private boolean paid;
    private String employerEmail;
    private String jobID;


    public Application(){}

    public Application(String employeeEmail, boolean accepted, Boolean seen, String description) {
        this.employeeEmail = employeeEmail;
        this.seen = seen;
        this.accepted = accepted;
        this.description = description;
        this.paid = false;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }

    public void setEmployerEmail(String employerEmail) {
        this.employerEmail = employerEmail;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getEmployeeEmail(){
        return employeeEmail;
    }
    public void setEmployeeEmail(String email){employeeEmail = email;}

    public Boolean getSeen(){
        return seen;
    }

    public void setSeen(Boolean seen){this.seen = seen;}

    public Boolean getAccepted(){
        return accepted;
    }
    public void setAccepted(Boolean accepted){this.accepted = accepted;}

    public String getDescription(){
        return description;
    }
    public void setDescription(String description){this.description = description;}

}
