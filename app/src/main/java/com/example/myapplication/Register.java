package com.example.myapplication;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Register class that has functionality for profile pictures which is intended to be implemented
 * and merged with RegisterUser in iteration 2.
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
public class Register extends AppCompatActivity {

    EditText id,name, password;
    ImageView profile;
    Button signupButton;
    Uri imageUri;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_register));
        id = findViewById(R.id.id);
        password = findViewById(R.id.loginPasswordET);
        name = findViewById(R.id.name);
        signupButton = findViewById(R.id.loginToSignupButton);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });
    }


    // warning to group: this method may be violating single responsibility principle.
    private void signup() {
        String idText = id.getText().toString();
        String passwordText = password.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(idText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("users").child(uid);

                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){

                                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String imageUrl = task.toString();
                                        UserModel userModel = new UserModel();
                                        userModel.setName(name.getText().toString());
                                        userModel.setUid(uid);
                                        userModel.setImageUrl(imageUrl);
                                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(Register.this,"Failed to create",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
