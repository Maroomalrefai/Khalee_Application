package com.example.sallaapplication;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;
import com.adapter.CommunitiesAdapter;
import com.adapter.MyAdapter;
import com.model.CommunitiesData;
import java.util.ArrayList;
import java.util.List;


public class CommunitiesMain extends AppCompatActivity {
    RecyclerView recyclerView;
    CommunitiesAdapter adapter;
    SearchView searchView;
    List<CommunitiesData>communitiesDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communites_main);
        recyclerView = findViewById(R.id.Communities);
        searchView=findViewById(R.id.searchicon);
        searchView.clearFocus();
        communitiesDataList = new ArrayList<>();
        adapter = new CommunitiesAdapter(CommunitiesMain.this, communitiesDataList);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fileList(newText);
                return true;
            }
        });

        ImageView icon = findViewById(R.id.profileicon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CommunitiesMain.this, ProfileChange.class);
                startActivity(i);
            }
        });

        communitiesDataList.add(new CommunitiesData("   General Community", R.drawable.generalfinal));
        communitiesDataList.add(new CommunitiesData("Gluten free Community", R.drawable.gluten));
        communitiesDataList.add(new CommunitiesData("Egg free Community", R.drawable.egg));
        communitiesDataList.add(new CommunitiesData("Sesame free Community", R.drawable.sesame));
        communitiesDataList.add(new CommunitiesData("Lactose free Community", R.drawable.lactose));
        communitiesDataList.add(new CommunitiesData("Tree nuts free Community", R.drawable.treenuts));
        communitiesDataList.add(new CommunitiesData("Soy free Community", R.drawable.soy));
        communitiesDataList.add(new CommunitiesData("Peanut free Community", R.drawable.peanut));
        communitiesDataList.add(new CommunitiesData("Seafood free Community", R.drawable.seafood));
        communitiesDataList.add(new CommunitiesData("Mustard free Community", R.drawable.mustard));
        setCommunitiesRecycler(communitiesDataList);

    }

    private void fileList(String text) {
        ArrayList<CommunitiesData> filteredList= new ArrayList<>();
        for(CommunitiesData communities :communitiesDataList){
            if (communities.getCommunityName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(communities);
            }
        adapter.setFilteredList(filteredList);
        }
    }
    private void setCommunitiesRecycler(List<CommunitiesData> communitiesDatalist) {
        recyclerView = findViewById(R.id.Communities);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommunitiesAdapter(this, communitiesDatalist);
        recyclerView.setAdapter(adapter);
    }
}