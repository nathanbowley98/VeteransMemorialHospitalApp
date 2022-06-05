package com.example.myapplication;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class JobRadiusEspressoTest {

    @Rule
    public ActivityScenarioRule rule  = new ActivityScenarioRule<>(RegisterUser.class);

    @BeforeClass
    public static void createEmployerAndJob(){
        ActivityScenario.launch(RegisterUser.class);
        onView(withId(R.id.registerFirstName)).perform(typeText("EmployerFirstName"));
        onView(withId(R.id.registerLastName)).perform(typeText("EmployerLastName"));
        onView(withId(R.id.registerEmail)).perform(typeText("employer@dal.ca"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerPasswordET)).perform(typeText("employerpassword"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerUserSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employer"))).perform(click());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerButton)).perform(click());

        ActivityScenario.launch(EmployerActivity.class);
        onView(withId(R.id.employerView)).check(matches(isDisplayed()));
        onView(withId(R.id.createJob)).perform(click());

        ActivityScenario.launch(CreateJob.class);
        onView(withId(R.id.createJob)).check(matches(isDisplayed()));

        onView(withId(R.id.jobTitle)).perform(typeText("JOB IN RANGE"));
        onView(withId(R.id.description)).perform(typeText("JOB TITLE DESC"));
        onView(withId(R.id.hourlyRate)).perform(typeText("10"));
        onView(withId(R.id.employer_email)).perform(typeText("employer@dal.ca"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.longitude)).perform(typeText("-122.0800000"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.lat)).perform(typeText("37.4219983"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.submitJobButton)).perform(click());

        ActivityScenario.launch(CreateJob.class);
        onView(withId(R.id.createJob)).check(matches(isDisplayed()));

        onView(withId(R.id.jobTitle)).perform(typeText("JOB OUT OF RANGE"));
        onView(withId(R.id.description)).perform(typeText("JOB TITLE DESC"));
        onView(withId(R.id.hourlyRate)).perform(typeText("15"));
        onView(withId(R.id.employer_email)).perform(typeText("employer@dal.ca"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.longitude)).perform(typeText("-108.85610003"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.lat)).perform(typeText("49.19389814"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.submitJobButton)).perform(click());
    }

    /**
     * US11-AT1
     *
     * Given the user is in a location, when the user searches for jobs,
     * they should only see jobs within some radius
     */
    @Test
    public void testUserGetsJobsWithinRadius()
    {
        ActivityScenario.launch(RegisterUser.class);
        onView(withId(R.id.registerFirstName)).perform(typeText("EmpFirstNameOne"));
        onView(withId(R.id.registerLastName)).perform(typeText("EmpLastNameOne"));
        onView(withId(R.id.registerEmail)).perform(typeText("employee1@dal.ca"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerPasswordET)).perform(typeText("employeepassword1"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerUserSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.jobs)).perform(click());

        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));

    }

    /**
     * US11-AT2
     *
     * Given the user is in a location, when the user searches for jobs,
     * the jobs that are outside the radius should not appear
     */
    @Test
    public void testUserDoesNotGetJobOutsideRadius()
    {
        onView(withId(R.id.registerFirstName)).perform(typeText("EmpFirstNameTwo"));
        onView(withId(R.id.registerLastName)).perform(typeText("EmpLastNameTwo"));
        onView(withId(R.id.registerEmail)).perform(typeText("employee2@dal.ca"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerPasswordET)).perform(typeText("employeepassword2"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerUserSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.jobs)).perform(click());

        onView(withId(R.id.recyclerview)).check(matches(isDisplayed()));
    }
}
