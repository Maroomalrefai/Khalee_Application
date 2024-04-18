package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    Button btnReset, btnBack;
    EditText edtEmail;
    FirebaseAuth mAuth;
    String strEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        //Initializaton
        btnBack = findViewById(R.id.backBtn);
        btnReset = findViewById(R.id.resetBtn);
        edtEmail = findViewById(R.id.emailInput);
        mAuth = FirebaseAuth.getInstance();

        //bacck button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForgetPassword.this, Login.class);
                startActivity(i);
            }
        });

        //Reset Button Listener
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = edtEmail.getText().toString().trim();
                if (!TextUtils.isEmpty(strEmail)) {
                    ResetPassword();
                } else {
                    edtEmail.setError("Email field can't be empty");
                }
            }

            private void ResetPassword() {
                mAuth.sendPasswordResetEmail(strEmail)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ForgetPassword.this, "Reset Password link has been sent to your registered Email", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgetPassword.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ForgetPassword.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



    }
}