package com.example.safehaven;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;

import android.widget.Toast;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    CardView cview1,cview2,cview3,cview4,cview5,cview6;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cview1 = findViewById(R.id.card1);
        cview2 = findViewById(R.id.card2);
        cview3 = findViewById(R.id.card3);
        cview4 = findViewById(R.id.card6);
        cview5 = findViewById(R.id.card7);
        cview6 = findViewById(R.id.card8);
        firebaseAuth = FirebaseAuth.getInstance();

        //Sets listeners for each option on main screen, then directs them to the appropiate class
        cview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, addContacts.class));
            }
        });

        cview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,emergency.class));
            }
        });

        cview5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, helplineNumbers.class));
            }
        });

        cview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,siren.class));
            }
        });

        cview6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertMessage();
            }
        });

        cview4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Howtouse.class));
            }
        });
    }

    //Logs user out, using firebase
    private void logOut(){
        firebaseAuth.signOut();
        Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this,login.class));
        finish();
    }

    //Logout confirmation
    private void showAlertMessage(){
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logOut();
                    }
                }).show();
    }

}

