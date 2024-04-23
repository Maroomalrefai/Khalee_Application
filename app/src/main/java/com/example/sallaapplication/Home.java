package com.example.sallaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.CommunitiesAdapter;
import com.adapter.RecentsAdapter;
import com.model.CommunitiesData;
import com.model.RecentData;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    RecyclerView recentRecycler;
    RecentsAdapter recentsAdapter;
    RecyclerView communitiesRecycler;
    CommunitiesAdapter communitiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView scan=findViewById(R.id.recycle_bin);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, ImageToText.class);
                startActivity(i);
            }
        });
        Button seeAll =findViewById(R.id.textView5);
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, CommunitesMain.class);
                startActivity(i);
            }
        });
        Button history =findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, ImageToText.class);
                startActivity(i);
            }
        });

        List<RecentData> recentDataList =new ArrayList<>();
        recentDataList.add(new RecentData("milk","Baladuna","2 JOD",R.drawable.milk));
        recentDataList.add(new RecentData("Cheese","Alyaom","5 JOD",R.drawable.ok));
        recentDataList.add(new RecentData("milk","Baladuna","2 JOD",R.drawable.good));
        recentDataList.add(new RecentData("milk","Baladuna","2 JOD",R.drawable.notok));

        setRecentRecycler(recentDataList);

        List<CommunitiesData> communitiesDatalist=new ArrayList<>();
        communitiesDatalist.add(new CommunitiesData("General Community",R.drawable.general));
        communitiesDatalist.add(new CommunitiesData("Tree Nut Community",R.drawable.treenuts));
        communitiesDatalist.add(new CommunitiesData("Gluten Community",R.drawable.gluten));
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, communitiesRecycler.HORIZONTAL, false);
        communitiesRecycler.setLayoutManager(layoutManager);
        communitiesAdapter=new CommunitiesAdapter(this,communitiesDatalist);
        communitiesRecycler.setAdapter(communitiesAdapter);
    }
}