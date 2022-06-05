package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
public class JobSearchEspressoTest {
    private static String TEST_ID = "123";

    @Rule
    public ActivityScenarioRule myRule =
            new ActivityScenarioRule(JobSearch.class);

    @BeforeClass
    public static void setup() {
        Session.startSession(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Session.login("test@dal.ca", TEST_ID, "Employer");

        Map<String, Object> job = new HashMap<>();
        job.put("employerEmail", "george.smith@dal.ca");
        job.put("jobTitle", "Title");
        job.put("description", "Car Wash");
        job.put("userHash", TEST_ID);
        job.put("compensation", 1);

        // Getting an instance of the firebase realtime database
        DatabaseReference dbRef = FirebaseUtils.connectFirebase().getReference().child(FirebaseUtils.JOBS_COLLECTION);

        dbRef.child(TEST_ID).setValue(job);
    }

    @AfterClass
    public static void tearDown() {
        Intents.release();
        FirebaseDatabase.getInstance().getReference("jobs").setValue(null);
        FirebaseDatabase.getInstance().getReference("users").setValue(null);
    }

//    @Test
//    public void searchNoJobsExist(){
//
//        ActivityScenario.launch(EmployerActivity.class);
//
//        onView(withId(R.id.employerSearchButton)).perform(click());
//
//        onView(withId(R.id.searchJobButton)).perform(click());
//
//        assertEquals(0,ViewJobAdapter.getHolderArrayList().size());
//
//
//    }

    @Test
    public void searchJobsExistNoJobsInfoProvided(){

        ActivityScenario.launch(EmployerActivity.class);

        onView(withId(R.id.employerSearchButton)).perform(click());

        onView(withId(R.id.searchJobButton)).perform(click());

        assertEquals(1,ViewJobAdapter.getHolderArrayList().size());

    }

    @Test
    public void searchJobsExistJobsInfoProvidedEmail(){

        Map<String, Object> job = new HashMap<>();
        job.put("employerEmail", "george.smith@dal.ca");
        job.put("jobTitle", "Title");
        job.put("description", "Stuff");
        job.put("userHash", TEST_ID);
        job.put("compensation", 1);

        // Getting an instance of the firebase realtime database
        DatabaseReference dbRef = FirebaseUtils.connectFirebase().getReference().child(FirebaseUtils.JOBS_COLLECTION);

        dbRef.child(TEST_ID).setValue(job);

        ActivityScenario.launch(EmployerActivity.class);

        onView(withId(R.id.employerSearchButton)).perform(click());


        onView(withId(R.id.searchEmployerEmail)).perform(typeText("george.smith@dal.ca"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchJobButton)).perform(click());

        //shows two jobs.
        ArrayList<Job> jobArrayList = ViewJobAdapter.getJobArrayList();
        for (int i = 0; i<jobArrayList.size(); i++) {
            Job job_next = jobArrayList.get(i);
            String currentEmail = job_next.getEmployerEmail();
            assertEquals("george.smith@dal.ca", currentEmail);
        }

    }


    @Test
    public void searchJobsExistJobsInfoProvidedTitle(){
        // Getting an instance of the firebase realtime database
        DatabaseReference dbRef = FirebaseUtils.connectFirebase().getReference().child(FirebaseUtils.JOBS_COLLECTION);


        Map<String, Object> job2 = new HashMap<>();
        job2.put("employerEmail", "george.smith@dal.ca");
        job2.put("jobTitle", "Title");
        job2.put("description", "Car Destroy");
        job2.put("userHash", TEST_ID);
        job2.put("compensation", 1);

        // Getting an instance of the firebase realtime database

        dbRef.child(TEST_ID).setValue(job2);

        ActivityScenario.launch(EmployerActivity.class);

        //for some reason this one causes it to crash but it doesn't in the previous test.
        onView(withId(R.id.employerSearchButton)).perform(click());


        onView(withId(R.id.searchJobTitle)).perform(typeText("Title"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchJobButton)).perform(click());

        //shows two jobs.
        ArrayList<Job> jobArrayList = ViewJobAdapter.getJobArrayList();
        for (int i = 0; i<jobArrayList.size(); i++) {
            Job job_next = jobArrayList.get(i);
            String currentTitle = job_next.getJobTitle();
            assertEquals("Title", currentTitle);
        }

    }

    @Test
    public void searchJobsExistJobsInfoProvidedHourlyRate(){

        // Getting an instance of the firebase realtime database
        DatabaseReference dbRef = FirebaseUtils.connectFirebase().getReference().child(FirebaseUtils.JOBS_COLLECTION);


        Map<String, Object> job2 = new HashMap<>();
        job2.put("employerEmail", "george.smith@dal.ca");
        job2.put("jobTitle", "Title");
        job2.put("description", "Car Destroy");
        job2.put("userHash", TEST_ID);
        job2.put("compensation", 1);

        // Getting an instance of the firebase realtime database

        dbRef.child(TEST_ID).setValue(job2);

        ActivityScenario.launch(EmployerActivity.class);

        //for some reason this one causes it to crash but it doesn't in the previous test.
        onView(withId(R.id.employerSearchButton)).perform(click());

        onView(withId(R.id.searchHourlyRate)).perform(typeText("1.0"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchJobButton)).perform(click());

        //shows two jobs.
        ArrayList<Job> jobArrayList = ViewJobAdapter.getJobArrayList();
        for (int i = 0; i<jobArrayList.size(); i++) {
            Job job = jobArrayList.get(i);
            String currentRate = String.valueOf(job.getCompensation());
            assertEquals("1.0", currentRate);
        }

    }

    @Test
    public void searchJobsExistJobsInfoProvidedDescription(){

        // Getting an instance of the firebase realtime database
        DatabaseReference dbRef = FirebaseUtils.connectFirebase().getReference().child(FirebaseUtils.JOBS_COLLECTION);


        Map<String, Object> job2 = new HashMap<>();
        job2.put("employerEmail", "george.smith@dal.ca");
        job2.put("jobTitle", "Title");
        job2.put("description", "Make my Hellcat shine");
        job2.put("userHash", TEST_ID);
        job2.put("compensation", 1);

        // Getting an instance of the firebase realtime database

        dbRef.child(TEST_ID).setValue(job2);

        ActivityScenario.launch(EmployerActivity.class);

        onView(withId(R.id.employerSearchButton)).perform(click());

        onView(withId(R.id.searchDescription)).perform(typeText("Make my Hellcat shine"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchJobButton)).perform(click());

        //shows two jobs.
        ArrayList<Job> jobArrayList = ViewJobAdapter.getJobArrayList();
        for (int i = 0; i<jobArrayList.size(); i++) {
            Job job = jobArrayList.get(i);
            String currentDesc = job.getDescription();
            assertEquals("Make my Hellcat shine", currentDesc);
        }

    }

    //user story 14 tests below.

    @Test
    public void searchJobsEmailSaved(){

        ActivityScenario.launch(EmployerActivity.class);

        onView(withId(R.id.employerSearchButton)).perform(click());
        onView(withId(R.id.searchEmployerEmail)).perform(typeText("george.smith@dal.ca"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchJobButton)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.employerSearchButton)).perform(click());
        onView(withId(R.id.searchEmployerEmail)).check(matches(withText(containsString("george.smith@dal.ca"))));

    }


