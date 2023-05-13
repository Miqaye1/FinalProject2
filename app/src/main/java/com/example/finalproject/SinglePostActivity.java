package com.example.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SinglePostActivity extends AppCompatActivity {

    TextView singleDescription;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    TextView nameAndSurname;
    ImageView singleImage, profileImage;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        singleDescription = findViewById(R.id.singleDescription);
        singleImage = findViewById(R.id.singleImage);
        profileImage = findViewById(R.id.profile_image);
        nameAndSurname = findViewById(R.id.nameAndSurname);
        Picasso.get().load(getIntent().getStringExtra("singleImage"))
                .into(singleImage);

        singleDescription.setText(getIntent().getStringExtra("singleDescription"));
        userId = getIntent().getStringExtra("userId");
        Log.d("SinglePostActivity", "userId: " + userId); // Add this line
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.exists()) {
                        String profileImageUrl = dataSnapshot.child("profile_picture").getValue(String.class);
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String surname = dataSnapshot.child("surname").getValue(String.class);

                        // Load the profile_image using Picasso
                        Picasso.get().load(profileImageUrl)
                                .placeholder(R.drawable.rsz_1rsz_1rsz_1rsz_1user)
                                .into(profileImage);

                        nameAndSurname.setText(name + " " + surname);
                    }
                }else {
                    // Handle the case where the profile_image URL is not available
                    // You can display a default profile_image or show an error message
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case
            }
        });
    }
}
