package com.example.sallaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class AdminHome extends AppCompatActivity {
    CardView Posts,Products,users,feedback;
    ImageView profile;
    String profileImageUrl;
    TextView Name;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Name = findViewById(R.id.name);
        users=findViewById(R.id.info);
        profile=findViewById(R.id.profileIcon);
        Posts = findViewById(R.id.posts);
        Products = findViewById(R.id.products);
        feedback=findViewById(R.id.cardViewFeedback);

        feedback.setOnClickListener(new View.OnClickListener() {
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
            }
        });
        Products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminHome.this, ViewProducts.class);
                startActivity(i);
            }
        });
        // Set profile image and user name if available
        if (user != null) {
            // Set profile image
            profileImageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            if (profileImageUrl != null) {
                // Load profile image using your preferred image loading library, e.g., Picasso, Glide
                Picasso.get().load(profileImageUrl).placeholder(R.drawable.profileicon).into(profile);
            }
            if (user.getDisplayName() != null) {
                Name.setText(user.getDisplayName());
            } else {
                Name.setText("User name");
            }
        }
    }
}