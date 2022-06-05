package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertFalse;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setUpSession() {
        Session.startSession(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @Before
    public void logout() {
        Session.logout();
    }

    /**
     * US5-AT1: Given that the user is not logged in, when the user closes their application, and
     * reopens it then the user should still need to login.
     *
     */
    @Test
    public void requireLoginWhenAppReopen() {
        rule.getScenario().close();

        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.mainActivity)).check(matches(isDisplayed()));
    }


    /**
     * US5-AT2-Employee: Given that the user is not logged in, when the user closes their application, and
     * reopens it then the user should be sent to the MainActivty page where
     * they can register or login.
     *
     */
    @Test
    public void reopenToMainActivityWhenLoggedOutEmployee() {
        rule.getScenario().close();

        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.mainActivity)).check(matches(isDisplayed()));
    }

    /**
     * US5-AT2-Employer: Given that the user is not logged in, when the user closes their application, and
     * reopens it then the user should be sent to the MainActivty page where
     * they can register or login.
     *
     */
    @Test
    public void reopenToMainActivityWhenLoggedOutEmployer() {
        Session.login("test@dal.ca", "123", "Employer");
        Session.logout();
        rule.getScenario().close();

        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.mainActivity)).check(matches(isDisplayed()));
    }

    /**
     * US5-AT3: Given that the user is an Employer and logged in, when the user closes
     * their application, and reopens it then the user should be sent to the EmployerActivity
     * page.
     *
     */
    @Test
    public void reopenAsEmployerWhenLoggedIn() {
        Session.login("test@dal.ca", "123", "Employer");
        rule.getScenario().close();
        ActivityScenario.launch(MainActivity.class);

        //Verify the app has opened to EmployerActivity instead of MainActivity
        onView(withId(R.id.employerView)).check(matches(isDisplayed()));
    }

    /**
     * US5-AT4: Given that the user is an Employee and logged in, when the user closes
     * their application, and reopens it then the user should be sent to the EmployeeActivity
     * page.
     *
     */
    @Test
    public void reopenAsEmployeeWhenLoggedIn() {
        Session.login("test@dal.ca", "123", "Employee");
        rule.getScenario().close();
        ActivityScenario.launch(MainActivity.class);

        //Verify the app has opened to EmployeeActivity instead of MainActivity
        onView(withId(R.id.employeeView)).check(matches(isDisplayed()));
    }
}
