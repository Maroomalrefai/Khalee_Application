package com.example.sallaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.RecentsAdapter;
import com.model.RecentData;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    RecyclerView recentRecycler;
    RecentsAdapter recentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        List<RecentData> recentDataList =new ArrayList<>();
        recentDataList.add(new RecentData("milk","Baladuna","2 JOD",R.drawable.milk));
        recentDataList.add(new RecentData("Cheese","Alyaom","5 JOD",R.drawable.ok));
        recentDataList.add(new RecentData("milk","Baladuna","2 JOD",R.drawable.good));
        recentDataList.add(new RecentData("milk","Baladuna","2 JOD",R.drawable.notok));

        setRecentRecycler(recentDataList);

    }

    private void setRecentRecycler(List<RecentData> recentsDataList) {
        recentRecycler = findViewById(R.id.recent_Recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recentRecycler.setLayoutManager(layoutManager);
        recentsAdapter = new RecentsAdapter(this, recentsDataList);
        recentRecycler.setAdapter(recentsAdapter);
    }
}