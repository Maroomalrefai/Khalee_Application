package com.example.sallaapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashDelay extends AppCompatActivity {
    private static final String PREFS_NAME = "MyAppPreferences";
    private static final String FIRST_TIME_KEY = "isFirstTime";
    ImageView logo;
    FirebaseAuth auth;
    FirebaseUser user;
    private static final long SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_delay);

        logo = findViewById(R.id.logo);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Animation rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        logo.startAnimation(rotationAnimation);

        new Handler().postDelayed(() -> {
            // Check if it's the first time the app is opened
            if (isFirstTime()) {
                startActivity(new Intent(SplashDelay.this, MainActivity.class));
                finish();
            } else {
                // If it's not the first time, check if the user is logged in
                if (isUserLoggedIn()) {
                    // If the user is logged in, check if they are an admin
                    fetchIsAdminStatus();
                } else {
                    // If the user is not logged in, show the Login activity
                    startActivity(new Intent(SplashDelay.this, Login.class));
                    finish(); // Finish SplashDelay to prevent the user from returning to it
                }
            }
        }, SPLASH_DURATION);
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(FIRST_TIME_KEY, true);
        if (isFirstTime) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_TIME_KEY, false);
            editor.apply();
        }
        return isFirstTime;
    }

    private boolean isUserLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        return preferences.getBoolean("isLoggedIn", false);
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
                        if (isAdmin) {
                            // Redirect to AdminHome if the user is an admin
                            startActivity(new Intent(SplashDelay.this, AdminHome.class));
                            finish();
                        } else {
                            // Redirect to Home if the user is not an admin
                            startActivity(new Intent(SplashDelay.this, Home.class));
                            finish();
                        }
                        finish(); // Finish SplashDelay to prevent the user from returning to it
                    } else {
                        Toast.makeText(SplashDelay.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SplashDelay.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
