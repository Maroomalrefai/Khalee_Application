package com.example.sallaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if it's the first time the app is opened
        if (isFirstTime()) {
            // If it's the first time, show the MainActivity
            setContentView(R.layout.activity_main);
        } else {
            // If it's not the first time, check if the user is logged in
            if (isUserLoggedIn()) {
                // If the user is logged in, redirect to Home activity
                startActivity(new Intent(MainActivity.this, Home.class));
                finish(); // Finish MainActivity to prevent the user from returning to it
                return;
            } else {
                // If the user is not logged in, show the Login activity
                startActivity(new Intent(MainActivity.this, Login.class));
                finish(); // Finish MainActivity to prevent the user from returning to it
                return;
            }
        }

        start = findViewById(R.id.button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
                finish(); // Finish MainActivity to prevent the user from returning to it
            }
        });
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean("isFirstTime", true);
        // Update the flag to indicate that the app has been opened before
        if (isFirstTime) {
            preferences.edit().putBoolean("isFirstTime", false).apply();
        }
        return isFirstTime;
    }

    private boolean isUserLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        return preferences.getBoolean("isLoggedIn", false);
    }
}
