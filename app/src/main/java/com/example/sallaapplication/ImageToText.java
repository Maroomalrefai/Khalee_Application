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
import android.text.TextUtils;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.model.HistoryData;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
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
    private static final int CAMERA_REQUEST_CODE = 100;
    private Uri imageUri;

    private static final int STORAGE_REQUEST_CODE=101;
    private String[] cameraPermission;
    private String [] storagePermission;
    private ProgressDialog progressDialog;
    private TextRecognizer textRecognizer;
    JSONObject allergenData;
    List<String> filteredTokens;
    String recognizedText=null;
    ValueEventListener eventListener;
    List<String> allergies;
    DatabaseReference databaseReference;
    List<String> ingredients;
    String dialogMessage;
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;


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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userId = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("allergies");
        allergies = new ArrayList<>();
        ingredients = new ArrayList<>();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               allergies.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String allergyName = snapshot.getKey().toString();
                    boolean isAllergic = snapshot.getValue(Boolean.class);
                    if (isAllergic) {
                        allergies.add(allergyName);
                    }
                }
                // Now you have a list of allergies for the user
                Log.d("allergies", "User's allergies: " + allergies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Log.e("allergies", "Failed to read user's allergies.", databaseError.toException());
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("ingredients" );
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               ingredients.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String SingleIngredient = snapshot.getKey().toString();
                    boolean isChosen = snapshot.getValue(Boolean.class);
                    if (isChosen) {
                        ingredients.add(SingleIngredient);
                    }
                }
                // Now you have a list of allergies for the user
                Log.d("avoid ingredients", "User's ingredients list: " + ingredients);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
                Log.e("ingredient", "Failed to retrieve value.", databaseError.toException());
            }
        });

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
                }

            }
        });




// Initialize the galleryActivityResultLauncher
        galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                imageUri = data.getData();
                                Log.d(TAG, "Image URI from Gallery: " + imageUri.toString());
                                startCropActivity(imageUri);
                            } else {
                                Log.e(TAG, "Gallery data is null");
                                Toast.makeText(ImageToText.this, "Failed to get image from gallery", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ImageToText.this, "Cancelled", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );;

        // Initialize the cameraActivityResultLauncher
        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            if (imageUri != null) {
                                Log.d(TAG, "Image URI from Camera: " + imageUri.toString());
                                imageIv.setImageURI(imageUri);
                            } else {
                                Log.e(TAG, "Camera imageUri is null");
                                Toast.makeText(ImageToText.this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ImageToText.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private String preprocessText(String text) {
        // Clear the filteredTokens list before adding tokens from the new recognized text
        filteredTokens.clear();

        // Convert the text to lowercase and trim any leading or trailing whitespace
        String trimmedText = text.toLowerCase().trim();

        // Tokenization based on non-alphabetical characters
        String[] tokens = trimmedText.split("[^a-zA-Z]+");

        // Remove stopwords
        List<String> stopwords = Arrays.asList("and", "or", "the", "is", "it", "on", "in", "with", "from", "made", "contain");
        for (String token : tokens) {
            String trimmedToken = token.trim(); // Trimming each token
            if (!stopwords.contains(trimmedToken) && !trimmedToken.isEmpty()) {
                filteredTokens.add(trimmedToken); // Using trimmedToken instead of token.trim()
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
                            recognizedTextEt.setText(filteredTokens.toString());
                            if (!recognizedText.isEmpty()) {
                                // Call performSearch after text recognition is successful
                                performSearch(allergies);
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

    private void showInputImageDialog() {
        //items to display in dialog
        PopupMenu popupMenu = new PopupMenu(this, inputImageBtn);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "CAMERA");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "GALLERY");
        popupMenu.show();//show dialog
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                //camera option clicked
                if (id == 1) {
                    if (checkCameraPermission()) {
                        pickImageCamera();
                    } else {
                        requestCameraPermission();
                    }
                    //gallery option clicked
                } else if (id == 2) {
                    if (checkStoragePermission()) {
                        pickImageGallery();
                    } else {
                        requestStoragePermission();
                    }
                }
                return true;
            }
        });
    }
    private void pickImageGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set intent type to image
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }


    private void pickImageCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Sample Title");//title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void startCropActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .start(this);
    }
//handle image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Start CropImage activity with the captured image URI
            startCropActivity(imageUri);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri croppedImageUri = result.getUri();
                // Do something with the cropped image URI
                // Update imageUri to the URI of the cropped image
                imageUri = croppedImageUri;
                imageIv.setImageURI(croppedImageUri); // Example: Display the cropped image
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                // Handle cropping error
                Log.e(TAG, "Crop Error: ", error);
            }
        }
    }

    private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
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
//handle permission result
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
    private void performSearch(List<String> allergies) {
        // Continue with the existing logic
        boolean containsAllergen = false;
        List<String> foundIngredients = new ArrayList<>();
        if (allergies != null) {
            for (String allergy : allergies) {
                String foundIngredient = searchAllergen(allergy.trim(), filteredTokens);
                if (foundIngredient != null) {
                    containsAllergen = true;
                    foundIngredients.add(foundIngredient);
                }
            }
        }
        if (ingredients != null) {
            for (String oneIngredient : ingredients) {
                if (oneIngredient.equalsIgnoreCase("Wine")) {
                    String foundIngredient = searchAllergen(oneIngredient, filteredTokens);
                    if (foundIngredient != null) {
                        containsAllergen = true;
                        foundIngredients.add(foundIngredient);
                    }
                }
                if (checkIndividualIngredient(oneIngredient)) {
                    containsAllergen = true;
                    foundIngredients.add(oneIngredient);
                }
            }
        }

        if (containsAllergen) {
            String allergens = TextUtils.join(", ", foundIngredients);
            dialogMessage = "Ops! This product contains " + allergens + " and it is not suitable for you.";
            showDialog(dialogMessage, R.drawable.notfree);
        } else {
            dialogMessage = "Great! This product is suitable for you.";
            showDialog(dialogMessage, R.drawable.allergenfree);
        }
    }
    private String searchAllergen(String allergy, List<String> filteredTokens) {
        // Perform search within the specified allergy for each filtered token
        for (String token : filteredTokens) {
            try {
                JSONArray ingredientsArray = allergenData.getJSONObject("allergens").getJSONArray(allergy);
                for (int i = 0; i < ingredientsArray.length(); i++) {
                    String jsonIngredient = ingredientsArray.getString(i).toLowerCase();

                    // Check if the preprocessed ingredient is contained in the preprocessed token
                    if (jsonIngredient.equalsIgnoreCase(token)) {
                        return jsonIngredient; // Allergen found for this token
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // If no allergen found for any token, return null
        return null;
    }
    private boolean checkIndividualIngredient(String individual) {
        // Check for individual ingredient
        for (String token : filteredTokens) {
            if (token.equalsIgnoreCase(individual)) {
                return true;
            }
        }
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
