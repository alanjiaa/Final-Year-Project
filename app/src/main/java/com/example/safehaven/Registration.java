package com.example.safehaven;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.AuthResult;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

public class Registration extends AppCompatActivity {
    TextInputLayout e1,e2,e3,e4;
    Button b;
    TextView tv;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        e1 = findViewById(R.id.et1);
        e2 = findViewById(R.id.et2);
        e3 = findViewById(R.id.et3);
        e4 = findViewById(R.id.et4);
        b = findViewById(R.id.b1);
        tv = findViewById(R.id.tv3);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.pb);

        //If an account is signed in, application will open to main page
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        //Creates fields for user to create account
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = e1.getEditText().getText().toString().trim();
                String email = e2.getEditText().getText().toString().trim();
                String password = e3.getEditText().getText().toString().trim();
                String confirmPassword = e4.getEditText().getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    e1.setError("Please enter your name");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    e2.setError("Please enter your email");
                    return;
                }
                //Email validation
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (!email.matches(emailPattern)) {
                    e2.setError("Please enter a valid email address.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    e3.setError("Password is Required");
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    e4.setError("Enter password one more time here");
                    return;
                }
                //Password validations
                if(password.length() < 8) {
                    e3.setError("Password Must be >= 8 Characters");
                    return;
                }
                if(!password.matches("(.*[0-9].*)")) {
                    e3.setError("Password must contain at least one number");
                    return;
                }
                if(!password.matches("(.*[A-Z].*)")) {
                    e3.setError("Password must contain a capital letter");
                    return;
                }
                if(!password.matches("^(?=.*[_.()$&@]).*$")) {
                    e3.setError("Password must contain special symbol(s)");
                    return;
                }
                if(!(confirmPassword.equals(password))){
                    e4.getEditText().setText("");
                    e4.setError("Wrong password");
                    return;
                }
                
                //Creates new user and adds to firebase
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Registration.this, "User Created.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Registration.this, MainActivity.class);
                            intent.putExtra("Name", e1.getEditText().getText().toString());
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(Registration.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });


            }

        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this,login.class));
                finish();
            }
        });

    }
}
