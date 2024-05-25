package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
    String profileImageUrl;

    private ConstraintLayout constraintLayout;
    private boolean isAdmin;

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
        constraintLayout = findViewById(R.id.constraintLayout);

        // Initialize data list
        communitiesDataList = new ArrayList<>();

        // Set profile image and user name if available
        if (user != null) {
            // Set profile image
            profileImageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            if (profileImageUrl != null) {
                // Load profile image using your preferred image loading library, e.g., Picasso, Glide
                Picasso.get().load(profileImageUrl).placeholder(R.drawable.profileicon).into(profileIcon);
            }
        }
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
            String userId = user.getUid();
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("Khalee_Users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        isAdmin = document.getBoolean("isAdmin") != null && document.getBoolean("isAdmin");
                        setCommunitiesRecycler(isAdmin);
                    } else {
                        Toast.makeText(CommunitiesMain.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CommunitiesMain.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
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
                Intent intent;
                if (isAdmin) {
                    intent = new Intent(CommunitiesMain.this, AdminProfile.class);
                } else {
                    intent = new Intent(CommunitiesMain.this, ProfileChange.class);
                }
                startActivity(intent);
            }
        });
    }

    private void setupInitialDataForRecyclerView() {
        communitiesDataList.add(new CommunitiesData("General Community", "General", R.drawable.generalfinal));
        communitiesDataList.add(new CommunitiesData("Gluten free Community", "Gluten", R.drawable.gluten));
        communitiesDataList.add(new CommunitiesData("Egg free Community", "Egg", R.drawable.egg));
        communitiesDataList.add(new CommunitiesData("Sesame free Community", "Sesame", R.drawable.sesame));
        communitiesDataList.add(new CommunitiesData("Lactose free Community", "Lactose", R.drawable.lactose));
        communitiesDataList.add(new CommunitiesData("Tree nuts free Community", "Nut", R.drawable.treenuts));
        communitiesDataList.add(new CommunitiesData("Soy free Community", "Soy", R.drawable.soy));
        communitiesDataList.add(new CommunitiesData("Peanut free Community", "Peanut", R.drawable.peanut));
        communitiesDataList.add(new CommunitiesData("Seafood free Community", "Seafood", R.drawable.seafood));
        communitiesDataList.add(new CommunitiesData("Mustard free Community", "Mustard", R.drawable.mustard));
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
        if (isAdmin) {
            constraintLayout.setBackgroundResource(R.drawable.adsmall);
        } else {
            constraintLayout.setBackgroundResource(R.drawable.bluesmallbar);
        }
    }
}
