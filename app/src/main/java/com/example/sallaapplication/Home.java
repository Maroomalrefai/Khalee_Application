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
import com.adapter.ProductsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.model.CommunitiesData;
import com.model.ProductData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    RecyclerView recentRecycler;
    TextView Name;
    ImageView profileIcon;
    ProductsAdapter productsAdapter;
    RecyclerView communitiesRecycler;
    CommunitiesAdapter communitiesAdapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageView scan = findViewById(R.id.recycle_bin);
        Name = findViewById(R.id.name);
        profileIcon = findViewById(R.id.profileIcon);

        // Set profile image and user name if available
        if (user != null) {
            // Set profile image
            String profileImageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            if (profileImageUrl != null) {
                // Load profile image using Picasso
                Picasso.get().load(profileImageUrl).placeholder(R.drawable.profileicon).into(profileIcon);
            }
            // Set user name
            if (user.getDisplayName() != null) {
                Name.setText(user.getDisplayName());
            } else {
                Name.setText("User name");
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

        Button history = findViewById(R.id.history);
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
                Intent i = new Intent(Home.this, CommunitiesMain.class);
                startActivity(i);
            }
        });

        fetchIsAdminStatus();

    }

    private void setRecentRecycler() {
        List<ProductData> productDataList = new ArrayList<>();
//        productDataList.add(new ProductData("Lactose free milk", "Baladna", R.drawable.milk, "https://www.baladna.com.jo/"));
//        productDataList.add(new ProductData("Cheese", "AlMazraa", R.drawable.cheese, "https://mazraadairy.com/"));
//        productDataList.add(new ProductData("Flax seeds bread", "REEF", R.drawable.flaxseeds, "https://reef-bakeries.com/"));
//        productDataList.add(new ProductData("Coconut bread", "Leeds", R.drawable.coconutbread, "https://leeds-bakery.com/"));
//        productDataList.add(new ProductData("Lababa", "Al Youm", R.drawable.labaneh, "https://samajordan.jo/en/brands/alyoum-food"));
//        productDataList.add(new ProductData("Yogurt", "Maha", R.drawable.mahayogurt, "https://jordandairy.com/"));
//        productDataList.add(new ProductData("Popcorn", "Kasih", R.drawable.popcorn, "https://www.kasih.com/"));
//        productDataList.add(new ProductData("Beans", "Kasih", R.drawable.beans, "https://www.kasih.com/"));
//        productDataList.add(new ProductData("Chocolate", "Today", R.drawable.chcolate, "http://www.todaychocolate.com/"));

        recentRecycler = findViewById(R.id.recent_Recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recentRecycler.setLayoutManager(layoutManager);
        productsAdapter = new ProductsAdapter(this, productDataList);
        recentRecycler.setAdapter(productsAdapter);
    }

    private void setCommunitiesRecycler(boolean isAdmin) {
        List<CommunitiesData> communitiesDatalist = new ArrayList<>();
        communitiesDatalist.add(new CommunitiesData("General Community", "General", R.drawable.generalfinal));
        communitiesDatalist.add(new CommunitiesData("Tree nuts free Community", "Nut", R.drawable.treenuts));
        communitiesDatalist.add(new CommunitiesData("Gluten free Community", "Gluten", R.drawable.gluten));

        communitiesRecycler = findViewById(R.id.communitiesRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        communitiesRecycler.setLayoutManager(layoutManager);

        // Create an instance of CommunitiesAdapter with the determined isAdmin status
        communitiesAdapter = new CommunitiesAdapter(this, communitiesDatalist, isAdmin);
        communitiesRecycler.setAdapter(communitiesAdapter);
    }

    private void fetchIsAdminStatus() {
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("Khalee_Users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        boolean isAdmin = document.getBoolean("isAdmin") != null && document.getBoolean("isAdmin");
                        setCommunitiesRecycler(isAdmin);
                    } else {
                        Toast.makeText(Home.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Home.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
