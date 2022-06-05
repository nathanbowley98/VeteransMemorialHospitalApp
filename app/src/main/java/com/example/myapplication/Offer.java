package com.example.myapplication;

public class Offer {
    private String employerEmail;
    private Boolean dismissed;
    private String description;

    public Offer(String employerEmail, Boolean dismissed, String description) {
        this.employerEmail = employerEmail;
        this.dismissed = dismissed;
        this.description = description;

    }

    public String getEmployerEmail(){
        return employerEmail;
    }
    public void setEmployerEmail(String email){employerEmail = email;}

    public Boolean getDismissed(){
        return dismissed;
    }

    public void setDismissed(Boolean dismissed){this.dismissed = dismissed;}

    public String getDescription(){
        return description;
    }
    public void setDescription(String description){this.description = description;}

    public Offer(){};
}
