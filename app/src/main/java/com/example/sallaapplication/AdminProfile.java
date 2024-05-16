package com.example.sallaapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminProfile extends AppCompatActivity {
    EditText userName;
    EditText userEmail;
    ImageView ImgUserPhoto;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    FirebaseAuth mAuth;
    Button save, logoutBtn;
    ProgressBar progressBar;
    FirebaseUser user;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);


        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userEmail.setEnabled(false);
        ImgUserPhoto = findViewById(R.id.UserPhoto);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        save = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progress_bar);
        userId = user.getUid();
        logoutBtn = findViewById(R.id.logout);

        // Fetch and display user information
        getUserInformation();

        // Logout button click listener
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set login status to false
                setLoginStatus(false);

                // Sign out user
                mAuth.signOut();

                // Redirect to the login activity
                startActivity(new Intent(AdminProfile.this, Login.class));
                finish();
            }
        });

        // Save button click listener
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update user information
                updateUserProfile(user, pickedImgUri, userName.getText().toString());
            }
        });

        // ImageView click listener
        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });
        // Get user profile picture
        getUserProfilePicture();
    }


    private void setLoginStatus(boolean isLoggedIn) {
        SharedPreferences preferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }
    private void updateUserProfile(final FirebaseUser currentUser, @Nullable final Uri newImageUri, final String newName) {
        UserProfileChangeRequest.Builder profileUpdateBuilder = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName);

        // If a new image URI is provided, set the photo URI in the profile update
        if (newImageUri != null) {
            profileUpdateBuilder.setPhotoUri(newImageUri);
        }

        // Build the profile update request
        UserProfileChangeRequest profileUpdate = profileUpdateBuilder.build();

        // Update the user's profile
        currentUser.updateProfile(profileUpdate)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Profile update successful
                        String message = "Profile updated successfully";

                        // If both name and photo are updated, show a combined success message
                        if (newImageUri != null) {
                            message += " with new photo";
                        }

                        Toast.makeText(AdminProfile.this, message, Toast.LENGTH_SHORT).show();

                        // Update UI to reflect changes
                        getUserInformation();
                    } else {
                        // Profile update failed
                        Toast.makeText(AdminProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }

                    // Hide progress bar after updating profile
                    progressBar.setVisibility(View.GONE);
                });
    }

//    private void getUserInformation() {
//        userEmail.setText(user.getEmail());
//        userName.setText(user.getDisplayName());
//    }
    private void getUserInformation() {
        // Fetch user information from Firebase Authentication
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userName.setText(currentUser.getDisplayName());
            userEmail.setText(currentUser.getEmail());
        }
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(AdminProfile.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AdminProfile.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(AdminProfile.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(AdminProfile.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else
            openGallery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);
        }
    }
    private void getUserProfilePicture() {
        if (user.getPhotoUrl() != null) {
            Picasso.get().load(user.getPhotoUrl()).error(R.drawable.profileicon).placeholder(R.drawable.profileicon).into(ImgUserPhoto);
        } else {
            ImgUserPhoto.setImageResource(R.drawable.cameraiconbright);
        }
    }




}
