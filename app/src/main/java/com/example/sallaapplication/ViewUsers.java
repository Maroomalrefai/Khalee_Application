package com.example.sallaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.adapter.UserAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.model.CommunitiesData;
import com.model.Users;

import java.util.ArrayList;


public class ViewUsers extends AppCompatActivity {
    RecyclerView recyclerView;
    UserAdapter userAdapter;
//    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        searchView=findViewById(R.id.searchicon);



        FirebaseRecyclerOptions<Users> options =new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("users"),Users.class)
                .build();
        userAdapter =new UserAdapter(options);
        recyclerView.setAdapter(userAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        userAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userAdapter.startListening();

    }
}