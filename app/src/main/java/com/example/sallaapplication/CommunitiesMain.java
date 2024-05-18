package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;
import com.adapter.CommunitiesAdapter;
import com.model.CommunitiesData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CommunitiesMain extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommunitiesAdapter adapter;
    private SearchView searchView;
    private List<CommunitiesData> communitiesDataList;
    private FirebaseUser user;
    private ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communites_main);

        // Initialize Firebase authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Initialize views
        recyclerView = findViewById(R.id.Communities);
        searchView = findViewById(R.id.searchicon);
        profileIcon = findViewById(R.id.profileicon);

        // Initialize data list
        communitiesDataList = new ArrayList<>();

        // Fetch isAdmin status from Firestore and set up UI
        fetchIsAdminStatus();

        // Set up search functionality
        setupSearchView();

        // Set up profile icon click listener
        setupProfileIconClickListener();

        // Set up initial data for the RecyclerView
        setupInitialDataForRecyclerView();
    }

    private void fetchIsAdminStatus() {
        if (user != null) {
            // Get the Firestore instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Get the document reference for the current user
            DocumentReference docRef = db.collection("users").document(user.getUid());

            // Retrieve the isAdmin field
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Boolean isAdmin = documentSnapshot.getBoolean("isAdmin");
                        if (isAdmin != null) {
                            // Pass isAdmin status to setCommunitiesRecycler method
                            setCommunitiesRecycler(isAdmin);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle failure to fetch isAdmin status
                }
            });
        }
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fileList(newText);
                return true;
            }
        });
    }

    private void setupProfileIconClickListener() {
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CommunitiesMain.this, ProfileChange.class);
                startActivity(i);
            }
        });
    }

    private void setupInitialDataForRecyclerView() {
        communitiesDataList.add(new CommunitiesData("   General Community","General", R.drawable.generalfinal));
        communitiesDataList.add(new CommunitiesData("Gluten free Community","Gluten", R.drawable.gluten));
        communitiesDataList.add(new CommunitiesData("Egg free Community","Egg", R.drawable.egg));
        communitiesDataList.add(new CommunitiesData("Sesame free Community","Sesame", R.drawable.sesame));
        communitiesDataList.add(new CommunitiesData("Lactose free Community","Lactose", R.drawable.lactose));
        communitiesDataList.add(new CommunitiesData("Tree nuts free Community", "Nut",R.drawable.treenuts));
        communitiesDataList.add(new CommunitiesData("Soy free Community","Soy", R.drawable.soy));
        communitiesDataList.add(new CommunitiesData("Peanut free Community","Peanut", R.drawable.peanut));
        communitiesDataList.add(new CommunitiesData("Seafood free Community","Seafood", R.drawable.seafood));
        communitiesDataList.add(new CommunitiesData("Mustard free Community","Mustard", R.drawable.mustard));
        // Set up RecyclerView
        setCommunitiesRecycler(false); // isAdmin status initially unknown, set to false as default
    }

    private void fileList(String text) {
        ArrayList<CommunitiesData> filteredList = new ArrayList<>();
        for (CommunitiesData communities : communitiesDataList) {
            if (communities.getCommunityName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(communities);
            }
            adapter.setFilteredList(filteredList);
        }
    }

    private void setCommunitiesRecycler(boolean isAdmin) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommunitiesAdapter(this, communitiesDataList, isAdmin);
        recyclerView.setAdapter(adapter);
    }

}
