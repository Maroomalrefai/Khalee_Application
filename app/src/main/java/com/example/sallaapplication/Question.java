package com.example.sallaapplication;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
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

    boolean editMode;



    MaterialCardView selectCard;
    TextView tvIngredients;
    boolean []selectedIngredients;
    ArrayList<Integer>ingredientList=new ArrayList<>();
    String [] ingredientArray={"Strawberry","Sunflower seeds","Pumpkin seeds","Garlic",
            "Cherries","Onion","Blackberry","Raspberry","Honey","Tomato"};
    DatabaseReference userIngredientsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        FirebaseApp.initializeApp(Question.this);
        save = findViewById(R.id.save);
        date = findViewById(R.id.editTextDate);
        agreeRadioButton = findViewById(R.id.agree);
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


        selectCard=findViewById(R.id.selectCard);
        tvIngredients=findViewById(R.id.ingredients);
        selectedIngredients= new boolean[ingredientArray.length];
        userIngredientsRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("ingredients");


        selectCard.setOnClickListener(v -> {
            showIngredients();

        });


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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveIngredientsToFirebase();
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


                saveLoginStatus(true);
                //saving allergies
                Intent i = new Intent(Question.this, Home.class);
                startActivity(i);

            }
            // Method to save selected ingredients to Firebase
            // Method to save selected ingredients to Firebase
            private void saveIngredientsToFirebase() {
                String userId = firebaseAuth.getCurrentUser().getUid();
                DatabaseReference ingredientsRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("ingredients");

                // First, set all ingredients to false
                for (String ingredient : ingredientArray) {
                    ingredientsRef.child(ingredient).setValue(false);
                }

                // Then, set selected ingredients to true
                for (Integer index : ingredientList) {
                    String selectedIngredient = ingredientArray[index];
                    ingredientsRef.child(selectedIngredient).setValue(true)
                            .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Ingredient saved to Firebase", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to save ingredient to Firebase", Toast.LENGTH_SHORT).show());
                }
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
        }


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

    // Method to save login status
    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences preferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
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


    private void showIngredients(){
        AlertDialog.Builder builder=new AlertDialog.Builder(Question.this);
        builder.setTitle("Select ingredients");
        builder.setCancelable(false);
        builder.setMultiChoiceItems(ingredientArray, selectedIngredients, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked){
                    ingredientList.add(which);
                }else {
                    ingredientList.remove(which);
                }
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder stringBuilder=new StringBuilder();
                for(int i=0;i<ingredientList.size();i++){
                    stringBuilder.append(ingredientArray[ingredientList.get(i)]);

                    //check condition
                    if(i!= ingredientList.size()-1){
                        //when  value not equal to ingredient list sizze
                        stringBuilder.append(", ");
                    }
                    //setting selected ingredients
                    tvIngredients.setText(stringBuilder.toString());
                }


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //clearing all selected ingredients
                for(int i=0;i<selectedIngredients.length;i++){
                    selectedIngredients[i]=false;
                    ingredientList.clear();
                    tvIngredients.setText("");
                }

            }
        });
        builder.show();

    }


}

