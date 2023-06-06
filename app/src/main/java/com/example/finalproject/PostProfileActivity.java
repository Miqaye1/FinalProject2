package com.example.finalproject;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostProfileActivity extends AppCompatActivity {

    TextView nameAndSurname, email;
    ArrayList<ProjectModel> recycleList;
    RecyclerView recyclerView;
    ImageView profileImage;
    String userId;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); // Red color
        SpannableString s = new SpannableString("TravelEasy");
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#348881")), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar));
        }

        nameAndSurname = findViewById(R.id.nameAndSurname_post_profile);
        profileImage = findViewById(R.id.profile_image_post_profile);
        email = findViewById(R.id.post_email);

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
                    String profileImageUrl = dataSnapshot.child("profile_picture").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String surname = dataSnapshot.child("surname").getValue(String.class);
                    String email_2 = dataSnapshot.child("email").getValue(String.class);

                    email.setText(email_2);
                    nameAndSurname.setText(name + " " + surname);

                    RequestOptions requestOptions = new RequestOptions()
                            .placeholder(R.drawable.rsz_1rsz_1rsz_1rsz_1user)
                            .error(R.drawable.rsz_1rsz_1rsz_1rsz_1user);
                    Glide.with(PostProfileActivity.this)
                            .load(profileImageUrl)
                            .apply(requestOptions)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false)
                            .into(profileImage);
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

        recyclerView = findViewById(R.id.main_container3);
        recycleList = new ArrayList<>();
        PostProfileAdapter recyclerAdapter = new PostProfileAdapter(recycleList, getApplicationContext());
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(recyclerAdapter);

        DatabaseReference postsRef = firebaseDatabase.getReference("users").child(userId).child("post");
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recycleList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String postId = postSnapshot.getKey();
                    ProjectModel projectModel = postSnapshot.getValue(ProjectModel.class);
                    projectModel.setUserId(userId);
                    projectModel.setPostId(postId); // Add the postId to the project model
                    recycleList.add(projectModel);
                }
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the cancellation error if needed
            }
        });
    }
}