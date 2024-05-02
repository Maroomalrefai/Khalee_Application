package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class detailActivity extends AppCompatActivity {
    TextView detailDesc, detailTitle, detailLang;
    ImageView detailImage;
    FloatingActionButton deleteButton;
    String key;
    String imageUrl;
    ProgressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailLang = findViewById(R.id.detailLang);
        deleteButton = findViewById(R.id.delete);
        progBar = findViewById(R.id.progBar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailLang.setText(bundle.getString("Company"));
            key = bundle.getString("Key"); // Assuming "Key" is the key in your database
            imageUrl = bundle.getString("Image");
            // load image from Picasso library
            Picasso.get().load(bundle.getString("Image")).into(detailImage);
            // Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Android Tutorials")
                        .child(userId).child("images");
                progBar.setVisibility(View.VISIBLE);
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(detailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), History.class));
                                finish();
                                progBar.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(detailActivity.this, "Failed to delete data from database", Toast.LENGTH_SHORT).show();
                                progBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(detailActivity.this, "Failed to delete image from storage", Toast.LENGTH_SHORT).show();
                        progBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

}
