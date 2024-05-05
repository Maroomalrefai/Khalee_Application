package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    AppCompatRadioButton rbLeft, rbRight;
    Button login, signUpNow;
    EditText editTextEmail, editTextPass;
    FirebaseAuth mAuth;
    //    ProgressBar progressBar;
    TextView textViewResetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rbLeft = findViewById(R.id.rbLeft);
        rbRight = findViewById(R.id.rbRight);
        login = findViewById(R.id.login);
        editTextEmail = findViewById(R.id.email);
        editTextPass = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
//        progressBar=findViewById(R.id.progressBar);
        textViewResetPass = findViewById(R.id.resetPass);

        rbRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = ((AppCompatRadioButton) view).isChecked();
                if (view.getId() == R.id.rbLeft) {
                    if (isSelected) {
                        rbLeft.setTextColor(Color.WHITE);
                        rbRight.setTextColor(Color.BLACK);
                    }
                } else if (view.getId() == R.id.rbRight) {
                    if (isSelected) {
                        rbLeft.setTextColor(Color.BLACK);
                        rbRight.setTextColor(Color.WHITE);
                    }
                }
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        textViewResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, ForgetPassword.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPass.getText());

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Ensure your email and password fields are not empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check for internet connection
                if (!isNetworkAvailable()) {
                    Toast.makeText(Login.this, "No internet connection. Failed to Log in .", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Save login status to SharedPreferences
                                    saveLoginStatus(true);
                                    Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    // Method to check for internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences preferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }
}
