package com.example.sallaapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.model.Post;
import com.squareup.picasso.Picasso;

public class CreatePost extends AppCompatActivity {
    ImageView postImage,profileImage;
    FloatingActionButton attachPhoto;
    EditText postText;
    Button post;
    TextView userNameTx;
    private static final String TAG = "MAIN_TAG";
    private Uri imageUri = null ;
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=101;
    private String[] cameraPermission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String[] storagePermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    String userName;
    String profileImageUrl;
    String currentCommunityId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        //intial views
        postImage=findViewById(R.id.postImage);
        postText=findViewById(R.id.postText);
        attachPhoto=findViewById(R.id.addphoto);
        post=findViewById(R.id.postbtn);
        profileImage=findViewById(R.id.getprofileImage);
        userNameTx=findViewById(R.id.getprofileName);

        Intent intent = getIntent();
        if (intent != null) {
            currentCommunityId = intent.getStringExtra("communityId");
        }

        //disabled post button
        post.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        currentUser  = mAuth.getCurrentUser();
        if (currentUser != null) {
            userName = currentUser.getDisplayName();
        }
        else{
            userName = "User Name";
        }

        progressDialog=new ProgressDialog(this);

        // Set profile image and user name if available
        if (currentUser != null) {
            // Set profile image
            profileImageUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
            if (profileImageUrl != null) {
                // Load profile image using your preferred image loading library, e.g., Picasso, Glide
                Picasso.get().load(profileImageUrl).placeholder(R.drawable.profileicon).into(profileImage);

            }
            // Set user name
            if (userName != null) {
                // Set user name to the appropriate view, e.g., TextView
                userNameTx.setText(userName);
            }else {
                userName="User Name";
                userNameTx.setText(userName);

            }
        }


        // Add a TextChangedListener to the EditText to check if the text is empty or not
        postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the EditText is empty or not
                if (s.toString().trim().isEmpty()) {
                    // If empty, disable the button
                    post.setEnabled(false);
                } else {
                    // If not empty, enable the button
                    post.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this implementation
            }
        });
        // add photo to the post
        attachPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputImageDialog();

            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage(" Publishing Post ");
                progressDialog.show();
                if(imageUri!=null) {
                    //to do create Post object and add it to firebase
                    // upload post image || need to access firebase Storage
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Android Tutorials").child(currentCommunityId).child("Posts").child(currentUser.getUid());
                    StorageReference imageFilePath = storageReference.child(imageUri.getLastPathSegment());
                    imageFilePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();
                                    //check image profile
                                    //create post object
                                    Post post = new Post(postText.getText().toString(),
                                            imageDownloadLink,
                                            currentUser.getUid()
                                            ,userName
                                            ,profileImageUrl
                                    );

                                    //add post to firebase
                                    addPost(post);
                                    // Open DetailCommunity activity here
                                    Intent intent = new Intent(CreatePost.this, DetailCommunity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //something wrong
                                    Toast.makeText(CreatePost.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                }else {
                    // If imageUri is null, create the Post object without the image URL
                    Post post = new Post(postText.getText().toString(),
                            currentUser.getUid()
                            ,userName
                            ,profileImageUrl);

                    // Add the post to Firebase database
                    addPost(post);
                    // Open DetailCommunity activity here
                    Intent intent = new Intent(CreatePost.this, DetailCommunity.class);
                    startActivity(intent);
                    finish();
                }


            }
        });
    }

    // choices for the photo
    private void showInputImageDialog(){
        PopupMenu popupMenu = new PopupMenu(this,attachPhoto);
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
                        postImage.setImageURI(imageUri);
                        postImage.setVisibility(View.VISIBLE);
                    } else {
                        Log.d(TAG, "onActivityResult: cancelled ");
                        Toast.makeText(CreatePost.this, "Cancelled", Toast.LENGTH_LONG).show();
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
                        postImage.setImageURI(imageUri);
                        postImage.setVisibility(View.VISIBLE);
                    }
                    else{
                        Log.d(TAG, "onActivityResult: cancelled");
                        Toast.makeText(CreatePost.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkStoragePermission(){
//        check if storage permissions are allowed or not
//        return true if allowed , false if not allowed
        boolean result= ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
//        request storage permission (for gallery image pick)
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean cameraResult = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
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
//                    check if storage permissions granted, contains boolean results either ture or false
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
//                   check if storage permission is granted or not
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
    private void addPost(Post post){
        if (currentCommunityId != null) {
            if (!isNetworkAvailable()) {
                Toast.makeText(CreatePost.this, "No internet connection. Failed to add post.", Toast.LENGTH_LONG).show();
                return;
            }
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Android Tutorials").child(currentCommunityId).child("Posts").push();
            //get post unique ID and update postKey
            String key = myRef.getKey();
            post.setPostKey(key);
            // Add post data to firebase
            myRef.setValue(post)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(CreatePost.this, "Post Published Successfully", Toast.LENGTH_LONG).show();
                            Log.i("posts", "add");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreatePost.this, "Failed to add post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.i("posts", " not add");
                            progressDialog.dismiss();
                        }

                    });
        }else {
            Log.e(TAG, "Current community ID is null");
        }
    }
    // Method to check for internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}