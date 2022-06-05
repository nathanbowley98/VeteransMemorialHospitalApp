package com.example.myapplication;

/**
 * Employee class that will be used for refactoring code to reduce coupling in iteration 2 of
 * the Winter 2022 CSCI3130 group project.
 * @authors: Nathanael Bowley,
 *          John Corsten,
 *          Nathan Horne,
 *          Ted Graveson,
 *          Hongzheng Ding,
 *          Tianhao Jia,
 *          Saher Anwar Ziauddin
 * @course: CSCI3130 @ Dalhousie University.
 * @semester: Winter 2022
 * @group: Group 4
 * @clientTA: Disha Malik
 */
public class Employee {

    private String firstName;
    private String lastName;
    private double doubleRating;
    private String rating = "Unknown";
    private String email;
    public static String EMPLOYEE = "Employee";
    private double distance;

    public Employee(String firstName, String lastName, String rating, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.rating = rating;
        try {
            this.doubleRating = Double.parseDouble(rating);
        } catch (NumberFormatException e) {
            this.doubleRating = -1;
        }
        this.email = email;
        this.distance = Double.MAX_VALUE;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDoubleRating() {
        return doubleRating;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
