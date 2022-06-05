package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Random;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * RegisterUser Activity that is responsible for allowing a user to signup their user details
 * and submit it to a database to be stored for later login. The RegisterUser activity also
 * redirects the user to the correct EmployeeActivity or EmployerActivity depending on their
 * chosen Employment type.
 * @authors: Nathanael Bowley,
 *          John Corsten,
 *          Nathan Horne,
 *          Ted Graveson,
 *          Hongzheng Ding,
 *          Tianhao Jia,
 *          Saher Anwar Ziauddin
 * @course: CSCI3130 @ Dalhousie University.
 * @semester: Winter 2022
 * @group: Group 4
 * @clientTA: Disha Malik
 */
public class RegisterUser extends AppCompatActivity{

    private EditText nameFNField;
    private EditText nameLNField;
    private EditText emailField;
    private Spinner userTypeSpinner;
    private EditText passwordField;
    private ImageView imageView;
    private String[] userTypes;
    private String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        userTypes = getResources().getStringArray(R.array.user_types);

        nameFNField = findViewById(R.id.registerFirstName);
        nameLNField = findViewById(R.id.registerLastName);
        emailField = findViewById(R.id.registerEmail);
        passwordField = findViewById(R.id.registerPasswordET);
        userTypeSpinner = findViewById(R.id.registerUserSpinner);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> registerUser());

        @SuppressLint("ResourceType") ArrayAdapter<String> userTypesAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, userTypes);
        userTypesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(userTypesAdapter);

    }


    /**
     * registerUser(): if the user doesn't already have an account, and the user inputs the correct
     *                 details, then it successfully registers the user
     */
    public void registerUser(){
        userType = userTypeSpinner.getSelectedItem().toString();

        // get the reference to the database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userAlreadyExists = false;

                // checks if user email already exists
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String databaseEmail = dataSnapshot.child("email").getValue(String.class);

                    if(databaseEmail != null && databaseEmail.equals(emailField.getText().toString())){
                        userAlreadyExists = true;
                    }

                }

                // if user already exists, then display an error message, otherwise validate user
                // input
                if(userAlreadyExists){
                    displayToast("You are already registered!");
                } else{
                    if(validateInput()) {
                        String key = addRecord();
                        switchActivity(key);
                    }
                    else {
                        displayToast("Registration Failed!");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * addRecord(): reads the existing values inputted to the fields and adds them to the database
     *              as a hashmap record under the child "users"
     */
    protected String addRecord() {

        // Finding all views by ID from the register page
        EditText nameFNField = findViewById(R.id.registerFirstName);
        EditText nameLNField = findViewById(R.id.registerLastName);
        EditText emailField = findViewById(R.id.registerEmail);
        EditText passwordField = findViewById(R.id.registerPasswordET);
        Random rand = new Random();
        String key = Integer.toString(rand.nextInt(1000000000));

        //US-3 functionality forcing 2 types of users
        if (userType.equalsIgnoreCase(Employer.EMPLOYER) || userType.equalsIgnoreCase(Employee.EMPLOYEE)) {
            // Creating a HashMap of user information to store on firebase
            Map<String, Object> map = new HashMap<>();
            map.put("firstName", nameFNField.getText().toString());
            map.put("lastName", nameLNField.getText().toString());
            map.put("email", emailField.getText().toString());
            map.put("userType", userType);
            map.put("password", passwordField.getText().toString());
            map.put("hash", key);
            map.put("loginState", true);

            if(getIntent().getExtras() != null){
                map.put("user location (latitude)", ((LatLng) getIntent().getExtras().get("user location")).latitude);
                map.put("user location (longitude)", ((LatLng) getIntent().getExtras().get("user location")).longitude);
            } else{
                map.put("user location (latitude)", null);
                map.put("user location (longitude)", null);
            }

            // Getting an instance of the firebase realtime database
            FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL)
                    .getReference()
                    .child("users")
                    .child(key)
                    .setValue(map)
                    .addOnSuccessListener(aVoid -> {
                        displayToast("Registered!");
                    });
            return key;
        }
        else {
            Toast.makeText(getBaseContext(), "Please select from employee or employer", Toast.LENGTH_SHORT).show();
        }
        return "none";

    }

    /**
     * displayToast(): displays a long toast using string
     * @param message : String message to display
     */
    private void displayToast(String message) {
        Toast failRegAlert = Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG);

        failRegAlert.show();
    }

    /**
     * validateInput(): checks if user's first name, last name and email has been inputted correctly
     * @return boolean: if input is valid
     */
    protected boolean validateInput(){

        boolean firstNameValid = checkFirstName(nameFNField.getText().toString());
        boolean lastNameValid = checkLastName(nameLNField.getText().toString());
        boolean emailValid = checkEmail(emailField.getText().toString());
        boolean passwordValid = checkPassword(passwordField.getText().toString());

        return firstNameValid && lastNameValid && emailValid && passwordValid;
    }

    /**
     * checkFirstName(): first name should start with at least 2 characters
     * @param nameFN : first name to compare using regex
     * @return boolean : true if first name is valid; false otherwise
     */
    protected static boolean checkFirstName(String nameFN) {
        Pattern fnPattern = Pattern.compile("^[a-zA-z]{2,}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = fnPattern.matcher(nameFN.trim());

        return matcher.find();
    }

    /**
     * checkLastName(): last name should start with at least 2 characters
     * @param lastName : last name to compare using regex
     * @return boolean : true if last name is valid; false otherwise
     */
    protected static boolean checkLastName(String lastName) {
        Pattern lnPattern = Pattern.compile("^[a-z-']{2,}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = lnPattern.matcher(lastName.trim());
        return matcher.find();
    }

    /**
     * checkEmail(): checks if string is an email
     * @param email : email input to verify
     * @return boolean : true if email is valid; false otherwise
     */
    //Was using library functions to check email, however we ran into errors with pipeline
    // TODO: Email checking will be fixed in next iteration!
    protected static boolean checkEmail(String email) {
        Pattern emailPattern = Pattern.compile("^.*@.*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email.trim());
        return matcher.find();

    }

    /**
     * checkPassword(): checks if password is at least 4 characters
     * @param password : password input to verify
     * @return boolean : true if password is valid, false otherwise
     */
    protected static boolean checkPassword(String password) {
        Pattern pw = Pattern.compile("^.{4,}$", Pattern.CASE_INSENSITIVE);
        Matcher pwMatch = pw.matcher(password);

        return pwMatch.find();
    }

    private void switchActivity(String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (editor != null) {
            editor.putBoolean(Session.LOGIN, true);
            editor.commit();
        }

        if (userType.equalsIgnoreCase(Employee.EMPLOYEE)) {
            Intent intent = new Intent(RegisterUser.this, EmployeeActivity.class);
            intent.putExtra("Login Email", emailField.getText().toString());
            intent.putExtra("Login Password", passwordField.getText().toString());
            intent.putExtra("User Type", Employee.EMPLOYEE);
            intent.putExtra("User Hash", key);
            startActivity(intent);
        }
        else if (userType.equalsIgnoreCase(Employer.EMPLOYER)) {
            Intent intent = new Intent(RegisterUser.this, EmployerActivity.class);
            intent.putExtra("Login Email", emailField.getText().toString());
            intent.putExtra("Login Password", passwordField.getText().toString());
            intent.putExtra("User Type", Employer.EMPLOYER);
            intent.putExtra("User Hash", key);
            startActivity(intent);
        }
        else {
            Log.e("ERROR", "This should never be possible that a user is not employer or emploee!");
            //force crash.
            System.exit(-1);
        }

    }
}