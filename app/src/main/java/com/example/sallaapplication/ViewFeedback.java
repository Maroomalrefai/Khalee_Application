package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.adapter.FeedbackAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewFeedback extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private List<String> feedbackList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feedback);
        recyclerView = findViewById(R.id.recyclerViewFeedback);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(feedbackList);
        recyclerView.setAdapter(adapter);

        fetchFeedbackFromFirebase();
    }
    private void fetchFeedbackFromFirebase() {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child("Feedback");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                feedbackList.clear();
//                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                    for (DataSnapshot feedbackSnapshot : userSnapshot.child("feedback").getChildren()) {
//                        String feedback = feedbackSnapshot.getValue(String.class);
//                        if (feedback != null) {
//                            feedbackList.add(feedback);
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feedbackList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Check if user has feedback
                    if (userSnapshot.hasChild("Feedback")) {
                        for (DataSnapshot feedbackSnapshot : userSnapshot.child("Feedback").getChildren()) {
                            String feedback = feedbackSnapshot.getValue(String.class);
                            if (feedback != null) {
                                feedbackList.add(feedback);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }



}