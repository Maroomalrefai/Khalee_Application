package com.example.sallaapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {
    Button signUp;
    EditText editTextEmail,editTextPass,editTextUsername;
    FirebaseAuth mAuth;

    TextView textView;
    TextInputLayout passwordInputLayout;
    TextView passwordFeedbackTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUp = findViewById(R.id.signUp);
        editTextEmail=findViewById(R.id.email);
        editTextPass=findViewById(R.id.password);
        editTextUsername=findViewById(R.id.username);
        mAuth=FirebaseAuth.getInstance();
        textView=findViewById(R.id.loginNow);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        passwordFeedbackTextView = findViewById(R.id.passwordFeedbackTextView);
        int minPassLength=6;
        editTextPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Unused
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();

                boolean hasUppercase = password.matches(".[A-Z].");
                boolean hasLowercase = password.matches(".[a-z].");
                boolean hasDigitOrSymbol = password.matches(".[0-9\\W].");

                if (hasUppercase && hasLowercase && hasDigitOrSymbol && password.length() >=minPassLength) {
                    // Password meets all requirements
                    passwordInputLayout.setError(null);
                    passwordFeedbackTextView.setVisibility(View.GONE);
                } else {
                    // Password does not meet all requirements
                    StringBuilder feedback = new StringBuilder();

                    if (!hasUppercase) {
                        feedback.append("At least one uppercase letter\n");
                    }

                    if (!hasLowercase) {
                        feedback.append("At least one lowercase letter\n");
                    }

                    if (!hasDigitOrSymbol) {
                        feedback.append("At least one number or symbol\n");
                    }

                    if (password.length() < minPassLength) {
                        feedback.append("Minimum " + minPassLength + " characters\n");
                    }

                    passwordInputLayout.setErrorEnabled(true);
                    passwordInputLayout.setError("Password does not meet requirements");
                    passwordFeedbackTextView.setText(feedback.toString().trim());
                    passwordFeedbackTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Unused
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email,password;
                email=String.valueOf(editTextEmail.getText());
                password=String.valueOf(editTextPass.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(SignUp.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(SignUp.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUp.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();
                                    //if the account had been created go to the question number one
                                    Intent intent =new Intent(getApplicationContext(),Question.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Toast.makeText(SignUp.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });


    }
}