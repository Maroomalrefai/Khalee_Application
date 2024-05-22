package com.example.sallaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminHome extends AppCompatActivity {
    CardView users;
    CardView Posts;
    ImageView profile;
    CardView cardViewFeedback ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        users=findViewById(R.id.info);
        profile=findViewById(R.id.profileIcon);
        Posts = findViewById(R.id.posts);
        cardViewFeedback = findViewById(R.id.cardViewFeedback);

        cardViewFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, ViewFeedback.class);
                startActivity(intent);
            }
        });
        Posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, CommunitiesMain.class);
                startActivity(intent);
            }
        });

        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminHome.this, ViewUsers.class);
                startActivity(i);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminHome.this, AdminProfile.class);
                startActivity(i);
                finish();
            }
        });
    }
}