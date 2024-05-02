package com.example.sallaapplication;

import androidx.annotation.Nullable;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
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
import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageActivity;
//import com.theartofdev.edmodo.cropper.CropImageView;

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
    String recognizedText=null;
    String allergy = "Egg";
    String dialogMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_text);
        inputImageBtn = findViewById(R.id.inputImageBtn);
        recognizeTextBtn = findViewById(R.id.recognizedTextBtn);
        imageIv = findViewById(R.id.imageIv);
        recognizedTextEt = findViewById(R.id.recognizedTextEt);
        recognizedTextEt.setEnabled(false);
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
                // Reset imageUri and recognizedText when selecting a new image
                imageUri = null;
                recognizedText = null;
                recognizedTextEt.setText("");
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
                }

            }
        });
    }

    private String preprocessText(String text) {
        // Clear the filteredTokens list before adding tokens from the new recognized text
        filteredTokens.clear();

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
        // Join tokens back into a string
        return String.join(" ", filteredTokens);
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
                            recognizedText = text.getText();
                            recognizedText = preprocessText(recognizedText);
                            Log.d(TAG, "onSuccess: recognizedText" + recognizedText);
                            recognizedTextEt.setText(recognizedText);
                            if (!recognizedText.isEmpty()) {
                                // Call performSearch after text recognition is successful
                                performSearch(allergy);
                            } else {
                                // Display a Toast message to inform the user
                                showDialog("Please ensure that your image contains your ingredients", R.drawable.emptyimage);
                            }
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
        Intent intent=new Intent(Intent.ACTION_PICK);// pick image from ellery
        intent.setType("image/*");
        galleryActivityResultLancher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLancher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: imageUri" + imageUri);
                        imageIv.setImageURI(imageUri);
                    } else {
                        Log.d(TAG, "onActivityResult: cancelled ");
                        Toast.makeText(ImageToText.this, "Cancelled", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    private void pickImageCamera (){
        Log.d(TAG, "pickImageCamera: ");
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Sample Title");//title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Description");//description

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
        //Permission for SDK between 23 and 29
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(ImageToText.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            }
        }
        //Permission for SDK 30 or above
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R) {
            if(!Environment.isExternalStorageManager()){
                try{
                    Intent intent= new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                    startActivityIfNeeded(intent,101);
                }catch (Exception exception){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    startActivityIfNeeded(intent,101);

                }
            }

        }

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri croppedImageUri = result.getUri();
                // Do something with the cropped image URI
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                // Handle cropping error
            }
        }
    }

//crop Image
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (resultCode == STORAGE_REQUEST_CODE) {
//                CropImage.activity(data.getData())
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(this);
//            }
//            if (resultCode == CAMERA_REQUEST_CODE) {
//                CropImage.activity(imageUri)
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(this);
//            }
//        }
//        if (resultCode==CAMERA_REQUEST_CODE){
//            CropImage.ActivityResult result=CropImage.getActivityResult(data);
//            if (resultCode==RESULT_OK){
//                Uri resultUri =result.getUri();
//                imageIv.setImageURI(resultUri);
//                BitmapDrawable bitmapDrawable=(BitmapDrawable)imageIv.getDrawable();
//                TextRecognizer recognizer = new TextRecognizer.Builder().build();
//            }
//        }
//    }

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
        // Check if recognized text is empty
        if (recognizedText.isEmpty()) {
            // If recognized text is empty, do nothing
            return ;
        }
        // Perform search within the specified allergy for each filtered token
        boolean containsAllergen = searchAllergen(allergy, filteredTokens);

        // Display result to user
        if (containsAllergen) {
            dialogMessage = "Ops! This product contains " + allergy + " and it is not suitable for you.";
            showDialog(dialogMessage,R.drawable.notfree);
        } else {
            dialogMessage = "Great! This product is free from " + allergy + ".";
            showDialog(dialogMessage, R.drawable.allergenfree);
        }
    }
    private boolean searchAllergen(String allergy, List<String> filteredTokens) {
        // Perform search within the specified allergy for each filtered token
        for (String token : filteredTokens) {
            // skip (oil,flour) conflict words
            if (token.equalsIgnoreCase("oil")|| (token.equalsIgnoreCase("flour")&&!(allergy.equalsIgnoreCase("Gluten")))) continue;            try {
                JSONArray ingredientsArray = allergenData.getJSONObject("allergens").getJSONArray(allergy);
                for (int i = 0; i < ingredientsArray.length(); i++) {
                    String jsonIngredient = ingredientsArray.getString(i).toLowerCase();


                    // Check if the preprocessed ingredient is contained in the preprocessed token
                    if (jsonIngredient.equals(token)) {
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
        dialogView.setBackground(ContextCompat.getDrawable(this, R.drawable.dialogbox));
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
                // Start uploadActivity with the selected image URI
                Intent intent = new Intent(ImageToText.this, uploadActivity.class);
                intent.putExtra("imageUri", imageUri.toString());
                intent.putExtra("dialogMessage", dialogMessage);
                startActivity(intent);
                alertDialog.dismiss();
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
