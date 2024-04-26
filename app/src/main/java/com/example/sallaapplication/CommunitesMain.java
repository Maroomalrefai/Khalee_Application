package com.example.sallaapplication;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.adapter.CommunitiesAdapter;
import com.model.CommunitiesData;

import java.util.ArrayList;
import java.util.List;


public class CommunitesMain extends AppCompatActivity {
    RecyclerView recyclerView;
    CommunitiesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communites_main);
     recyclerView = findViewById(R.id.Communities);

     List <CommunitiesData> communitiesDataList = new ArrayList<>();
     communitiesDataList.add(new CommunitiesData("General Community",R.drawable.generalfinal));
     communitiesDataList.add(new CommunitiesData("Gluten free Community",R.drawable.gluten));
     communitiesDataList.add(new CommunitiesData("Egg free Community",R.drawable.egg));
        communitiesDataList.add(new CommunitiesData("Sesame free Community",R.drawable.sesame));
        communitiesDataList.add(new CommunitiesData("Lactose free Community",R.drawable.lactose));
        communitiesDataList.add(new CommunitiesData("Tree nuts free Community",R.drawable.treenuts));
        communitiesDataList.add(new CommunitiesData("Soy free Community",R.drawable.soy));
        communitiesDataList.add(new CommunitiesData("Peanut free Community",R.drawable.peanut));
        communitiesDataList.add(new CommunitiesData("Seafood free Community",R.drawable.seafood));
        communitiesDataList.add(new CommunitiesData("Mustard free Community",R.drawable.mustard));
        setCommunitiesRecycler(communitiesDataList);
    }
    private void setCommunitiesRecycler(List<CommunitiesData> communitiesDatalist) {
        recyclerView = findViewById(R.id.Communities);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new CommunitiesAdapter(this,communitiesDatalist);
        recyclerView.setAdapter(adapter);
    }
}




