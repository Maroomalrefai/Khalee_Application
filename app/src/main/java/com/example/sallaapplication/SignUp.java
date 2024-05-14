package com.example.sallaapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.jetbrains.annotations.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class SignUp extends AppCompatActivity {
    Button signUp;
    ImageView google;
    EditText editTextEmail, editTextPass, editTextUsername;
    FirebaseAuth mAuth;
    AppCompatRadioButton rbLeft, rbRight;
    TextInputLayout passwordInputLayout;
    TextView passwordFeedbackTextView;
    private static final int REQUEST_CODE_SPLASH = 100;

    private GoogleSignInClient googleSignInClient;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            // Handling result of Google Sign-In
            if (result.getResultCode() == RESULT_OK) {
                // Get Google account information
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    // Get Google sign-in credential
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    // Sign in with Google credential
                    mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Google Sign-In successful, proceed with account creation
                                Toast.makeText(SignUp.this, "Account created.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Question.class);
                                startActivity(intent);
                            } else {
                                // Google Sign-In failed
                                Toast.makeText(SignUp.this, "Failed to sign in with Google:" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (ApiException e) {
                    // Google Sign-In failed due to an exception
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        rbLeft = findViewById(R.id.rbLeft);
        rbRight = findViewById(R.id.rbRight);
        signUp = findViewById(R.id.signUp);
        editTextEmail = findViewById(R.id.email);
        editTextPass = findViewById(R.id.password);
        editTextUsername = findViewById(R.id.username);
        google = findViewById(R.id.google);
        mAuth = FirebaseAuth.getInstance();
        passwordInputLayout = findViewById(R.id.passwordTextInputLayout);
        passwordFeedbackTextView = findViewById(R.id.passwordFeedbackTextView);
        int minPassLength = 6;
        FirebaseApp.initializeApp(this);

        // Configure Google Sign-In
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(SignUp.this, options);

        // Set click listener for the Google sign-in button
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Google Sign-In intent
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });
        // Set click listener for the left and right radio buttons
        rbLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Change text color based on selection
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
                // Navigate to login activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        // Set click listener for the sign-up button
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user-entered email, password, and username
                String email = editTextEmail.getText().toString();
                String password = editTextPass.getText().toString();
                String name = editTextUsername.getText().toString();

                // Validate input fields
                if (TextUtils.isEmpty(email)) {
                    showToast("Please enter your email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    showToast("Please enter your password");
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    showToast("Please enter your username");
                    return;
                }
                // Check for internet connection
                if (!isNetworkAvailable()) {
                    showToast("No internet connection. Failed to Sign up.");
                    return;
                }

                // Create user with email and password
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Set the display name for the user
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null && user.isEmailVerified()) {
                                        // If the email is verified, navigate to the Question activity
                                        startActivity(new Intent(SignUp.this, Question.class));
                                        finish(); // Finish the SignUp activity
                                    }
                                    if (user != null) {
                                        user.updateProfile(new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .build());
                                    }
                                    // Send email verification
                                    sendEmailVerification();
                                } else {
                                    // User registration failed
                                    showToast("Authentication failed.");
                                }
                            }
                        });
            }
        });
    }
    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        showToast("Verification email sent. Please verify your email.");

                        // Check email verification status periodically
                        checkEmailVerificationStatus();
                    } else {
                        showToast("Failed to send verification email.");
                    }
                }
            });
        }
    }

    // Method to periodically check email verification status
    private void checkEmailVerificationStatus() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Schedule a timer to check the email verification status periodically
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    user.reload(); // Reload the user data to get the updated email verification status
                    if (user.isEmailVerified()) {
                        // If email is verified, navigate to the Question activity
                        startActivity(new Intent(SignUp.this, Question.class));
                        finish(); // Finish the SignUp activity
                        cancel(); // Cancel the timer
                    }
                }
            }, 0, 1000); // Check every second
        }
    }



    // Helper method to show toast messages
    private void showToast(String message) {
        Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
    }

    // Method to check for internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}