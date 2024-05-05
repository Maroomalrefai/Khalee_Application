package com.example.sallaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


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
                Picasso.get().load(profileImageUrl).placeholder(R.drawable.profileicon).into(profileIcon);

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
        recentDataList.add(new RecentData("lactose free milk","Baladna",R.drawable.milk,"https://www.baladna.com.jo/"));
        recentDataList.add(new RecentData("Cheese","AlMazraa",R.drawable.cheese,"https://mazraadairy.com/"));
        recentDataList.add(new RecentData("Flax seeds bread","REEF",R.drawable.flaxseeds,"https://reef-bakeries.com/"));
        recentDataList.add(new RecentData("Coconut bread ","Leeds",R.drawable.coconutbread,"https://leeds-bakery.com/"));
        recentDataList.add(new RecentData("Lababa","Al Youm",R.drawable.labaneh,"https://samajordan.jo/en/brands/alyoum-food"));
        recentDataList.add(new RecentData("Yogurt","Maha",R.drawable.mahayogurt,"https://jordandairy.com/"));
        recentDataList.add(new RecentData("Popcorn","Kasih",R.drawable.popcorn,"https://www.kasih.com/"));
        recentDataList.add(new RecentData("Beans","Kasih",R.drawable.beans,"https://www.kasih.com/"));
        recentDataList.add(new RecentData("Chocolate","Today",R.drawable.chcolate,"http://www.todaychocolate.com/"));
        setRecentRecycler(recentDataList);

        List<CommunitiesData> communitiesDatalist=new ArrayList<>();
        communitiesDatalist.add(new CommunitiesData("General Community","General",R.drawable.generalfinal));
        communitiesDatalist.add(new CommunitiesData("Tree nuts free Community","Nut",R.drawable.treenuts));
        communitiesDatalist.add(new CommunitiesData("Gluten free Community","Gluten",R.drawable.gluten));
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