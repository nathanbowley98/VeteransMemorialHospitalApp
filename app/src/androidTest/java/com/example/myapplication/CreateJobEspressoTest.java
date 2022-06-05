package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class CreateJobEspressoTest {

    private static final FirebaseDatabase firebaseDB = FirebaseUtils.connectFirebase();
    private static final DatabaseReference jobsRef = firebaseDB.getReference().child(FirebaseUtils.JOBS_COLLECTION);
    private static final String TEST_ID = "123";
    private static Boolean is_present = false;



    @Rule
    public ActivityScenarioRule myRule = new ActivityScenarioRule<>(CreateJob.class);


    @Before
    public void clearNode() {
        jobsRef.child(TEST_ID).setValue(null);
    }

    @BeforeClass
    public static void createSession() {
        Session.startSession(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Session.login("test@dal.ca", TEST_ID, "Employer");
    }

    @BeforeClass
    public static void setup() {
        Intents.init();
    }

    @AfterClass
    public static void tearDown() {
        Intents.release();
    }

    @Test
    public void createJob(){
        onView(withId(R.id.createJobTitle)).perform(typeText("Car Wash"));
        onView(withId(R.id.createJobDescription)).perform(typeText("Make my Hellcat shine"));
        onView(withId(R.id.createJobHourlyRate)).perform(typeText("25"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.createJobSubmitButton)).perform(click());
        is_present = false;
        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Job job = data.getValue(Job.class);
                    if (job.getHash().toString().equals(TEST_ID)){
                        is_present = true;
                    }
                }
                assertEquals(true, is_present);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assertFalse(true);
            }
        });
    }

    @Test
    public void invalidWage() {
        onView(withId(R.id.createJobTitle)).perform(typeText("Car Wash"));
        onView(withId(R.id.createJobDescription)).perform(typeText("Make my Hellcat shine"));
        onView(withId(R.id.createJobHourlyRate)).perform(typeText(""));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.createJobSubmitButton)).perform(click());

        jobsRef.child(TEST_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assertEquals(snapshot.getChildrenCount(), 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assertFalse(true);
            }
        });
    }

    @Test
    public void invalidTitle() {
        onView(withId(R.id.createJobTitle)).perform(typeText(""));
        onView(withId(R.id.createJobDescription)).perform(typeText("Make my Hellcat shine"));
        onView(withId(R.id.createJobHourlyRate)).perform(typeText("25"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.createJobSubmitButton)).perform(click());

        jobsRef.child(TEST_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assertEquals(snapshot.getChildrenCount(), 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assertFalse(true);
            }
        });
    }

    @Test
    public void invalidDesc() {
        onView(withId(R.id.createJobTitle)).perform(typeText("Title"));
        onView(withId(R.id.createJobDescription)).perform(typeText(""));
        onView(withId(R.id.createJobHourlyRate)).perform(typeText("25"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.createJobSubmitButton)).perform(click());

        jobsRef.child(TEST_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                assertEquals(0, snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assertFalse(true);
            }
        });
    }

    // Checks that user is redirected when all fields are filled
    @Test
    public void allFieldsFilled(){
        onView(withId(R.id.createJobTitle)).perform(typeText("Working and stuff."));
        onView(withId(R.id.createJobDescription)).perform(typeText("Make my Hellcat shine"));
        onView(withId(R.id.createJobHourlyRate)).perform(typeText("25"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.createJobSubmitButton)).perform(click());
        is_present = false;
        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Job job = data.getValue(Job.class);
                    Log.d("demo",job.getHash());
                    Log.d("demo2",TEST_ID);
                    if (job.getHash().equals(TEST_ID)){
                        is_present = true;
                    }
                }
                assertEquals(true, is_present);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                assertFalse(true);
            }
        });
    }

    // Checks that user is not redirected when all fields are not filled
    @Test
    public void notAllFieldsFilled(){
        onView(withId(R.id.createJobTitle)).perform(typeText(""));
        onView(withId(R.id.createJobDescription)).perform(typeText("Make my Hellcat shine"));
        onView(withId(R.id.createJobHourlyRate)).perform(typeText("25"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.createJobSubmitButton)).perform(click());
        onView(withId(R.id.createJob)).check(matches(isDisplayed()));
    }

}
