package com.example.sallaapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class feedback extends AppCompatActivity {

    EditText feedbackText;
    Button sendBtn;
    private DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser user ;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedbackText = findViewById(R.id.feedbackText);
        sendBtn = findViewById(R.id.sendFeedback);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();
        databaseReference = firebaseDatabase.getReference().child("users").child(userId).child("Feedback");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = feedbackText.getText().toString().trim();
                // Save text directly to Firebase
                databaseReference.push().setValue(text);
                // Optionally, you can clear the EditText after sending
                feedbackText.setText("");
                Toast.makeText(feedback.this, "Feedback has been sent!", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(feedback.this, ProfileChange.class);
//                startActivity(intent);
                finish();

            }
        });
    }
}
