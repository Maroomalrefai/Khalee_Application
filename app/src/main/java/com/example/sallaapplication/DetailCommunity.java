package com.example.sallaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailCommunity extends AppCompatActivity {
    FloatingActionButton addButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_community);
        addButton = findViewById(R.id.add);
        // open creat post page when add is clicked
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the action to be taken when the FloatingActionButton is clicked
                Intent intent = new Intent(DetailCommunity.this, CreatPost.class); // Change CurrentActivity and NewActivity to your actual activity names
                startActivity(intent);
            }
        });
    }
}