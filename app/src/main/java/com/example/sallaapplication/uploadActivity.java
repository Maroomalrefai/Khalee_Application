package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.model.HistoryData;

import java.text.DateFormat;
import java.util.Calendar;

public class uploadActivity extends AppCompatActivity {

    ImageView uploadImage;
    Button saveButton;
    EditText uploadTopic, uploadDesc, uploadLang;
    String imageURL;
    Uri uri;
    ProgressBar progressBar; // Add ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadImage = findViewById(R.id.uploadImage);
        uploadDesc = findViewById(R.id.uploadDesc);
        uploadDesc.setEnabled(false);

        uploadTopic = findViewById(R.id.uploadTopic);
        uploadLang = findViewById(R.id.uploadLang);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar); // Initialize ProgressBar

        String uriString = getIntent().getStringExtra("imageUri");
        uri = Uri.parse(uriString); // Convert the string back to Uri
        uploadImage.setImageURI(uri); // Set the image in ImageView

        String dialogMessage = getIntent().getStringExtra("dialogMessage");
        uploadDesc.setText(dialogMessage);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            // Handle the case when the user is not signed in
            return null;
        }
    }
    public void saveData(){

        // Get the text from the fields
        String topic = uploadTopic.getText().toString().trim();
        String lang = uploadLang.getText().toString().trim();
        if (topic.isEmpty() || lang.isEmpty()) {
            // Show a toast message indicating that the fields are required
            Toast.makeText(uploadActivity.this, "Product and Company names are required", Toast.LENGTH_SHORT).show();
            return; // Exit the method without proceeding further
        }

        String userId = getCurrentUserId();
        if (userId != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Tutorials").child(userId).child("images")
                    .child(uri.getLastPathSegment());


            progressBar.setVisibility(View.VISIBLE); // Show ProgressBar

            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    uriTask.addOnSuccessListener(uri -> {
                        imageURL = uri.toString();
                        uploadData(userId);
                        progressBar.setVisibility(View.GONE); // Hide ProgressBar
                        Intent intent = new Intent(uploadActivity.this, History.class);
                        startActivity(intent);
                        finish();
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE); // Hide ProgressBar
                    Toast.makeText(uploadActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else{
                Toast.makeText(uploadActivity.this, "User ID not available", Toast.LENGTH_SHORT).show();
            }
    }

    public void uploadData(String userId){
        String title = uploadTopic.getText().toString();
        String desc = uploadDesc.getText().toString();
        String lang = uploadLang.getText().toString();

        HistoryData dataClass = new HistoryData(title, desc, lang, imageURL);

        FirebaseDatabase.getInstance().getReference("Android Tutorials").child(userId).child("images").child(title)
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(uploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(uploadActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}