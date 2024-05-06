package com.example.sallaapplication;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Question extends AppCompatActivity {
    Button save;
    EditText date;
    RadioButton agreeRadioButton;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference userRef, databaseReference;
    CheckBox treeNutCheckBox, glutenCheckBox, lactoseCheckBox, peanutCheckBox, seafoodCheckBox, sesameCheckBox, eggCheckBox, soyCheckBox, mustardCheckBox;
    TextInputLayout ingredientContainer;
    MaterialAutoCompleteTextView ingredientDropdown;
    String[] optionsList;

    boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        FirebaseApp.initializeApp(Question.this);
        save = findViewById(R.id.save);
        date = findViewById(R.id.editTextDate);
        agreeRadioButton = findViewById(R.id.agree);
        optionsList = getResources().getStringArray(R.array.options_list);

        //
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "Current user is null");
            return;
        }
        String userId = currentUser.getUid();
        Log.d(TAG, "UserID: " + userId);
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);


        // Retrieve the editMode parameter
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            editMode = extras.getBoolean("editMode", false);
        }
        // Hide the birthdate part if it's in edit mode
        if (editMode) {
            TextView birthTextView = findViewById(R.id.Birth);
            EditText birthEditText = findViewById(R.id.editTextDate);
            birthTextView.setVisibility(View.GONE);
            birthEditText.setVisibility(View.GONE);

        }


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


        databaseReference = FirebaseDatabase.getInstance().getReference("ingredients");
        ingredientContainer = findViewById(R.id.ingredientContainer);
        ingredientDropdown = findViewById(R.id.ingredient);

        ingredientDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedIngredient = ingredientDropdown.getText().toString();
            databaseReference.setValue(selectedIngredient)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Ingredient saved to Firebase", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to save ingredient to Firebase", Toast.LENGTH_SHORT).show());


        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedIngredient = ingredientDropdown.getText().toString();

                // Save the selected ingredient to Firebase
                saveIngredientToFirebase(selectedIngredient);

//                if(ingredient.getText().toString().isEmpty())
//                {   ingredientContainer.setError("Please select an ingredient!");
//
//                }
                saveCheckboxState(treeNutCheckBox, "treeNut");
                saveCheckboxState(glutenCheckBox, "gluten");
                saveCheckboxState(lactoseCheckBox, "lactose");
                saveCheckboxState(peanutCheckBox, "peanut");
                saveCheckboxState(seafoodCheckBox, "seafood");
                saveCheckboxState(sesameCheckBox, "sesame");
                saveCheckboxState(eggCheckBox, "egg");
                saveCheckboxState(soyCheckBox, "soy");
                saveCheckboxState(mustardCheckBox, "mustard");
                saveDateToFirebase(date.getText().toString());//saving date into Firebase
                //saving allergies
                Intent i = new Intent(Question.this, Home.class);
                startActivity(i);
            }

            private void saveIngredientToFirebase(String selectedIngredient) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("ingredient");
                databaseReference.setValue(selectedIngredient)
                        //.addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Ingredient saved to Firebase", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to save ingredient to Firebase", Toast.LENGTH_SHORT).show());

            }


            private void saveCheckboxState(CheckBox checkBox, String allergyType) {
                boolean isChecked = checkBox.isChecked();
                Log.d(TAG, "Saving " + allergyType + " state: " + isChecked);
                userRef.child("allergies").child(allergyType).setValue(isChecked);
            }

            private void saveDateToFirebase(String date) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    DatabaseReference userRef = database.getReference("users").child(userId);
                    userRef.child("dateOfBirth").setValue(date)
                            //   .addOnSuccessListener(aVoid -> Toast.makeText(Question.this, "Date saved successfully", Toast.LENGTH_SHORT).show())
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

        if (editMode) {
            setupAllergyFunction();
            retrieveIngredientFromFirebase();
        }
    }
//    private void retrieveIngredientFromFirebase() {
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<String> ingredientsList = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String ingredient = snapshot.getValue(String.class);
//                    if (ingredient != null) {
//                        ingredientsList.add(ingredient);
//                    }
//                }
//                // Set the retrieved data as items for the dropdown
////                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), androidx.transition.R.layout.support_simple_spinner_dropdown_item, ingredientsList);
////                ingredientDropdown.setAdapter(adapter);
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, optionsList);
//                ingredientDropdown.setAdapter(adapter);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle error
//                Log.e("Firebase", "Failed to retrieve data from Firebase: " + databaseError.getMessage());
//                Toast.makeText(getApplicationContext(), "Failed to retrieve data from Firebase", Toast.LENGTH_SHORT).show();
//            }
//        });
//}
private void retrieveIngredientFromFirebase() {

    DatabaseReference ingredientRef = userRef.child("ingredient");
    ingredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            String ingredient = dataSnapshot.getValue(String.class);
            if (ingredient != null) {
                ingredientDropdown.setText(ingredient);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("Firebase", "Failed to retrieve ingredient from Firebase: " + databaseError.getMessage());
            Toast.makeText(getApplicationContext(), "Failed to retrieve ingredient from Firebase", Toast.LENGTH_SHORT).show();
        }
    });
}





    private void setupAllergyFunction() {
        // Setup allergy listeners only if in edit mode
        setupAllergyListener("treeNut", treeNutCheckBox);
        setupAllergyListener( "gluten",glutenCheckBox);
        setupAllergyListener("lactose", lactoseCheckBox);
        setupAllergyListener( "peanut",peanutCheckBox);
        setupAllergyListener("seafood", seafoodCheckBox);
        setupAllergyListener( "sesame",sesameCheckBox);
        setupAllergyListener("egg", eggCheckBox);
        setupAllergyListener( "soy",soyCheckBox);
        setupAllergyListener( "mustard",mustardCheckBox);
    }

    private void setupAllergyListener(String allergyType, CheckBox checkBox) {
        userRef.child("allergies").child(allergyType).addListenerForSingleValueEvent(new ValueEventListener() {
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
                userRef.child("allergies").child(allergyType).setValue(isChecked);
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

