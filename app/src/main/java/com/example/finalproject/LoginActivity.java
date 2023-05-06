package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.databinding.ActivityLoginBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class LoginActivity extends AppCompatActivity {


    ActivityLoginBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.loginEmail.getText().toString();
                String password = binding.loginPassword.getText().toString();

                if(email.equals("") || password.equals("")) {
                    CustomToast.showErrorToast(LoginActivity.this, "An error occurred!", "All fields are mandatory", 1000);
                }
                else{
                    usersRef.whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    boolean passwordMatched = false;
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        DatabaseHelper user = documentSnapshot.toObject(DatabaseHelper.class);
                                        if (user.getPassword().equals(password)) {
                                            passwordMatched = true;
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            break;
                                        }
                                    }
                                    if (!passwordMatched) {
                                        CustomToast.showErrorToast(LoginActivity.this, "An error occurred!", "Wrong password", 1000);
                                    }
                                } else {
                                    CustomToast.showErrorToast(LoginActivity.this, "An error occurred!", "User not found", 1000);
                                }
                            })
                            .addOnFailureListener(e -> {
                                CustomToast.showErrorToast(LoginActivity.this, "An error occurred!", "Login failed. Please check your internet connection", 1000);
                            });
                }
            }
        });
        binding.gotoSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}