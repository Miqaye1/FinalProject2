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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements TagsAdapter.OnTagClickListener {
    private RecyclerView tagRecyclerView;
    private TagsAdapter tagAdapter;
    private ArrayList<String> tagList;
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<ProjectModel> recycleList;
    private FavouritesAdapter recyclerAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    // Rest of the code

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.main_container);
        recycleList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerAdapter = new FavouritesAdapter(recycleList, requireContext());
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(recyclerAdapter);

        tagRecyclerView = view.findViewById(R.id.layout_container);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize the tagList and tagAdapter
        tagList = new ArrayList<>();
        tagAdapter = new TagsAdapter(tagList, getActivity());
        tagAdapter.setOnTagClickListener(this);
        tagRecyclerView.setAdapter(tagAdapter);

        // Retrieve tags from the database and populate the tagList
        retrieveTagsFromDatabase();
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
                recycleList.addAll(0, newRecycleList); // Add newRecycleList to recycleList
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Enable options menu for the fragment
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
                HashMap<String, Integer> tagCountMap = new HashMap<>();

                // Iterate through each user
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Iterate through each post of the user
                    for (DataSnapshot postSnapshot : userSnapshot.child("post").getChildren()) {
                        // Get the tags of the post
                        DataSnapshot tagsSnapshot = postSnapshot.child("tags");
                        if (tagsSnapshot.exists()) {
                            // Iterate through each tag
                            for (DataSnapshot tagSnapshot : tagsSnapshot.getChildren()) {
                                String tag = tagSnapshot.getKey();
                                if (tagSnapshot.getValue() != null && tagSnapshot.getValue().equals(true)) {
                                    // Increment the count of the tag
                                    if (tagCountMap.containsKey(tag)) {
                                        int count = tagCountMap.get(tag);
                                        tagCountMap.put(tag, count + 1);
                                    } else {
                                        tagCountMap.put(tag, 1);
                                    }
                                }
                            }
                        }
                    }
                }

                // Sort the tags based on their count in descending order
                List<Map.Entry<String, Integer>> sortedTags = new ArrayList<>(tagCountMap.entrySet());
                Collections.sort(sortedTags, new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                        return entry2.getValue().compareTo(entry1.getValue());
                    }
                });

                // Clear the existing tagList
                tagList.clear();

                // Add the tags to the tagList in the sorted order
                for (Map.Entry<String, Integer> entry : sortedTags) {
                    tagList.add(entry.getKey());
                }

                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if needed
            }
        });
    }

    @Override
    public void onTagClick(String tag) {
/*        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ProjectModel> filteredList = new ArrayList<>();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        DataSnapshot postSnapshot = userSnapshot.child("post");

                        for (DataSnapshot postChildSnapshot : postSnapshot.getChildren()) {
                            String postId = postChildSnapshot.getKey();
                            DataSnapshot tagsSnapshot = postChildSnapshot.child("tags");

                            if (tagsSnapshot.child(tag).exists() && tagsSnapshot.child(tag).getValue(Boolean.class)) {
                                ProjectModel post = postChildSnapshot.getValue(ProjectModel.class);
                                if (post != null) {
                                    post.setUserId(userId);
                                    filteredList.add(post);
                                }
                            }
                        }
                    }
                }

                // Update the recyclerList with the filteredList
                recycleList.clear();
                recycleList.addAll(filteredList);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if needed
            }
        });*/
    }
}