package com.example.myapplication;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ReviewEspressoTest {
    // John's ID
    public static final String TEST_ID1 = "123";
    // Nhoj's ID
    public static final String TEST_ID2 = "321";

    // Getting an instance of the firebase realtime database
    public static final DatabaseReference col_ref = FirebaseUtils.connectFirebase().getReference().child("colleagues");

    @Rule
    public ActivityScenarioRule myRule = new ActivityScenarioRule<>(reviewView.class);

    @BeforeClass
    public static void setup() {
        Session.startSession(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Colleague col1 = new Colleague("john@dal.ca","John Corsten");
        Colleague col2 = new Colleague("nhoj@dal.ca","Nohj Netsroc");
        // Giving John and Nhoj each other as colleagues
        col_ref.child(TEST_ID1).child("test1").setValue(col2);
        col_ref.child(TEST_ID2).child("test2").setValue(col1);
    }

    @AfterClass
    public static void tearDown() {
        //Intents.release();
        FirebaseDatabase.getInstance().getReference("colleagues").child("123").setValue(null);
        FirebaseDatabase.getInstance().getReference("colleagues").child("321").setValue(null);
    }

    // Checks that the other colleague is present in the options list for the employer
    @Test
    public void searchForEmployeeColleague(){

        Session.startSession(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Session.login("john@dal.ca", TEST_ID1, "Employer");
        ActivityScenario.launch(EmployerActivity.class);
        onView(withId(R.id.makeAReview_employer)).perform(click());
        assertEquals(1,viewColleagueAdapter.getLength()-1);
    }

    // Checks that the other colleague is present in the options list for the employer
    @Test
    public void searchForEmployerColleague(){

        Session.startSession(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Session.login("nhoj@dal.ca", TEST_ID2, "Employee");
        ActivityScenario.launch(EmployeeActivity.class);
        onView(withId(R.id.makeAReview_employee)).perform(click());
        assertEquals(1,viewColleagueAdapter.getLength()-1);
    }


}
