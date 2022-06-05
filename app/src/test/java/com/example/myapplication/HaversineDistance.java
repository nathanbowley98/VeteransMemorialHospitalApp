package com.example.myapplication;

import android.text.TextUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HaversineDistance {

    //this class will test the HaversineDistance method in the Location class.

    @BeforeClass
    public static void setUpBeforeClass() {
        System.out.println("Set up before class");
    }

    @Before
    public void setUp() throws Exception {
        System.out.println("Set up");
    }

    @Test
    public void haversineDistanceLocationOppositePoles() {
        Location locationA = new Location(-90, -180);
        Location locationB = new Location(90, 180);

        double distance = locationA.getHaversineDistance(locationB);
        double earthCircumference = 40075;

        Assert.assertTrue("different distances than expected", distance <= (earthCircumference / 2));
    }

    @Test
    public void haversineDistanceLocationSameSpot() {
        Location locationA = new Location(90, 180);
        Location locationB = new Location(90, 180);

        double distance = locationA.getHaversineDistance(locationB);

        Assert.assertTrue("different distances than expected", distance == 0);
    }


    @Test
    public void withinDistanceReturnsFalse() {

        double desiredDistance = 0;
        double distance = 50;

        boolean result = Location.withinDistance(desiredDistance, distance);

        Assert.assertFalse("withinDistance should have returned false", result);
    }

    @Test
    public void withinDistanceReturnsTrue() {
        double distanceFilter = 60;
        double distance = 50;

        boolean result = Location.withinDistance(distanceFilter, distance);


        Assert.assertTrue("withinDistance should have returned ", result);
    }

    @Test
    public void withinDistanceReturnsDesiredAndDistanceSame() {
        double distanceFilter = 50;
        double distance = 50;

        boolean result = Location.withinDistance(distanceFilter, distance);


        Assert.assertTrue("different distances than expected", result);
    }

    @Test
    public void withinDistanceWithNegativeDesiredDistance() {

        double distanceFilter = -1;
        double distance = 50;

        boolean result = Location.withinDistance(distanceFilter, distance);

        Assert.assertFalse("withinDistance should have returned false", result);
    }

    @Test
    public void withinDistanceWithNegativeDistance() {

        double distanceFilter = 50;
        double distance = -5;

        boolean result = Location.withinDistance(distanceFilter, distance);

        Assert.assertFalse("withinDistance should have returned false", result);
    }
}
