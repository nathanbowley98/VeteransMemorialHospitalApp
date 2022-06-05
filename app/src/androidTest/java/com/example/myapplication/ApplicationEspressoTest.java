package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;


@RunWith(AndroidJUnit4.class)
public class ApplicationEspressoTest {

    public static final String TEST_ID = "123";
    // Getting an instance of the firebase realtime database
    public static final DatabaseReference dbRef = FirebaseUtils.connectFirebase().getReference().child("applications");

    @Rule
    public ActivityScenarioRule myRule = new ActivityScenarioRule<>(ViewApplications.class);


    @BeforeClass
    public static void setup() {
        Session.startSession(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Session.login("test@dal.ca", TEST_ID, "Employer");
    }

    public static void pushApplication() {
        Application app = new Application("test@dal.ca", false, false, "123");

        // Getting an instance of the firebase realtime database
        DatabaseReference dbRef = FirebaseUtils.connectFirebase().getReference().child("applications");

        dbRef.child(TEST_ID).push().setValue(app);
    }

//    @After
//    public void clearApplications() {
//        // Getting an instance of the firebase realtime database
//        DatabaseReference dbRef = FirebaseUtils.connectFirebase().getReference().child("applications");
//
//        dbRef.child(TEST_ID).setValue(null);
//    }

    @Test
    public void applicationDisplayed() {
        pushApplication();
        onView(withId(R.id.jobApplicationsRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void acceptApplication() {
        pushApplication();
//        onView(withId(R.id.jobApplicationsRecyclerView))
//                .perform(RecyclerViewActions.actionOnItem(
//                        hasDescendant(withText("ACCEPT")), click()));
        onView(withId(R.id.jobApplicationsRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

}
