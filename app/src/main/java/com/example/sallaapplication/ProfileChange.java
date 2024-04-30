package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ProfileChange extends AppCompatActivity {

    EditText edate;
    EditText userName;
    EditText userEmail;
    ImageView ImgUserPhoto;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    FirebaseAuth mAuth;
    Button save,logoutBtn;
    ProgressBar progressBar;
    FirebaseUser user ;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);
        Button editAllergyBtn = findViewById(R.id.editallergy);
        edate = findViewById(R.id.edate);
        ImgUserPhoto = findViewById(R.id.UserPhoto);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userEmail.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        save = findViewById(R.id.save_button);
        ImgUserPhoto.setImageURI(pickedImgUri);
        progressBar = findViewById(R.id.progress_bar);
        userId = user.getUid();
        logoutBtn=findViewById(R.id.logout);
        getUserInformation();



        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent =new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });
        editAllergyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileChange.this,Question.class);
                startActivity(i);
            }
        });
        edate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


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
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = userName.getText().toString();
                updateUserImage(pickedImgUri, mAuth.getCurrentUser(), newName);
            }
        });


    }
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,R.style.dialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update EditText with the selected date
                        edate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }
    private void updateUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Update user email
            userEmail.setText(user.getEmail());

            // Update user display name (username)
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                userName.setText(displayName);
            } else {
                // If display name is not set, display a placeholder
                userName.setText("No name");
            }

            Uri photoUri = user.getPhotoUrl();
            if (photoUri != null) {
                Glide.with(this)
                        .load(photoUri)
                        .error(R.drawable.profileicon) // Placeholder image in case of error
                        .into(ImgUserPhoto);
            } else {
                // If profile picture is not set, display a placeholder
                ImgUserPhoto.setImageResource(R.drawable.cameraiconbright);
            }
        }
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
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Profile update successful
                            String message = "Profile updated successfully";

                            // If both name and photo are updated, show a combined success message
                            if (newImageUri != null) {
                                message += " with new photo";
                            }

                            Toast.makeText(ProfileChange.this, message, Toast.LENGTH_SHORT).show();

                            // Update UI to reflect changes
                            updateUserInformation();
                        } else {
                            // Profile update failed
                            Toast.makeText(ProfileChange.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }

                        // Hide progress bar after updating profile
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    private void updateUserImage( Uri pickedImgUri, final FirebaseUser currentUser,String newName) {

        if (pickedImgUri != null) {
            StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("Android Tutorials").child(userId).child("profileImage");
            final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
            progressBar.setVisibility(View.VISIBLE);
            imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .setDisplayName(newName)
                                    .build();

                            currentUser.updateProfile(profileUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileChange.this, "success", Toast.LENGTH_SHORT).show();
                                                getUserInformation();
                                            }
                                            updateUserProfile(currentUser, uri, newName);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure in uploading image
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(ProfileChange.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            });
        } else {
            updateUserProfile(currentUser,null, newName);
        }
    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(ProfileChange.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileChange.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(ProfileChange.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(ProfileChange.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else
            openGallery();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUESCODE && data !=null){
            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);
//            // Generate and upload thumbnail
//            generateAndUploadThumbnail(pickedImgUri);
        }
    }
    public void getUserInformation() {
        progressBar.setVisibility(View.VISIBLE);
        userEmail.setText(user.getEmail());
        userName.setText(user.getDisplayName());
        Picasso.get().load(user.getPhotoUrl()).error(R.drawable.profileicon) .into(ImgUserPhoto);
       // Glide.with(this).load(user.getPhotoUrl()).error(R.drawable.profileicon) .into(ImgUserPhoto);
        progressBar.setVisibility(View.GONE);

    }


//    private void generateAndUploadThumbnail(Uri imageUri) {
//        try {
//            // Get the original bitmap from the image URI
//            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//
//            // Set thumbnail dimensions
//            int thumbnailWidth = 100;
//            int thumbnailHeight = 100;
//
//            // Generate thumbnail
//            Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(originalBitmap, thumbnailWidth, thumbnailHeight);
//
//            // Convert thumbnail bitmap to byte array
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] thumbnailData = baos.toByteArray();
//
//            // Upload thumbnail to Firebase Storage
//            StorageReference thumbnailRef = FirebaseStorage.getInstance().getReference().child("thumbnails").child(imageUri.getLastPathSegment());
//            thumbnailRef.putBytes(thumbnailData)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            // Thumbnail uploaded successfully
//                            // Now you can proceed to upload the original image or update user profile
//                            updateUserImage(imageUri, mAuth.getCurrentUser(), userName.getText().toString());
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Thumbnail upload failed
//                            Toast.makeText(ProfileChange.this, "Failed to upload thumbnail: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }



}
