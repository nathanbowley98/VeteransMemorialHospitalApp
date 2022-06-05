package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
public class LogoutForEmployerTest {

    private static final String employerKey = "tempEmployer";
    private static final String employeeKey = "tempEmployee";

    @Rule
    public ActivityScenarioRule<LoginActivity> myRule = new ActivityScenarioRule<>(LoginActivity.class);

    @BeforeClass
    public static void createTestUser() {
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

    }

    @BeforeClass
    public static void setup() {
        Intents.init();
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

    /**
     * US4-AT1:
     *
     * Register one user as an employer and then try click the logout button and test if it work.
     */
    @Test
    // run isolate
    public void logOutWithIntent() {
        onView(withId(R.id.loginUsernameET)).perform(typeText("testEmployer@dal.ca"));
        onView(withId(R.id.loginPasswordET)).perform(typeText("1234"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());

        onView(withId(R.id.employerLogoutButton)).perform(click());

        assertFalse(Session.checkLogin());
    }

    /**
     * US4-AT1:
     *
     * test the logout button can worked after one user registered
     */

    @Test
    public void testLogOutSp() {
        onView(withId(R.id.loginUsernameET)).perform(typeText("testEmployer@dal.ca"));
        onView(withId(R.id.loginPasswordET)).perform(typeText("1234"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.loginButton)).perform(click());

        onView(withId(R.id.employerLogoutButton)).perform(click());

        assertFalse(Session.checkLogin());
    }

    @AfterClass
    public static void teardown(){
        FirebaseDatabase.getInstance().getReference("users").setValue(null);
    }


}

