package com.example.sallaapplication;

import androidx.annotation.NonNull;
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
import com.adapter.PostAdapter;
import com.adapter.ProductsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    String profileImageUrl;
    ProductsAdapter productsAdapter;
    RecyclerView communitiesRecycler;
    CommunitiesAdapter communitiesAdapter;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    List<ProductData> productDataList = new ArrayList<>(); // Initialize here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        ImageView scan = findViewById(R.id.recycle_bin);
        Name = findViewById(R.id.name);
        profileIcon = findViewById(R.id.profileIcon);
        recentRecycler = findViewById(R.id.recent_Recycle);

        // Set profile image and user name if available
        if (user != null) {
            // Set profile image
            profileImageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            if (profileImageUrl != null) {
                // Load profile image using your preferred image loading library, e.g., Picasso, Glide
                Picasso.get().load(profileImageUrl).placeholder(R.drawable.profileicon).into(profileIcon);
            }
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

        // Fetch products from database
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        fetchIsAdminStatus();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productDataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ProductData product = itemSnapshot.getValue(ProductData.class);
                    if (product != null) {
                        productDataList.add(product);
                    }
                }
                // Set up the RecyclerView here after data is fetched
                setRecentRecycler();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Failed to retrieve data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecentRecycler() {
        // Set up the RecyclerView and Adapter
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recentRecycler.setLayoutManager(layoutManager);
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
                        setupProductAdapter(isAdmin);
                    } else {
                        Toast.makeText(Home.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Home.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void setupProductAdapter(boolean isAdmin) {
        productsAdapter = new ProductsAdapter(Home.this, productDataList, isAdmin);
        recentRecycler.setAdapter(productsAdapter);
    }
}
//        productDataList.add(new ProductData("Lactose free milk", "Baladna", R.drawable.milk, "https://www.baladna.com.jo/"));
//        productDataList.add(new ProductData("Cheese", "AlMazraa", R.drawable.cheese, "https://mazraadairy.com/"));
//        productDataList.add(new ProductData("Flax seeds bread", "REEF", R.drawable.flaxseeds, "https://reef-bakeries.com/"));
//        productDataList.add(new ProductData("Coconut bread", "Leeds", R.drawable.coconutbread, "https://leeds-bakery.com/"));
//        productDataList.add(new ProductData("Lababa", "Al Youm", R.drawable.labaneh, "https://samajordan.jo/en/brands/alyoum-food"));
//        productDataList.add(new ProductData("Yogurt", "Maha", R.drawable.mahayogurt, "https://jordandairy.com/"));
//        productDataList.add(new ProductData("Popcorn", "Kasih", R.drawable.popcorn, "https://www.kasih.com/"));
//        productDataList.add(new ProductData("Beans", "Kasih", R.drawable.beans, "https://www.kasih.com/"));
//        productDataList.add(new ProductData("Chocolate", "Today", R.drawable.chcolate, "http://www.todaychocolate.com/"));
