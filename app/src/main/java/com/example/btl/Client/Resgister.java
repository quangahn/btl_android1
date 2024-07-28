package com.example.btl.Client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl.R;
import com.example.btl.entites.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class Resgister extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String TAG = "RegisterActivity";
    private DatabaseReference databaseReference;
    private FirebaseFirestore db;
    private EditText etUsername, etPassword, etConfirmPassword, etEmail;
    private Button buttonRegister;
    private ProgressBar loading;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resgister);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        db = FirebaseFirestore.getInstance();
         buttonRegister = findViewById(R.id.btnRegister);
         etUsername = findViewById(R.id.etUsername);
         etPassword = findViewById(R.id.etPassword);
         etConfirmPassword = findViewById(R.id.etConfirmPassword);
         etEmail = findViewById(R.id.etEmail);
         loading= findViewById(R.id.customProgressBar);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                register();
            }
        });
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(Resgister.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading(true);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this,task-> {
                    if(task.isSuccessful()){

                        Toast.makeText(Resgister.this, "Register success", Toast.LENGTH_SHORT).show();
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String UID = firebaseUser.getUid();
                        User user = new User(UID,  email, password,"","USER", true,username);
//                        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(UID);
                        db.collection("Users").document(UID).set(user)
                                .addOnSuccessListener(a-> {
                                    Toast.makeText(Resgister.this, "Register success", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e-> {
                                    Toast.makeText(Resgister.this, "Register failed", Toast.LENGTH_SHORT).show();
                                    showLoading(false);
                                });
                        Intent intent = new Intent(Resgister.this, Login.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(Resgister.this, "Register failed", Toast.LENGTH_SHORT).show();
                        showLoading(false);
                    }
                });



    }



    private void showLoading(boolean b){
        if(b){
            loading.setVisibility(View.VISIBLE);
            buttonRegister.setVisibility(View.GONE);
        }else{
            loading.setVisibility(View.GONE);
            buttonRegister.setVisibility(View.VISIBLE);
        }
    }
}