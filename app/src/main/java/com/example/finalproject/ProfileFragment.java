package com.example.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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
    private Context context;

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        profileImage = requireView().findViewById(R.id.imageView5);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (context == null) {
                    // Fragment is not attached to a context, do not proceed
                    return;
                }

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profile_picture")) {
                        String imageUriString = dataSnapshot.child("profile_picture").getValue(String.class);
                        if (imageUriString != null) {
                            RequestOptions requestOptions = new RequestOptions()
                                    .placeholder(R.drawable.rsz_1rsz_1rsz_1rsz_1user)
                                    .error(R.drawable.rsz_1rsz_1rsz_1rsz_1user);

                            Glide.with(context)
                                    .load(imageUriString)
                                    .apply(requestOptions)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .skipMemoryCache(false)
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
                if (context != null) {
                    Toast.makeText(context, "Error retrieving profile picture", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImage = view.findViewById(R.id.imageView5);

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
