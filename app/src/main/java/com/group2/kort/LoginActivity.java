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

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth; // Firebase Auth instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        ProgressBar progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnLogin.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                btnLogin.setEnabled(true);
                                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred";
                                Toast.makeText(LoginActivity.this, "Authentication failed: " +
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
