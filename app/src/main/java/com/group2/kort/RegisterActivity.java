package com.group2.kort;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private EditText etName, etEmail, etPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(FirebaseConfig.DATABASE_URL)
                .getReference();

        etName = findViewById(R.id.etRegName);
        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        ProgressBar progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Fill all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnRegister.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                String uid = mAuth.getCurrentUser().getUid();
                                HashMap<String, Object> user = new HashMap<>();
                                user.put("name", name);
                                user.put("email", email);
                                user.put("createdAt", System.currentTimeMillis());

                                database.child("users").child(uid).setValue(user)
                                        .addOnSuccessListener(unused -> {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(RegisterActivity.this, "User Registered!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finishAffinity();
                                        })
                                        .addOnFailureListener(error -> {
                                            progressBar.setVisibility(View.GONE);
                                            btnRegister.setEnabled(true);
                                            String errorMsg = error != null ? error.getMessage() : "Unknown error occurred";
                                            Toast.makeText(RegisterActivity.this,
                                                    "Profile save failed: " + errorMsg, Toast.LENGTH_LONG).show();
                                        });
                            } else {
                                progressBar.setVisibility(View.GONE);
                                btnRegister.setEnabled(true);
                                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred";
                                Toast.makeText(RegisterActivity.this, "Error: " +
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        TextView tvLoginLink = findViewById(R.id.tvLoginLink);
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Goes back to Login screen
            }
        });
    }
}
