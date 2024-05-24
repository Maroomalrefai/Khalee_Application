package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.PostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.model.Post;

import java.util.ArrayList;
import java.util.List;

public class DetailCommunity extends AppCompatActivity {
    FloatingActionButton addButton;
    RecyclerView recyclerView;
    List<Post> postList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    ProgressBar progressBar;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String currentCommunityId;
    String currentCommunityName;
    TextView communityname;
    PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_community);
        addButton = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recyclerView);
        communityname = findViewById(R.id.communityname);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(DetailCommunity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailCommunity.this);
        builder.setCancelable(false);
        progressBar.setVisibility(View.VISIBLE);

        postList = new ArrayList<>();

        // Receive community information passed from the previous activity
        Intent intent = getIntent();
        if (intent != null) {
            currentCommunityId = intent.getStringExtra("communityId");
            currentCommunityName = intent.getStringExtra("communityName");
            communityname.setText(currentCommunityName);
        }
        if (currentCommunityId != null) {
            if (!isNetworkAvailable()) {
                Toast.makeText(DetailCommunity.this, "No internet connection. Failed to reload posts.", Toast.LENGTH_LONG).show();
                return;
            }
            databaseReference = FirebaseDatabase.getInstance().getReference("Communities").child(currentCommunityId).child("Posts");
            progressBar.setVisibility(View.VISIBLE);

            eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    postList.clear();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        Post post = itemSnapshot.getValue(Post.class);
                        postList.add(0, post);
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });

            // Fetch the isAdmin status from Firestore
            fetchIsAdminStatus();

        } else {
            finish();
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCommunity.this, CreatePost.class);
                intent.putExtra("communityId", currentCommunityId);
                intent.putExtra("communityName", currentCommunityName);
                startActivity(intent);
            }
        });
    }

    private void fetchIsAdminStatus() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = fStore.collection("Khalee_Users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        boolean isAdmin = document.getBoolean("isAdmin") != null && document.getBoolean("isAdmin");
                        setupPostAdapter(isAdmin);
                    } else {
                        Toast.makeText(DetailCommunity.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailCommunity.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupPostAdapter(boolean isAdmin) {
        adapter = new PostAdapter(DetailCommunity.this, postList, isAdmin, currentCommunityId);
        recyclerView.setAdapter(adapter);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
