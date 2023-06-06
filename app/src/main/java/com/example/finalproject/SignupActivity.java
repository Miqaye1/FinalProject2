package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.finalproject.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    CollectionReference usersRef = firestoreDB.collection("users");
    DatabaseReference realtimeDBRef = FirebaseDatabase.getInstance("https://finalproject-11004-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.signupEmail.getText().toString();
                String password = binding.signupPassword.getText().toString();
                String name = binding.signupName.getText().toString();
                String surname = binding.signupSurname.getText().toString();

                if (email.equals("") || password.equals("") || name.equals("") || surname.equals("")) {
                    CustomToast.showErrorToast(SignupActivity.this, "An error occurred!", "All fields are mandatory", 1000);
                } else if (password.length() < 6) {
                    CustomToast.showErrorToast(SignupActivity.this, "An error occurred!", "Password should be at least 6 characters", 1000);
                } else if (name.length() < 2 || surname.length() < 2) {
                    CustomToast.showErrorToast(SignupActivity.this, "An error occurred!", "Name and surname should be at least 2 characters", 1000);
                } else {
                    usersRef.whereEqualTo("email", email)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                       /* sendEmailVerification(authResult.getUser());*/
                                                        String userId = authResult.getUser().getUid();
                                                        DatabaseHelper helperClass = new DatabaseHelper(name, surname, email, password);
                                                        DocumentReference userDocRef = usersRef.document(userId);
                                                        userDocRef.set(helperClass);
                                                        realtimeDBRef.child(userId).setValue(helperClass);
                                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        CustomToast.showErrorToast(SignupActivity.this, "An error occurred!", "Failed to create user: " + e.getMessage(), 1000);
                                                    }
                                                });
                                    } else {
                                        CustomToast.showErrorToast(SignupActivity.this, "An error occurred!", "You have already registered with this email", 1000);
                                    }
                                }
                            });
                }
            }
        });

        binding.gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }
    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Email verification sent successfully
                            Toast.makeText(SignupActivity.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                            // ...
                        } else {
                            // Failed to send verification email
                            Toast.makeText(SignupActivity.this, "Failed to send verification email.", Toast.LENGTH_LONG).show();
                            Log.e("EmailVerification", "Error sending verification email: " + task.getException().getMessage());
                        }
                    }
                });
    }
}