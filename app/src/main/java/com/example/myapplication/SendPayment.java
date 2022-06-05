package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.t;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SendPayment extends AppCompatActivity implements PayAdapter.IJobListener{
    //This should be a local key. Explained in paypal integration tutorial
    public static final String clientKey = "AUvAZuJ0IvHFmyIeOdDhsVPmjFYiHqBwglRNzUBAdmV95xvNZk5DYumlNGpgAesKfoBF0Baf2FFB1z45";
    public static final int PAYPAL_REQUEST_CODE = 123;

    //Database references
    private FirebaseDatabase firebaseDB = FirebaseUtils.connectFirebase();
    private DatabaseReference firebaseDBRefJobs = firebaseDB.getReference(FirebaseUtils.JOBS_COLLECTION);

    //Recycler view components
    private RecyclerView payRecyclerView;
    private PayAdapter  payAdapter;
    private  ArrayList<Application> applications = new ArrayList<>();
    private ArrayList<String> appKeys = new ArrayList<>();

    // Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready,
            // switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            // on below line we are passing a client id.
            .clientId(clientKey);


    private Button refreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_payment);
        payRecyclerView = findViewById(R.id.paymentRecycler);
        refreshBtn = findViewById(R.id.payRefreshBtn);

        initRecyclerView();

        getPaymentPendingApplications(Session.getUserID());

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPaymentPendingApplications(Session.getUserID());
            }
        });

    }

    //Create recycler view
    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        payRecyclerView.setLayoutManager(linearLayoutManager);
        payAdapter = new PayAdapter(applications, appKeys, this);
        payRecyclerView.setAdapter(payAdapter);
    }

    //Get all applications that have been accepted but no yet paid for currently logged in
    //employee.
    private void getPaymentPendingApplications(String employerID) {
        final Query appQuery = firebaseDB.getReference("applications").child(employerID);
        appQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                applications.clear();
                appKeys.clear();

                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot currentSnapShot : snapshot.getChildren()) {
                        Application app = currentSnapShot.getValue(Application.class);
                        if (app != null && !app.isPaid() && app.getAccepted()) {
                            applications.add(app);
                            appKeys.add(currentSnapShot.getKey());
                        }
                    }
                }
                payAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchActivity", error.getMessage());
            }
        });
    }


    //Process paypal payment for associated job
    @Override
    public void onPayClick(int position) {
        Application app = applications.get(position);
        String userID = Session.getUserID();

        //Get payment amount for job and then add payment record to database
        firebaseDBRefJobs.child(app.getJobID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Job.class) != null) {
                    Job job = dataSnapshot.getValue(Job.class);
                    double compensation = job.getCompensation();
                    String jobTitle = job.getJobTitle();
                    Log.d("comp", "onDataChange: " + compensation);

                    //Paypal payment
                    if(getPayment(compensation, jobTitle)) {
                        //Add payment record to database
                        storePayment(app, compensation);
                        app.setPaid(true);
                        firebaseDB.getReference("applications").child(userID)
                                .child(appKeys.get(position))
                                .setValue(app);

                        //Refresh recycler view
                        getPaymentPendingApplications(Session.getUserID());
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Could not find linked job!",
                            Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Un-accept offer
    @Override
    public void onCancelClick(int position) {
        Application app = applications.get(position);
        app.setAccepted(false);
        firebaseDB.getReference("applications").child(Session.getUserID())
                .child(appKeys.get(position)).setValue(app);
        getPaymentPendingApplications(Session.getUserID());
    }

    //Store payment record to database
    private void storePayment(Application app, double amount) {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        PaymentRecord record = new PaymentRecord(app.getEmployerEmail(), app.getEmployeeEmail(),
                amount ,date);
        storePayment(record, firebaseDB);
    }

    //Paypal stuff from tutorial
    private boolean getPayment(double amount, String jobTitle) {

        // Creating a paypal payment on below line.
        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "USD", jobTitle,
                PayPalPayment.PAYMENT_INTENT_SALE);

        // Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        // Putting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        // Starting the intent activity for result
        // the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

        //Need more logic if user wishes to cancel
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            // If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {

                // Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                // if confirmation is not null
                if (confirm != null) {
                    try {
                        // Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        // on below line we are extracting json response and displaying it in a text view.
                        JSONObject payObj = new JSONObject(paymentDetails);
                        String payID = payObj.getJSONObject("response").getString("id");
                        String state = payObj.getJSONObject("response").getString("state");
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Payment " + state + "\n with payment id is " + payID, Toast.LENGTH_SHORT);
                        toast.show();

                    } catch (JSONException e) {
                        // handling json exception on below line
                        Log.e("Error", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // on below line we are checking the payment status.
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                // on below line when the invalid paypal config is submitted.
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }


    //Paypal store record in database
    protected boolean storePayment(PaymentRecord payment, FirebaseDatabase db) {
        db.getReference(FirebaseUtils.PAYMENT_COLLECTION).push().setValue(payment);
        return true;
    }

}
