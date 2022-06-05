package com.example.myapplication;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class RegisterUserEspressoTest {

    private static String userEmail = "george.smith@dal.ca\n";
    private static String userFName = "George\n";
    private static String userLName = "Smith\n";
    private static String password = "password123\n";

    @Rule
    public ActivityScenarioRule rule  = new ActivityScenarioRule<>(RegisterUser.class);

    @Test
    public void activityInView() {
        onView(withId(R.id.registerUser)).check(matches(isDisplayed()));
    }


    /**
     * US2-AT1:
     * Given that the user is not registered, when the user inputs their details and clicks the
     * “register” button, the user should be registered.
     */
    @Test
    public void userRegistered() {
        onView(withId(R.id.registerFirstName)).perform(typeText(userFName));
        onView(withId(R.id.registerLastName)).perform(typeText(userLName));
        onView(withId(R.id.registerEmail)).perform(typeText(userEmail));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerPasswordET)).perform(typeText(password));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerUserSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.employeeView)).check(matches(isDisplayed()));
    }

    /**
     * US2-AT2:
     * Given that the user is registered, when the user inputs their details and clicks “register”,
     * it should display an error: “You already have an account”
     */
    @Test
    public void AlreadyRegisteredTest(){
        // Finding all views by ID from the register page
        String nameFn = "John";
        String lastName = "Adams";
        String emailField = "john.adams@dal.ca";
        String userType = "Employee";

        // Creating a HashMap of user information to store on firebase
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", nameFn);
        map.put("lastName", lastName);
        map.put("email", emailField);
        map.put("userType", userType);

        FirebaseDatabase.getInstance("https://quick-cash-55715-default-rtdb.firebaseio.com/")
                .getReference()
                .child("users")
                .push()
                .setValue(map);

        // Assume that an 'example' record already exists on Firebase with these details
        onView(withId(R.id.registerFirstName)).perform(typeText("John\n"));
        onView(withId(R.id.registerLastName)).perform(typeText("Adams\n"));
        onView(withId(R.id.registerEmail)).perform(typeText("john.adams@dal.ca\n"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerUserSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());

        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.registerUser)).check(matches(isDisplayed()));
    }

    /**
     * US2-AT3: Given that the user is not registered, when the user is inputting their details, then
     * we should throw an error if the required fields are not filled
     */
    @Test
    public void requiredFieldNotFilledTest(){
        onView(withId(R.id.registerFirstName)).perform(typeText("George\n"));
        onView(withId(R.id.registerLastName)).perform(typeText("Smith\n"));

        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerButton)).perform(click());

        // checks if login page is not displayed
        onView(withId(R.id.registerUser)).check(matches(isDisplayed()));
    }

    /**
     * US2-AT4:
     * Given that the user is not registered, when the user in inputting their details,
     * we should accept when the user has only filled the required fields
      */
    @Test
    public void allRequiredFieldsFilledEmployee() {
        onView(withId(R.id.registerFirstName)).perform(typeText("George\n"));
        onView(withId(R.id.registerLastName)).perform(typeText("Smith\n"));
        onView(withId(R.id.registerEmail)).perform(typeText("george.smith@dal.ca\n"));
        onView(withId(R.id.registerPasswordET)).perform(typeText("Password123\n"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerUserSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.employeeView)).check(matches(isDisplayed()));
    }

    /**
     * US2-AT4:
     * Given that the user is not registered, when the user in inputting their details,
     * we should accept when the user has only filled the required fields
     */
    //Need to add optional fields to UI, covid check was broken so will try and add that back
    //Also will add database mocks to make sure user doesn't already exist
    @Test
    public void allRequiredFieldsFilledEmployer() {
        onView(withId(R.id.registerFirstName)).perform(typeText("George\n"));
        onView(withId(R.id.registerLastName)).perform(typeText("Smith\n"));
        onView(withId(R.id.registerEmail)).perform(typeText("george.smith@dal.ca\n"));
        onView(withId(R.id.registerPasswordET)).perform(typeText("Password123\n"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerUserSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employer"))).perform(click());
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.employerView)).check(matches(isDisplayed()));
    }


    /**
     * US2-AT4:
     * Given that the user is not registered, when the user in inputting their details,
     * we should accept when the user has only filled the required fields
     */
    @Test
    public void requiredFieldsNotFilled() {
        onView(withId(R.id.registerFirstName)).perform(typeText("George\n"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerEmail)).perform(typeText("george.smith@dal.ca\n"));
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.registerUserSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.registerUser)).check(matches(isDisplayed()));
    }

    /**
     * US2-AT4:
     * Given that the user is not registered, when the user in inputting their details,
     * we should accept when the user has only filled the required fields
     */
    @Test
    public void requiredFieldsNotFilledPassword() {
        onView(withId(R.id.registerFirstName)).perform(typeText("George\n"));
        onView(withId(R.id.registerLastName)).perform(typeText("Smith\n"));
        onView(withId(R.id.registerEmail)).perform(typeText("george.smith@dal.ca\n"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerUserSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.registerUser)).check(matches(isDisplayed()));
    }

    /**
     * US2-AT5:
     * Given that the user is not registered, when the user fills their details and clicks the ‘register button’,
     * then the user should be directed to the login page.
     */
    @Test
    public void redirectToLoginPageEmployee() {
        onView(withId(R.id.registerFirstName)).perform(typeText("George\n"));
        onView(withId(R.id.registerLastName)).perform(typeText("Smith\n"));
        onView(withId(R.id.registerEmail)).perform(typeText("george.smith@dal.ca\n"));
        onView(withId(R.id.registerPasswordET)).perform(typeText("Password123\n"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerUserSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employee"))).perform(click());
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.employeeView)).check(matches(isDisplayed()));
    }

    /**
     * US2-AT5:
     * Given that the user is not registered, when the user fills their details and clicks the ‘register button’,
     * then the user should be directed to the correct login page.
     */
    @Test
    public void redirectToLoginPageEmployer() {
        onView(withId(R.id.registerFirstName)).perform(typeText("George\n"));
        onView(withId(R.id.registerLastName)).perform(typeText("Smith\n"));
        onView(withId(R.id.registerEmail)).perform(typeText("george.smith@dal.ca\n"));
        onView(withId(R.id.registerPasswordET)).perform(typeText("Password123\n"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.registerUserSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Employer"))).perform(click());
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.employerView)).check(matches(isDisplayed()));
    }

    @Before
    public void teardown(){
        FirebaseDatabase.getInstance().getReference("users").setValue(null);
    }

}