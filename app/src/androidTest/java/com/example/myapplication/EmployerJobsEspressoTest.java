package com.example.myapplication;

        import static androidx.test.espresso.Espresso.onData;
        import static androidx.test.espresso.Espresso.onView;
        import static androidx.test.espresso.action.ViewActions.actionWithAssertions;
        import static androidx.test.espresso.action.ViewActions.click;
        import static androidx.test.espresso.action.ViewActions.typeText;
        import static androidx.test.espresso.assertion.ViewAssertions.matches;
        import static androidx.test.espresso.intent.Intents.intended;
        import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
        import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
        import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
        import static androidx.test.espresso.matcher.ViewMatchers.withId;
        import static androidx.test.espresso.matcher.ViewMatchers.withText;

        import static org.hamcrest.CoreMatchers.allOf;
        import static org.hamcrest.CoreMatchers.instanceOf;
        import static org.hamcrest.CoreMatchers.is;

        import androidx.test.core.app.ActivityScenario;
        import androidx.test.espresso.Espresso;
        import androidx.test.espresso.ViewAssertion;
        import androidx.test.espresso.contrib.RecyclerViewActions;
        import androidx.test.espresso.intent.Intents;
        import androidx.test.espresso.matcher.ViewMatchers;
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

        import java.util.HashMap;
        import java.util.Map;

//for iteration 3 user story 2 testing of acceptance tests.
@RunWith(AndroidJUnit4.class)
public class EmployerJobsEspressoTest {

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
        FirebaseDatabase.getInstance().getReference(FirebaseUtils.USERS_COLLECTION).setValue(null);
        FirebaseDatabase.getInstance().getReference(FirebaseUtils.JOBS_COLLECTION).setValue(null);
        FirebaseDatabase.getInstance().getReference(FirebaseUtils.OFFERS_COLLECTION).setValue(null);
        FirebaseDatabase.getInstance().getReference(FirebaseUtils.PAYMENT_COLLECTION).setValue(null);


    }

    //jobs should be visible as a button for employers
    @Test
    public void testEmployerJobsButtonPresent() {
        ActivityScenario.launch(EmployerActivity.class);

        onView(withId(R.id.employerYourJobsButton)).check(matches(isDisplayed()));
    }

    //jobs should be visible as a button for employers
    @Test
    public void testEmployerJobsButtonIntentWorks() {
        ActivityScenario.launch(EmployerActivity.class);

        onView(withId(R.id.employerYourJobsButton)).perform(click());
        onView(withId(R.id.employerJobsRecyclerView)).check(matches(isDisplayed()));
    }



    @Test
    public void testEmployerJobsButtonShowsJobs() {
        ActivityScenario.launch(EmployerActivity.class);

        //create 2 jobs
        onView(withId(R.id.createJob)).perform(click());
        onView(withId(R.id.createJobTitle)).perform(typeText("Car Wash\n"));
        onView(withId(R.id.createJobDescription)).perform(typeText("Make my Hellcat shine\n"));
        onView(withId(R.id.createJobHourlyRate)).perform(typeText("25\n"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.createJobSubmitButton)).perform(click());

        //go to your jobs
        onView(withId(R.id.employerYourJobsButton)).perform(click());
        assert JobEmployerActivity.list.get(0).getDescription().equals("Make my Hellcat shine");
    }


    @Test
    public void testEmployerJobsButtonShowsNoJobs() {
        ActivityScenario.launch(EmployerActivity.class);

        //go to your jobs
        onView(withId(R.id.employerYourJobsButton)).perform(click());
        onView(withId(R.id.employerJobs)).check(matches(isDisplayed()));

        assert JobEmployerActivity.list.isEmpty();
    }

    @Test
    public void testEmployerJobsButtonShowsNoJobsMessage() {
        ActivityScenario.launch(EmployerActivity.class);

        //go to your jobs
        onView(withId(R.id.employerYourJobsButton)).perform(click());
        
        onView(allOf(withId(R.id.employerJobsNoJobs),
                withText("You have no jobs, create a job first."))).check(
                matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }



}
