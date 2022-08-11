package com.example.safehaven;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;

import android.os.Handler;

public class splashPage extends AppCompatActivity {
    private static int Timeout = 1000;

    //Sets splash page as the view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashh);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splashPage.this,Registration.class);
                startActivity(intent);
                finish();
            }
        },Timeout);
    }
}
