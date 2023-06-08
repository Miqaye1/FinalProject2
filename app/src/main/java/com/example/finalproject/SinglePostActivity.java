package com.example.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SinglePostActivity extends AppCompatActivity {

    TextView singleDescription;
    private AppCompatButton deleteButton;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    TextView nameAndSurname;
    ImageView singleImage, profileImage;
    String userId;
    private String userId2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);
        ActionBar actionBar = getSupportActionBar();

        // Set the background color of the action bar
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); // Red color

        // Set other properties of the action bar as needed

        SpannableString s = new SpannableString("TravelEasy");
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#348881")), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar));
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            userId2 = currentUser.getUid();
        }
        else{
            Toast.makeText(this, "UserId is null", Toast.LENGTH_SHORT).show();
        }
        singleDescription = findViewById(R.id.singleDescription);
        singleImage = findViewById(R.id.singleImage);
        profileImage = findViewById(R.id.profile_image);
        nameAndSurname = findViewById(R.id.nameAndSurname);
        String imageUrl = getIntent().getStringExtra("singleImage");
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.loading) // Set the placeholder image resource
                .into(singleImage);
        singleDescription.setText(getIntent().getStringExtra("singleDescription"));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }
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
                } else {
                    // Handle the case where the profile_image URL is not available
                    // You can display a default profile_image or show an error message
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case
            }
        });
        ToggleButton toggleButton = findViewById(R.id.toggleButton);
        String postId = getIntent().getStringExtra("postId");
        deleteButton = findViewById(R.id.delete_button);
        DatabaseReference currentUserPostRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId2)
                .child("post")
                .child(postId);

        currentUserPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // The post belongs to the current user
                    deleteButton.setVisibility(View.VISIBLE);
                } else {
                    // The post does not belong to the current user
                    deleteButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that occur
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Delete the post from the Firebase Realtime Database
            currentUserPostRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Post deleted successfully
                        Toast.makeText(SinglePostActivity.this, "Post deleted", Toast.LENGTH_SHORT).show();

                        // Remove the post from the UI
                        singleDescription.setText("");
                        // Remove the post image using Picasso or any other image-loading library
                        // Hide other post-related views if necessary
                        // ...

                        // Finish the activity or navigate back to the previous screen
                        finish();
                    } else {
                        // Failed to delete the post
                        Toast.makeText(SinglePostActivity.this, "Failed to delete post", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    });

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId2);
        DatabaseReference favouritesRef = userRef.child("favourites");
        favouritesRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // The post is in the user's favorites
                    toggleButton.setChecked(true);
                } else {
                    // The post is not in the user's favorites
                    toggleButton.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that occur
            }
        });
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleFavourite(postId, isChecked);
            }
        });
        nameAndSurname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SinglePostActivity.this, PostProfileActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SinglePostActivity.this, PostProfileActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

    }

    private void toggleFavourite(String postId, boolean isChecked) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId2 = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId2);
        DatabaseReference favouritesRef = userRef.child("favourites");

        // Check if the post is already in the user's favourites
        favouritesRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isChecked) {
                    // Add the post to favourites
                    favouritesRef.child(postId).setValue(true);
                } else {
                    // Remove the post from favourites
                    favouritesRef.child(postId).removeValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that occur
            }
        });
    }
}
