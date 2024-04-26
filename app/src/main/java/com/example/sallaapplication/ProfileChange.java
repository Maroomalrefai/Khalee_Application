package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class ProfileChange extends AppCompatActivity {

    EditText edate;
    EditText userName;
    EditText userEmail;
    ImageView ImgUserPhoto;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);
//        Button button = findViewById(R.id.editallergy);
//        edate = findViewById(R.id.edate);
        ImgUserPhoto = findViewById(R.id.UserPhoto);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);

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
    }
//    private void CreateUserAccount(String email, final String name, String password) {
//
//
//        // this method create user account with specific email and password
//
//        mAuth.createUserWithEmailAndPassword(email,password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//
//                            // user account created successfully
//                            showMessage("Account created");
//                            // after we created user account we need to update his profile picture and name
//                            updateUserInfo( name ,pickedImgUri,mAuth.getCurrentUser());
//
//
//
//                        }
//                        else
//                        {
//
//                            // account creation failed
//                            showMessage("account creation failed" + task.getException().getMessage());
//                            regBtn.setVisibility(View.VISIBLE);
//                            loadingProgress.setVisibility(View.INVISIBLE);
//
//                        }
//                    }
//                });
//
//    }

    // update user photo and name
//    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {
//
//        // first we need to upload user photo to firebase storage and get url
//
//        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
//        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
//        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                // image uploaded succesfully
//                // now we can get our image url
//
//                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//
//                        // uri contain user image url
//
//
//                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
//                                .setDisplayName(name)
//                                .setPhotoUri(uri)
//                                .build();
//
//
//                        currentUser.updateProfile(profleUpdate)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//
//                                        if (task.isSuccessful()) {
//                                            // user info updated successfully
//                                            showMessage("Register Complete");
//                                            updateUI();
//                                        }
//
//                                    }
//                                });
//
//                    }
//                });
//
//            }
//        });
//    }
//
//    private void updateUI() {
//
//        Intent homeActivity = new Intent(getApplicationContext(),Home.class);
//        startActivity(homeActivity);
//        finish();
//
//
//    }
//
//    // simple method to show toast message
//    private void showMessage(String message) {
//
//        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
//
//    }

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

        }
    }
}
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(ProfileChange.this,Question.class);
//                startActivity(i);
//            }
//        });
//        edate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDatePickerDialog();
//            }
//        });
//
//    }
//    private void showDatePickerDialog() {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                this,R.style.dialogTheme,
//                new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        // Update EditText with the selected date
//                        edate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
//                    }
//                },
//                year, month, dayOfMonth);
//
//        datePickerDialog.show();
//    }
//}