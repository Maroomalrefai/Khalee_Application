package com.example.sallaapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashDelay extends AppCompatActivity {
    ImageView logo;
    private static final long SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_delay);
        logo = findViewById(R.id.logo);
        Animation rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        logo.startAnimation(rotationAnimation);

        // Delay for SPLASH_DURATION milliseconds and then start the MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashDelay.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish the splash activity to prevent the user from returning to it
            }
        }, SPLASH_DURATION);
    }
}
