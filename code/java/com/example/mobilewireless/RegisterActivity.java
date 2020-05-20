package com.example.mobilewireless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

public class RegisterActivity extends AppCompatActivity {

    EditText emailAdd, password, conPassword;
    TextView tvLogin;
    ProgressBar progressBar;
    FirebaseAuth mFirebaseAuth;
    Button btnregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        emailAdd = findViewById(R.id.emailEt);
        password = findViewById(R.id.passwordEt);
        conPassword = findViewById(R.id.conPasswordEt);
        tvLogin = findViewById(R.id.loginNowText);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        btnregister = findViewById(R.id.registerbtn);

        mFirebaseAuth = FirebaseAuth.getInstance();

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    public void Register(){
        final String email = emailAdd.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String Conpass = conPassword.getText().toString().trim();

        if(email.isEmpty()){
            emailAdd.setError("Please enter email address");
            emailAdd.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailAdd.setError("Please enter correct email format");
            emailAdd.requestFocus();
            return;
        }

        if(pass.length() < 6){
            password.setError("Password length must be more than 6");
            password.requestFocus();
            return;
        }

        if(!pass.equals(Conpass)){
            password.setError("Password is not same");
            conPassword.setError("Password is not same");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mFirebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) { progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Register successful. Please verify you email", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent (RegisterActivity.this, LoginActivity.class);
                                        startActivity(i);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
