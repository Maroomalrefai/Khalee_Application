package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.adapter.PostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_community);
        addButton = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recyclerView);
        mAuth = FirebaseAuth.getInstance();
        currentUser  = mAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(DetailCommunity.this,1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailCommunity.this);
        builder.setCancelable(false);
        progressBar.setVisibility(View.VISIBLE);

        postList =new ArrayList<>();

        PostAdapter adapter = new PostAdapter(DetailCommunity.this,postList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials").child("Posts");
        progressBar.setVisibility(View.VISIBLE);

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Post post = itemSnapshot.getValue((Post.class));
                    postList.add(post);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the action to be taken when the FloatingActionButton is clicked
                Intent intent = new Intent(DetailCommunity.this, CreatePost.class); // Change CurrentActivity and NewActivity to your actual activity names
                startActivity(intent);
                finish();
            }
        });
    }
}