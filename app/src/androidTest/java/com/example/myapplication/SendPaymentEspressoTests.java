package com.example.myapplication;



import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import io.reactivex.rxjava3.internal.util.AppendOnlyLinkedArrayList;


@RunWith(AndroidJUnit4.class)
public class SendPaymentEspressoTests {

    public static final String TEST_ID = "123";
    // Getting an instance of the firebase realtime database
    public static final DatabaseReference appRef = FirebaseUtils.connectFirebase().getReference().child("applications");
    public static final DatabaseReference jobRef = FirebaseUtils.connectFirebase().getReference().child("jobs");
    public static String appKey;
    public static String jobKey;

    @Rule
    public ActivityScenarioRule myRule = new ActivityScenarioRule<>(SendPayment.class);


    @BeforeClass
    public static void setup() {
        Session.startSession(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Session.login("test@dal.ca", TEST_ID, "Employer");
    }

    @Before
    public void pushJobAndApp() {
        Job job = new Job("test@dal.ca", "Test Job", "Mocking Job", new Location(0, 0),
                TEST_ID,"IT/Computers");
        job.setCompensation(50);

        Application app = new Application("employee@dal.ca", true, false, "123");

        jobKey = jobRef.push().getKey();
        jobRef.child(jobKey).setValue(job);

        appKey = appRef.child(TEST_ID).push().getKey();
        app.setJobID(jobKey);
        app.setEmployerEmail("test@dal.ca");

        appRef.child(TEST_ID).child(appKey).setValue(app);
    }

    @After
    public void cleanDB() {
        jobRef.child(jobKey).setValue(null);
        appRef.child(TEST_ID).child(appKey).setValue(null);
    }

    /** US16-AT1
     * Given that I am an employer, I should be able to see all employee applications that
     * I have accepted for my posted jobs.
     */
    @Test
    public void seePayment() {
        onView(ViewMatchers.withId(R.id.paymentRecycler))
                // scrollTo will fail the test if no item matches.
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(withText("Pay"))
                ));
    }

    /** US16-AT3
     * Given that I am an employer, I should be able to cancel an outstanding payment if
     * the employee doesn’t complete their work.
     */
    @Test
    public void declinedPayment() {
        //Mocking Payment decline
        appRef.child(TEST_ID).child(appKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Application app = snapshot.getValue(Application.class);
                Log.d("nullCheck", "onDataChange: " + app);
                app.setAccepted(false);
                appRef.child(TEST_ID).child(appKey).setValue(app);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        ActivityScenario.launch(SendPayment.class);
        assertEquals(PayAdapter.getApps().size(), 0);
    }

    /** US16-AT2
     * Given that I am an employer, I can pay an employee for their work through the PayPal API.
     * Note: For this user story, it is hard to automate tests for the paypal API so manual
     * tests were done and in other user stories for the completed
     * payment records.
     */
    @Test
    public void completePayment() {
        //Mocking payment accept
        appRef.child(TEST_ID).child(appKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Application app = snapshot.getValue(Application.class);
                Log.d("nullCheck", "onDataChange: " + app);
                app.setPaid(true);
                appRef.child(TEST_ID).child(appKey).setValue(app);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        ActivityScenario.launch(SendPayment.class);
        assertEquals(PayAdapter.getApps().size(), 0);
    }

}
