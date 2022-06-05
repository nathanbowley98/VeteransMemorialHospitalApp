package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * LoginActivity class that manages the LoginActivity events that are enacted by a user.
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
public class LoginActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;


    EditText id,password;
    Button login,signup;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = findViewById(R.id.loginUsernameET);
        password = findViewById(R.id.loginPasswordET);
        signup = findViewById(R.id.loginToSignupButton);
        login = findViewById(R.id.loginButton);
        status = findViewById(R.id.loginStatus);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameInput = id.getText().toString();
                String passwordInput = LoginActivity.this.password.getText().toString();
                if (TextUtils.isEmpty(usernameInput)){
                    status.setText(getString(R.string.name_empty));
                }
                else if (TextUtils.isEmpty(passwordInput)){
                    status.setText(getString(R.string.pwd_empty));
                }
                else {
                    tryLogin(usernameInput, passwordInput);
                }

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, GoogleMapsActivity.class));
            }
        });

        connectFirebase();

    }

    /**
     * loginEvent method that is responsible for managing the the login events by searching through
     * dataSnapshot objects to find the correct child in the Firebase realtime database.
     * @author: everyone
     */
    private void tryLogin(String usernameInput, String passwordInput) {
        connectFirebase();
        firebaseDBRef.getDatabase();

        firebaseDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                    String checkEmail = dataSnapshot.child("email").getValue(String.class);
                    String checkPassword = dataSnapshot.child("password").getValue(String.class);
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);

                    boolean passwordIsSame = passwordInput.equals(checkPassword);
                    boolean emailIsSame = usernameInput.equals(checkEmail);

                    if(passwordIsSame && emailIsSame){
                        found = true;
                        String userType = dataSnapshot.child("userType").getValue(String.class);

                        String userID = dataSnapshot.getRef().getKey();
                        Intent intent;
                        if(userType.equalsIgnoreCase(Employee.EMPLOYEE)) {
                            intent = new Intent(LoginActivity.this, EmployeeActivity.class);
                        }
                        else {
                            intent = new Intent(LoginActivity.this, EmployerActivity.class);
                        }
                        Session.login(checkEmail, userID, userType);
                        Session.name(firstName,lastName);
                        startActivity(intent);
                        break;

//                        if (employType.equalsIgnoreCase(Employee.EMPLOYEE)) {
//                            Intent intent = new Intent(LoginActivity.this, EmployeeActivity.class);
//
//                            intent.putExtra("User Hash", dataSnapshot.getRef().getKey());
//                            intent.putExtra("Login Email", email);
//                            intent.putExtra("Login Password", password);
//                            intent.putExtra("User Type", Employee.EMPLOYEE);
//
//                            dataSnapshot.child("loginState").getRef().setValue(true);
//                            startActivity(intent);
//
//                        }
//                        else if (employType.equalsIgnoreCase(Employer.EMPLOYER)) {
//                            Intent intent = new Intent(LoginActivity.this, EmployerActivity.class);
//
//                            intent.putExtra("User Hash", dataSnapshot.getRef().getKey());
//                            intent.putExtra("Login Email", email);
//                            intent.putExtra("Login Password", password);
//                            intent.putExtra("User Type", Employer.EMPLOYER);
//
//                            dataSnapshot.child("loginState").getRef().setValue(true);
//                            startActivity(intent);
//                        }
//                        else {
//                            Log.e("Error", "User Type is neither Employee or Employer!");
//                            System.exit(-1);
//                        }
                    }
                }
                if (!found) {
                    Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
//        Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_SHORT).show();



    }
    /**
     * connectFirebase method that acts to connect the firebase using the firebase url
     * @author: everyone
     */
    private void connectFirebase(){
        firebaseDB = FirebaseDatabase.getInstance(FirebaseUtils.FIREBASE_URL);
        firebaseDBRef = firebaseDB.getReference("users");
    }

}
