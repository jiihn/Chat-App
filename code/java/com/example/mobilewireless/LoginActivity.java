package com.example.mobilewireless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailAdd, password;
    Button btnlogin;
    TextView tvForgetPass;
    ProgressBar progressBar;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailAdd = findViewById(R.id.emailEt);
        password = findViewById(R.id.passwordEt);
        btnlogin = findViewById(R.id.loginbtn);
        tvForgetPass = findViewById(R.id.forgetPassText);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login(){
        progressBar.setVisibility(View.VISIBLE);
        String email = emailAdd.getText().toString();
        String pass = password.getText().toString();

        if (email.isEmpty()) {
            emailAdd.setError("Please enter email address");
            emailAdd.requestFocus();
            progressBar.setVisibility(View.GONE);
        } else if (pass.isEmpty()) {
            password.setError("Please enter your password");
            password.requestFocus();
            progressBar.setVisibility(View.GONE);
        } else if (email.isEmpty() && pass.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Field are empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        } else if (!(email.isEmpty() && pass.isEmpty())) {
            mFirebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                    } else {
                        if (mFirebaseAuth.getCurrentUser().isEmailVerified()) {
                            progressBar.setVisibility(View.GONE);
                            Intent intToHome = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intToHome);
                        } else {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                }
            });

        } else {
            Toast.makeText(LoginActivity.this, "Error Occur", Toast.LENGTH_SHORT).show();
        }
    }
}
