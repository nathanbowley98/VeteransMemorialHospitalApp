package com.example.myapplication;

import static org.junit.Assert.assertEquals;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HistoryTest {
    private static String TEST_ID = "5184204";
    @Rule
    public ActivityScenarioRule myRule =
            new ActivityScenarioRule(MyJobsActivity.class);
    @BeforeClass
    public static void setup() {
        Session.startSession(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Session.login("td@dal.ca", TEST_ID, "Employer");

    }
    @Test
    public void getJobs(){

        ActivityScenario<MyJobsActivity> data = ActivityScenario.launch(MyJobsActivity.class);
        data.moveToState(Lifecycle.State.CREATED);
        assertEquals(true,MyJobsActivity.isGetData);

    }
}
