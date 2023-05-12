package com.example.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    ImageView profileImage;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImage = view.findViewById(R.id.imageView5);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profile_picture")) {
                        String imageUriString = dataSnapshot.child("profile_picture").getValue(String.class);
                        if (imageUriString != null) {
                            Glide.with(requireContext())
                                    .load(imageUriString)
                                    .placeholder(R.drawable.rsz_1rsz_1rsz_1rsz_1user)
                                    .error(R.drawable.rsz_1rsz_1rsz_1rsz_1user) // Use the same placeholder image for error
                                    .into(profileImage);
                        } else {
                            profileImage.setImageResource(R.drawable.rsz_1rsz_1rsz_1rsz_1user); // Set placeholder image if imageUriString is null
                        }
                    } else {
                        profileImage.setImageResource(R.drawable.rsz_1rsz_1rsz_1rsz_1user); // Set placeholder image if profile_picture field doesn't exist
                    }
                } else {
                    profileImage.setImageResource(R.drawable.rsz_1rsz_1rsz_1rsz_1user); // Set placeholder image if dataSnapshot doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Error retrieving profile picture", Toast.LENGTH_SHORT).show();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileImage.class);
                startActivityForResult(intent, 1);
            }
        });

        return view;
    }
}