    @Test
    public void searchJobsTitleSaved(){

        ActivityScenario.launch(EmployerActivity.class);

        onView(withId(R.id.employerSearchButton)).perform(click());
        onView(withId(R.id.searchJobTitle)).perform(typeText("Title"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchJobButton)).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.employerSearchButton)).perform(click());
        onView(withId(R.id.searchJobTitle)).check(matches(withText(containsString("Title"))));

    }

    @Test
    public void searchJobsHourlyRateSaved(){

        ActivityScenario.launch(EmployerActivity.class);

        onView(withId(R.id.employerSearchButton)).perform(click());
        onView(withId(R.id.searchHourlyRate)).perform(typeText("1"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchJobButton)).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.employerSearchButton)).perform(click());
        onView(withId(R.id.searchHourlyRate)).check(matches(withText(containsString("1.0"))));

    }

    @Test
    public void searchJobsDescriptionSaved(){

        ActivityScenario.launch(EmployerActivity.class);

        onView(withId(R.id.employerSearchButton)).perform(click());
        onView(withId(R.id.searchJobTitle)).perform(typeText("Car Wash"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchJobButton)).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.employerSearchButton)).perform(click());
        onView(withId(R.id.searchJobTitle)).check(matches(withText(containsString("Car Wash"))));


    }

}
