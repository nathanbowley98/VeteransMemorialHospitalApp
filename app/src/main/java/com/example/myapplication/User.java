package com.example.myapplication;

public class User {
    double average_rating = 5.0;
    String email;
    String firstName;
    String lastName;
    String hash;

    Boolean loginState;
    int num_reviews;
    String password;
    String longitude;
    String latitude;
    String userType;

    public void addReview(double review){
        if (review > 5 || review < 1){
            return;
        }
        average_rating = ((average_rating * num_reviews) + review)/(num_reviews+1);
        num_reviews++;
    }


    public void setAverage_rating(double average_rating) {
        this.average_rating = average_rating;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setLoginState(Boolean loginState) {
        this.loginState = loginState;
    }

    public void setNum_reviews(int num_reviews) {
        this.num_reviews = num_reviews;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public double getAverage_rating() {
        return average_rating;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getHash() {
        return hash;
    }

    public Boolean getLoginState() {
        return loginState;
    }

    public int getNum_reviews() {
        return num_reviews;
    }

    public String getPassword() {
        return password;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getUserType() {
        return userType;
    }




}
