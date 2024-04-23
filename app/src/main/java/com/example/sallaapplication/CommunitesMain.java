package com.example.sallaapplication;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import com.adapter.CommunitiesAdapter;

import com.model.CommunitiesData;
import java.util.ArrayList;
import java.util.List;


public class CommunitesMain extends AppCompatActivity {
    RecyclerView communitiesRecycler;
    CommunitiesAdapter communitiesMain;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communites_main);

        List<CommunitiesData> communitiesDatalist=new ArrayList<>();
        communitiesDatalist.add(new CommunitiesData("Khalee Community",R.drawable.generalmain));
        communitiesDatalist.add(new CommunitiesData("Tree Nut Community",R.drawable.treenutsmain));
        communitiesDatalist.add(new CommunitiesData("Gluten Community",R.drawable.glutenmain));
        communitiesDatalist.add(new CommunitiesData("Lactose Community",R.drawable.lactosemain));
        communitiesDatalist.add(new CommunitiesData("Seafood Community",R.drawable.seafoodmain));
        communitiesDatalist.add(new CommunitiesData("Sesame Community",R.drawable.sesamemain));
        communitiesDatalist.add(new CommunitiesData("   Egg Community",R.drawable.eggmain));
        communitiesDatalist.add(new CommunitiesData("   Soy Community",R.drawable.soymain));
        communitiesDatalist.add(new CommunitiesData("Mustard  Community",R.drawable.mustardmain));
        communitiesDatalist.add(new CommunitiesData("Peanut Community",R.drawable.peanutmain));
        communitiesDatalist.add(new CommunitiesData("",R.drawable.white));
        setCommunitiesRecycler(communitiesDatalist);
    }

    private void setCommunitiesRecycler(List<CommunitiesData> communitiesDatalist) {
        communitiesRecycler = findViewById(R.id.recyclerViewCommunities);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, communitiesRecycler.VERTICAL, false);
        communitiesRecycler.setLayoutManager(layoutManager);
        communitiesMain=new CommunitiesAdapter(this,communitiesDatalist);
        communitiesRecycler.setAdapter(communitiesMain);
    }

}