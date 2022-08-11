package com.example.safehaven;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;



import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ActionCodeSettings;

public class login extends AppCompatActivity {

    Button b;
    TextInputLayout e5,e6;
    TextView t,forgetPassTv;
    FirebaseAuth fireb;
    ProgressBar progressBar;
    String loginname , loginpassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        e5 = findViewById(R.id.et6);
        e6 = findViewById(R.id.et7);
        b = findViewById(R.id.b2);
        t = findViewById(R.id.tv6);
        forgetPassTv = findViewById(R.id.forget_password_tv);
        progressBar = findViewById(R.id.pb2);
        fireb = FirebaseAuth.getInstance();
        ActionCodeSettings actionCodeSettings;

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, Registration.class));
                finish();
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fields to enter account email and password, with validation to check if email and password has been entered
                String email = e5.getEditText().getText().toString().trim();
                String password = e6.getEditText().getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    e5.setError("Email is Required.");
                    e5.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    e6.setError("Password is Required");
                    e6.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //Firebase authentication sign in
                fireb.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(login.this,MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(login.this, "Error! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        forgetPassTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = e5.getEditText().getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    e5.setError("An email is Required.");
                    e5.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                fireb.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(getApplicationContext(),
                                    "Please check your email for reset password link",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        else
                            Toast.makeText(getApplicationContext(),
                                    "Email not found",
                                    Toast.LENGTH_SHORT)
                                    .show();

                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });

    }
}
