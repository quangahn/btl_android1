package com.example.btl.Client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl.R;
import com.example.btl.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private Button btnLogin;
    private EditText email, password;
    private TextView  toRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.customProgressBar);
        mAuth = FirebaseAuth.getInstance();
        toRegister = findViewById(R.id.btnToRegister);
        btnLogin= findViewById(R.id.btnLogin);
        String Stremail= getIntent().getStringExtra("email");
        if (Stremail != null) {
            email.setText(Stremail);
        }
        btnLogin.setOnClickListener(v -> {
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();
            sign_in(emailStr, passwordStr);
        });
        toRegister.setOnClickListener(v ->{
            Intent intent = new Intent(Login.this, Resgister.class);
            startActivity(intent);
        });
    }

    private void sign_in(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email và mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        showLoading(false);
                        finish();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException|| task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            showLoading(false);
                            Toast.makeText(Login.this, "Người dùng hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                        }else {
                            showLoading(false);
                            Toast.makeText(Login.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
        }

    }
}