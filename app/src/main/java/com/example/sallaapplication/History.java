//package com.example.sallaapplication;
//
//import android.os.Bundle;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class History extends AppCompatActivity {
//
//    private static final String TAG = "HistoryActivity";
//    private RecyclerView recyclerView;
//    private List<String> imageUrls;
//    private MyAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_history);
//
//        recyclerView = findViewById(R.id.recyclerView);
//
//        // Get a reference to the "images" node in the Firebase Realtime Database
//        DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference("history");
//
//        // Listen for changes in the "images" node
//        imagesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                imageUrls = new ArrayList<>();
//                // Iterate through the children of the "images" node
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    // Get the imageUrl from each snapshot and add it to the list
//                    ImageModel imageModel = snapshot.getValue(ImageModel.class);
//                    if (imageModel != null) {
//                        String imageUrl = imageModel.getImageUrl();
//                        imageUrls.add(imageUrl);
//                    }
//                }
//                // Initialize the adapter with the list of image URLs
//                adapter = new MyAdapter(History.this, imageUrls);
//                // Set the adapter to the RecyclerView
//                recyclerView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle errors
//                Log.e(TAG, "Failed to read value.", databaseError.toException());
//            }
//        });
//    }
//}
