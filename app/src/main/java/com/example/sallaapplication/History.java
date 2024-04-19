package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.adapter.MyAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.model.HistoryData;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {

    RecyclerView recyclerView;
    List<HistoryData> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.proBar);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(History.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        progressBar.setVisibility(View.VISIBLE);

        String userId = getCurrentUserId();
        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials").child(userId);
            dataList = new ArrayList<>();
            MyAdapter adapter = new MyAdapter(History.this, dataList);
            recyclerView.setAdapter(adapter);

            eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dataList.clear();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        HistoryData dataClass = itemSnapshot.getValue(HistoryData.class);
                        dataList.add(dataClass);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    // Handle onCancelled
                }
            });
        } else {
            // Handle the case when the user is not signed in
            // For example, display a message or redirect the user to the login screen
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("User is not signed in.")
                    .setPositiveButton("OK", (dialog, which) -> finish())
                    .show();
        }
    }

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            return null;
        }
    }
}
