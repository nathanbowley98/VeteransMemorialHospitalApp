package com.example.myapplication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import android.text.TextUtils;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class CreateJobTest {

    @AfterClass
    public static void tearDown() {
        System.gc();
    }

    @Test
    public void validDesc() {
        assertTrue(CreateJob.validateJobDescription("This is a job description!"));
    }

    @Test
    public void invalidDesc() {
        assertFalse(CreateJob.validateJobDescription(""));
    }

    @Test
    public void validTitle() {
        assertTrue(CreateJob.validateTitle("A valid title!"));
    }

    @Test
    public void invalidTitle() {
        assertFalse(CreateJob.validateTitle(""));
    }

    @Test
    public void validWage() {
        assertTrue(CreateJob.validateWage("12345"));
    }

    @Test
    public void invalidWage() {
        assertFalse(CreateJob.validateWage("-5"));
    }

    @Test
    public void invalidLongLat() {
        assertFalse(CreateJob.validateLongLat(180.123));
        assertFalse(CreateJob.validateLongLat(-190.0));
    }

    @Test
    public void validLongLat() {
        assertTrue(CreateJob.validateLongLat(98.09));
        assertTrue(CreateJob.validateLongLat(-170.98));
    }

}
