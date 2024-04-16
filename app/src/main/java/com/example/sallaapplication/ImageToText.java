package com.example.sallaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageToText extends AppCompatActivity {
    private MaterialButton inputImageBtn;
    private MaterialButton recognizeTextBtn;
    private ShapeableImageView imageIv;
    private EditText recognizedTextEt;
    private static final String TAG = "MAIN_TAG";
    private Uri imageUri = null ;
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=101;
    private String[] cameraPermission;
    private String [] storagePermission;
    private ProgressDialog progressDialog;
    private TextRecognizer textRecognizer;
    JSONObject allergenData;
    List<String> filteredTokens;
    String recognizedText;
    String allergy = "Peanut";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_text);
        inputImageBtn = findViewById(R.id.inputImageBtn);
        recognizeTextBtn = findViewById(R.id.recognizedTextBtn);
        imageIv = findViewById(R.id.imageIv);
        recognizedTextEt = findViewById(R.id.recognizedTextEt);
        cameraPermission = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        filteredTokens = new ArrayList<>();

    // Load allergen data from assets
        loadAllergenData();
        inputImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputImageDialog();
            }

        });
        recognizeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null) {
                    Toast.makeText(ImageToText.this, "Pick image first", Toast.LENGTH_SHORT).show();
                } else {
                    recognizeTextFromImage();
                    // Perform search
                    performSearch(allergy);
                }

            }
        });
    }

    private void preprocessText(String text) {
        // Convert the text to lowercase
        String lowercaseText = text.toLowerCase();

        // Remove any leading or trailing whitespace
        String trimmedText = lowercaseText.trim();

        // Remove punctuation
        String textWithoutPunctuation = trimmedText.replaceAll("[^a-zA-Z\\s,]", "");

        // Tokenization based on both whitespace and commas
        String[] tokens = textWithoutPunctuation.split("[\\s,]+");

        // Remove stopwords
        List<String> stopwords = Arrays.asList("and", "or", "the", "is", "it", "on", "in", "with");
        for (String token : tokens) {
            if (!stopwords.contains(token)) {
                filteredTokens.add(token);
            }
        }
    }

    private void recognizeTextFromImage() {
        Log.d(TAG,"recognizeTextFromImage:");
        progressDialog.setMessage("Preparing image");
        progressDialog.show();

        try{
            InputImage inputImage=InputImage.fromFilePath(this,imageUri);
            progressDialog.setMessage("Recognizing text");
            Task<Text> textTaskResult=textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            progressDialog.dismiss();
                            recognizedText=text.getText();
                            preprocessText(recognizedText);
                            Log.d(TAG, "onSuccess: recognizedText"+recognizedText);
                            recognizedTextEt.setText(recognizedText);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Log.e(TAG, "onFailure:",e);
                            Toast.makeText(ImageToText.this,"Failed recognizing text due to"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (Exception e){
            progressDialog.dismiss();
            Log.e(TAG, "recognizeTextFromImage: ",e);
            Toast.makeText(this,"Failed preparing image due to"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void showInputImageDialog(){
        PopupMenu popupMenu = new PopupMenu(this,inputImageBtn);
        popupMenu.getMenu().add(Menu.NONE,1,1,"CAMERA");
        popupMenu.getMenu().add(Menu.NONE,2,2,"GALLERY");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id=menuItem.getItemId();
                if (id == 1){
                    Log.d(TAG, "onMenuItemClick: Camera Clicked");
                    if (checkCameraPermission()){
                        pickImageCamera();
                    }
                    else {
                        requestCameraPermission();
                    }
                }
                else if (id == 2) {
                    Log.d(TAG, "onMenuItemClick:Gallery Clicked ");
//                    Gallery is clicked, check if storage permission is granted or not
                    if(checkStoragePermission()){
                        pickImageGallery();
                    }
                    else{
                        requestStoragePermission();
                    }
                }
                return true;
            }
        });
    }

    private void pickImageGallery(){
        Log.d(TAG, "pickImageGallery: ");
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLancher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLancher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== Activity.RESULT_OK){
                        Intent data =result.getData();
                        imageUri=data.getData();
                        Log.d(TAG, "onActivityResult:imageUri"+imageUri);
                        imageIv.setImageURI(imageUri);
                    }
                    else {
                        Log.d(TAG, "onActivityResult:cancelled ");
                        Toast.makeText(ImageToText.this,"Cancelled",Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    private void pickImageCamera (){
        Log.d(TAG, "pickImageCamera: ");
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Sample Title");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Description");

        imageUri= getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==Activity.RESULT_OK){
                        Log.d(TAG, "onActivityResult:imageUri " +imageUri);
                        imageIv.setImageURI(imageUri);
                    }
                    else{
                        Log.d(TAG, "onActivityResult: cancelled");
                        Toast.makeText(ImageToText.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkStoragePermission(){
//        check if storage permissions are allowed or not
//        return true if allowed , false if not allowed
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
//        request storage permission (for gallery image pick)
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean cameraResult = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean storageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return cameraResult && storageResult;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageAccepted){
                        pickImageCamera();
                    }
                    else {
                        Toast.makeText(this, "Camera and Storage permission are requires", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
//                check if some action from permission dialog performed or not Allow/Deny
                if(grantResults.length>0)
                {
//                    check if storage permissions granted, contains boolean results either ture or flase
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
//                   check if storage permission is grantd or not
                    if(storageAccepted){
//                        storage permission granted, we can launch gallery intent
                        pickImageGallery();
                    }
                    else {
//                        storage permission denied , can't launch gallery intent
                        Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    private void saveData() {
        if (imageUri != null) {
            // Show progress dialog
            progressDialog.setTitle("save to history");
            progressDialog.show();

            // Get the current user's ID (replace this with your actual way of getting the user ID)
            String userId = getCurrentUserId();
            // Get reference to Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Android Images").child(userId);

            // Create a reference to 'images/<FILENAME>'
            StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

            // Upload file to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL of the uploaded image
                        Task<Uri> downloadUrl = imageRef.getDownloadUrl();
                        downloadUrl.addOnSuccessListener(uri -> {
                            // Image uploaded successfully, get the download URL
                            String imageUrl = uri.toString();
                            // Now, you can save the imageURL to your history database
                            // For demonstration purposes, let's assume you have a HistoryData class
                            // where you store image URLs and recognized text.
                            // You can replace this with your actual database code.
                            ImageModel historyData = new ImageModel(imageUrl);
                            // Save historyData to your database (e.g., Firebase Realtime Database or Firestore)
                            // For example:
                            FirebaseDatabase.getInstance().getReference("history").push().setValue(historyData);
//
                            // Dismiss the progress dialog
                            progressDialog.dismiss();

                            // Show a success message
                            Toast.makeText(ImageToText.this, "Image saved to history", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        progressDialog.dismiss();
                        Toast.makeText(ImageToText.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // If imageUri is null, show a message
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
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

    private void loadAllergenData() {
        // Load JSON data from assets
        try {
            StringBuilder stringBuilder = new StringBuilder();
            InputStream inputStream=getAssets().open("allergyData.json");
            int size=inputStream.available();
            byte[] buffer= new byte[size];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                stringBuilder.append(new String(buffer, 0, bytesRead));
            }
            inputStream.close();

            // Parse JSON data
            allergenData = new JSONObject(stringBuilder.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    private void performSearch(String allergy) {
        // Perform search within the specified allergy for each filtered token
        boolean containsAllergen = searchAllergen(allergy, filteredTokens);

        // Display result to user
        if (containsAllergen) {
            showDialog("Ops! This product contains " + allergy + " and  it is not suitable for you.",R.drawable.notok);
        } else {
            showDialog("Great! This product is free from " + allergy + ".",R.drawable.ok);
        }
    }
    private boolean searchAllergen(String allergy, List<String> filteredTokens) {
        // Perform search within the specified allergy for each filtered token
        for (String token : filteredTokens) {
            try {
                JSONArray ingredientsArray = allergenData.getJSONObject("allergens").getJSONArray(allergy);
                for (int i = 0; i < ingredientsArray.length(); i++) {
                    String jsonIngredient = ingredientsArray.getString(i).toLowerCase();

                    // Check if the preprocessed ingredient is contained in the preprocessed token
                    if (jsonIngredient.contains(token)) {
                        return true; // Allergen found for this token
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // If no allergen found for any token, return false
        return false;
    }

    private void showDialog(String message, int imageResourceId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        dialogView.setBackground(ContextCompat.getDrawable(this, R.drawable.dialogBox));
        builder.setView(dialogView);
        ImageView imageView = dialogView.findViewById(R.id.dialog_image_view);
        TextView textView = dialogView.findViewById(R.id.dialog_text_view);
        Button savedImage = dialogView.findViewById(R.id.saveImageBtn); // Corrected to dialogView
        Button closeButton = dialogView.findViewById(R.id.close); // Close button added

        imageView.setImageResource(imageResourceId);
        textView.setText(message);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set background to transparent
        alertDialog.show();

        savedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); // Dismiss the dialog when the close button is clicked
            }
        });
    }
}
