package com.example.myapplication;

import java.io.Serializable;

/**
 * Location class that stores the lat and long values of a Job, originally part of Job class and
 * programmed by John Corsten, but Nathanael refactored into this Location class.
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
public class Location implements Serializable {

    // Logitude and latitude representing the location of the job
    private double latitude;
    private double longitude;

    /**
     * REQUIRED FOR JOBSEARCH - no args constructor
     */
    public Location() {

    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    //single responsibility principle violation?
    /**
     * getHaversineDistance method that uses another location object to calculate the well known and
     * well published about haversine distance equation from James Andrew in the year 1805. citation
     * for reference https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
     * @param otherLocation Location: the location object that is to be compared
     * @return the distance in kilometres to be returned.
     */
    public double getHaversineDistance(Location otherLocation) {
        double otherLatitude = otherLocation.getLatitude();
        double otherLongitude = otherLocation.getLongitude();

        double differenceInLat = Math.toRadians(otherLatitude) - Math.toRadians(latitude);
        double differenceInLong = Math.toRadians(otherLongitude) - Math.toRadians(longitude);

        double latitudeRad = Math.toRadians(latitude);
        double otherLatitudeRad = Math.toRadians(otherLatitude);

        double aValue = Math.pow(Math.sin(differenceInLat / 2), 2) +
                Math.pow(Math.sin(differenceInLong / 2), 2) * Math.cos(latitudeRad) * Math.cos(otherLatitudeRad);
        double cValue = 2 * Math.atan2(Math.sqrt(aValue), Math.sqrt(1-aValue));

        //radius according to NASA citation: https://nssdc.gsfc.nasa.gov/planetary/factsheet/earthfact.html
        double radiusOfEarth = 6371.0088;
        //end of citation

        return radiusOfEarth * cValue;

    }

    public static boolean withinDistance(double distanceFilter, double distance) {
        return distance <= distanceFilter && distanceFilter >= 0 && distance >= 0;
    }
}
