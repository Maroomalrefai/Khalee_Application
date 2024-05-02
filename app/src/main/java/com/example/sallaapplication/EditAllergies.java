package com.example.sallaapplication;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class EditAllergies extends AppCompatActivity {
    Button save;
    RadioButton agreeRadioButton;
    CheckBox treeNutCheckBox, glutenCheckBox, lactoseCheckBox, peanutCheckBox, seafoodCheckBox, sesameCheckBox, eggCheckBox, soyCheckBox, mustardCheckBox;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        //intialization
        save = findViewById(R.id.save);
        agreeRadioButton = findViewById(R.id.agree);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        // Initialize checkboxes
        treeNutCheckBox = findViewById(R.id.treenut);
        glutenCheckBox = findViewById(R.id.gluten);
        lactoseCheckBox = findViewById(R.id.lactose);
        peanutCheckBox = findViewById(R.id.peannut);
        seafoodCheckBox = findViewById(R.id.seafood);
        sesameCheckBox = findViewById(R.id.seasem);
        eggCheckBox = findViewById(R.id.egg);
        soyCheckBox = findViewById(R.id.soy);
        mustardCheckBox = findViewById(R.id.musterd);

        setupAllergyListener("treeNut", treeNutCheckBox);
        setupAllergyListener( "gluten",glutenCheckBox);
        setupAllergyListener("lactose", lactoseCheckBox);
        setupAllergyListener( "peanut",peanutCheckBox);
        setupAllergyListener("seafood", seafoodCheckBox);
        setupAllergyListener( "sesame",sesameCheckBox);
        setupAllergyListener("egg", eggCheckBox);
        setupAllergyListener( "soy",soyCheckBox);
        setupAllergyListener( "mustard",mustardCheckBox);



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditAllergies.this, ProfileChange.class);
                startActivity(i);
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
    private void setupAllergyListener(String allergyType, CheckBox checkBox) {
        databaseReference.child("allergies").child(allergyType).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isChecked = dataSnapshot.getValue(Boolean.class);
                if (isChecked != null) {
                    checkBox.setChecked(isChecked);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }


        });
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = checkBox.isChecked();
                databaseReference.child("allergies").child(allergyType).setValue(isChecked);
            }
        });
    }



}

