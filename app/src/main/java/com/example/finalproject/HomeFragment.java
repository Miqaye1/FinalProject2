package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class HomeFragment extends Fragment {
    private RecyclerView tagRecyclerView;
    private TagsAdapter tagAdapter;
    private ArrayList<String> tagList;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    ArrayList<ProjectModel> recycleList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tagRecyclerView = view.findViewById(R.id.layout_container);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize the tagList and tagAdapter
        tagList = new ArrayList<>();
        tagAdapter = new TagsAdapter(tagList, getActivity());
        tagRecyclerView.setAdapter(tagAdapter);

        // Retrieve tags from the database and populate the tagList
        retrieveTagsFromDatabase();

        recyclerView = view.findViewById(R.id.main_container);
        recycleList = new ArrayList<>();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FavouritesAdapter recyclerAdapter = new FavouritesAdapter(recycleList, requireContext());
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(recyclerAdapter);
        firebaseDatabase.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ProjectModel> newRecycleList = new ArrayList<>(); // Create a new list for updated order
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    DataSnapshot postSnapshot = userSnapshot.child("post");
                    ArrayList<DataSnapshot> postSnapshots = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : postSnapshot.getChildren()) {
                        postSnapshots.add(dataSnapshot);
                    }
                    Collections.reverse(postSnapshots); // Reverse the order of postSnapshots
                    for (DataSnapshot dataSnapshot : postSnapshots) {
                        ProjectModel projectModel = dataSnapshot.getValue(ProjectModel.class);
                        projectModel.setUserId(userId);

                        if (projectModel.getTags() == null) {
                            projectModel.setTags(new HashMap<>());
                        }

                        newRecycleList.add(projectModel);
                    }
                }
                recycleList.clear(); // Clear the original recycleList
                recycleList.addAll(newRecycleList); // Add newRecycleList to recycleList
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the cancellation error if needed
            }
        });
        return view;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.plus:
                Intent intent = new Intent(getActivity(), PostActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu); // Inflate the menu resource
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void retrieveTagsFromDatabase() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tagList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : userSnapshot.child("post").getChildren()) {
                        String postId = postSnapshot.getKey();
                        for (DataSnapshot tagSnapshot : postSnapshot.child("tags").getChildren()) {
                            String tag = tagSnapshot.getKey();
                            if (tagSnapshot.getValue() != null && tagSnapshot.getValue().equals(true)) {
                                tagList.add(tag);
                            }
                        }
                    }
                }
                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if needed
            }
        });
    }
}