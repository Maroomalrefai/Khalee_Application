package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adapter.ProductsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.model.ProductData;

import java.util.ArrayList;
import java.util.List;

public class ViewProducts extends AppCompatActivity {
    FloatingActionButton addButton;
    RecyclerView recyclerView;
    List<ProductData> productDataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    ProgressBar progressBar;
    ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);
        addButton = findViewById(R.id.add);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewProducts.this, 2); // 2 columns
        recyclerView.setLayoutManager(gridLayoutManager);

        productDataList = new ArrayList<>();
        adapter = new ProductsAdapter(ViewProducts.this, productDataList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        progressBar.setVisibility(View.VISIBLE);

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
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewProducts.this, "Failed to retrieve data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProducts.this, AddProducts.class);
                startActivity(intent);
            }
        });
    }
}
