package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LoginEspressoTest {

    private static final String employerKey = "tempEmployer";
    private static final String employeeKey = "tempEmployee";


    @Rule
    public ActivityScenarioRule<LoginActivity> myRule = new ActivityScenarioRule<>(LoginActivity.class);

    @BeforeClass
    public static void setup() {
        Intents.init();

        // Creating a HashMap of user information to store on firebase
        Map<String, Object> employee = new HashMap<>();
        employee.put("firstName", "Test");
        employee.put("lastName", "Employee");
        employee.put("email", "testEmployee@dal.ca");
        employee.put("userType", "Employee");
        employee.put("password", "1234");
        employee.put("loginState", false);

        Map<String, Object> employer = new HashMap<>();
        employer.put("firstName", "Test");
        employer.put("lastName", "Employer");
        employer.put("email", "testEmployer@dal.ca");
        employer.put("userType", "Employer");
        employer.put("password", "1234");
        employer.put("loginState", false);

        // Getting an instance of the firebase realtime database
        DatabaseReference dbRef = FirebaseUtils.connectFirebase().getReference().child(FirebaseUtils.USERS_COLLECTION);

        dbRef.child(employeeKey).setValue(employee);
        dbRef.child(employerKey).setValue(employer);
        Session.startSession(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }


    @AfterClass
    public static void tearDown() {
        Intents.release();
        FirebaseDatabase.getInstance().getReference(FirebaseUtils.USERS_COLLECTION).child(employerKey).setValue(null);
        FirebaseDatabase.getInstance().getReference(FirebaseUtils.USERS_COLLECTION).child(employeeKey).setValue(null);

    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.myapplication", appContext.getPackageName());
    }
    /***sign up**/

    /***sign up**/
    @Test
    public void testSignUpActivity() {
        onView(withId(R.id.loginToSignupButton)).perform(click());
        intended(hasComponent(RegisterUser.class.getName()));
    }

    /**
     * US1-AT1:
     *
     * Given that I am an employee or employer, when I try to log in without entering a username,
     * then I should be told to enter a username and I will not be logged in.
     */
    @Test
    public void checkUserNameIsEmpty() {
        onView(withId(R.id.loginUsernameET)).perform(typeText(""));
        onView(withId(R.id.loginButton)).perform(click());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginStatus)).check(matches(withText("username is empty")));
    }

    /**
     * US1-AT2:
     *
     * Given that I am an employee or employer, when I try to log in without entering a password,
     * then I should be told to enter a password and I will not be logged in.
     */
    @Test
    public void checkPasswordIsEmpty() {
        onView(withId(R.id.loginUsernameET)).perform(typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.loginPasswordET)).perform(typeText(""), ViewActions.closeSoftKeyboard());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.loginStatus)).check(matches(withText("password is empty")));
    }

    /**
     * US1-AT3:
     *
     * Given that am an employee, when I try to log in with a username
     * and password that matches an existing account, then I will be logged in.
     */
    @Test
    public void checkIfLoggedInEmployee() {
        onView(withId(R.id.loginUsernameET)).perform(typeText("testEmployee@dal.ca"));
        onView(withId(R.id.loginPasswordET)).perform(typeText("1234"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.employeeView)).check(matches(isDisplayed()));

    }

    /**
     * US1-AT3:
     *
     * Given that am an employer, when I try to log in with a username
     * and password that matches an existing account, then I will be logged in.
     */
    @Test
    public void checkIfLoggedInEmployer() {
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.loginUsernameET)).perform(typeText("testEmployer@dal.ca"));
        onView(withId(R.id.loginPasswordET)).perform(typeText("1234"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.employerView)).check(matches(isDisplayed()));

    }

    /**
     * US1-AT4:
     *
     * Given that I am an employee or employer, when I try to log in with a username and password
     * that does not match an existing account, then I will not be logged in.
     */
    @Test
    public void checkIfAccountExists() {
        onView(withId(R.id.loginUsernameET)).perform(typeText("peorge.swift@dal.ca"));
        onView(withId(R.id.loginPasswordET)).perform(typeText("123abc123"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.loginView)).check(matches(isDisplayed()));

    }


    /**
     * US6-AT1:
     * Given that I am an employee, when I login to the app then I should see a map interface unique
     * to employees.
     */
    @Test
    public void checkLandingPageEmployee() {
        onView(withId(R.id.loginUsernameET)).perform(typeText("testEmployee@dal.ca"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.loginPasswordET)).perform(typeText("1234"), ViewActions.closeSoftKeyboard());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.employeeView)).check(matches(isDisplayed()));
    }

    /**
     * US6-AT2:
     * Given that I am an employer, when I login to the app then
     * I should see an interface unique to employers.
     */
    @Test
    public void checkLandingPageEmployer() {
        onView(withId(R.id.loginUsernameET)).perform(typeText("testEmployer@dal.ca"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.loginPasswordET)).perform(typeText("1234"), ViewActions.closeSoftKeyboard());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.employerView)).check(matches(isDisplayed()));
    }

}