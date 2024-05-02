package com.example.sallaapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Question extends AppCompatActivity {
 Button save;
 EditText date;
 RadioButton agreeRadioButton;
 FirebaseAuth firebaseAuth;
 FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        save = findViewById(R.id.save);
        date = findViewById(R.id.editTextDate);
        agreeRadioButton = findViewById(R.id.agree);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDateToFirebase(date.getText().toString());//saving date into Firebase
                Intent i = new Intent(Question.this, Login.class);
                startActivity(i);
            }



            private void saveDateToFirebase(String date) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    DatabaseReference userRef = database.getReference("users").child(userId);
                    userRef.child("dateOfBirth").setValue(date)
                            .addOnSuccessListener(aVoid -> Toast.makeText(Question.this, "Date saved successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(Question.this, "Failed to save date: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(Question.this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });



        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        agreeRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Enable the button if the user agrees, disable it otherwise
                save.setEnabled(isChecked);
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
                        date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

}

