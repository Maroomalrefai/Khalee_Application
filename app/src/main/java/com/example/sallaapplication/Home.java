package com.example.sallaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.CommunitiesAdapter;
import com.adapter.RecentsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.model.CommunitiesData;
import com.model.RecentData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    RecyclerView recentRecycler;
    TextView Name;
    ImageView profileIcon;
    RecentsAdapter recentsAdapter;
    RecyclerView communitiesRecycler;
    CommunitiesAdapter communitiesAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView scan=findViewById(R.id.recycle_bin);
        Name =findViewById(R.id.name);
        profileIcon = findViewById(R.id.profileIcon);

        // Set profile image and user name if available
        if (user != null) {
            // Set profile image
            String profileImageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            if (profileImageUrl != null) {
                // Load profile image using your preferred image loading library, e.g., Picasso, Glide
                Picasso.get().load(profileImageUrl).into(profileIcon);

            }
            // Set user name
            if (user != null) {
                Name.setText(user.getDisplayName());
            } else {
                Name.setText("user name");
            }
        }


        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, ProfileChange.class);
                startActivity(i);
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, ImageToText.class);
                startActivity(i);
            }
        });
        ImageView icon = findViewById(R.id.profileIcon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, ProfileChange.class);
                startActivity(i);
            }
        });
        Button history =findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, History.class);
                startActivity(i);
            }
        });

        Button seeAll = findViewById(R.id.textView5);
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(Home.this, CommunitiesMain.class);
                startActivity(i);
            }
        });

        List<RecentData> recentDataList =new ArrayList<>();
        recentDataList.add(new RecentData("milk","Baladuna","2 JOD",R.drawable.milk));
        recentDataList.add(new RecentData("Cheese","Alyaom","5 JOD",R.drawable.emptyimage));
        recentDataList.add(new RecentData("milk","Baladuna","2 JOD",R.drawable.emptyimage));
        recentDataList.add(new RecentData("milk","Baladuna","2 JOD",R.drawable.emptyimage));

        setRecentRecycler(recentDataList);

        List<CommunitiesData> communitiesDatalist=new ArrayList<>();
        communitiesDatalist.add(new CommunitiesData("General Community",R.drawable.generalfinal));
        communitiesDatalist.add(new CommunitiesData("Tree nuts free Community",R.drawable.treenuts));
        communitiesDatalist.add(new CommunitiesData("Gluten free Community",R.drawable.gluten));
        setCommunitiesRecycler(communitiesDatalist);
    }

    private void setRecentRecycler(List<RecentData> recentsDataList) {
        recentRecycler = findViewById(R.id.recent_Recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recentRecycler.setLayoutManager(layoutManager);
        recentsAdapter = new RecentsAdapter(this, recentsDataList);
        recentRecycler.setAdapter(recentsAdapter);
    }

    private void setCommunitiesRecycler(List<CommunitiesData> communitiesDatalist) {
        communitiesRecycler = findViewById(R.id.communitiesRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        communitiesRecycler.setLayoutManager(layoutManager);
        communitiesAdapter=new CommunitiesAdapter(this,communitiesDatalist);
        communitiesRecycler.setAdapter(communitiesAdapter);
    }
}