package com.example.safehaven;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.net.Uri;



public class helplineNumbers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpline);
    }

    //Phone number for women's aid UK
    public void aid(View v){
        Intent in1 = new Intent(Intent.ACTION_DIAL);
        in1.setData(Uri.parse("tel:0808 802 5565"));
        startActivity(in1);
    }

    //Phone number for national domestic abuse service
    public void domestic(View view) {
        Intent in2 = new Intent(Intent.ACTION_DIAL);
        in2.setData(Uri.parse("tel:0808 2000 247"));
        startActivity(in2);
    }

    //Phone number for UK police
    public void police(View view) {
        Intent in3 = new Intent(Intent.ACTION_DIAL);
        in3.setData(Uri.parse("tel:999"));
        startActivity(in3);
    }

    //Phone number for student helpline
    public void student(View view) {
        Intent in4 = new Intent(Intent.ACTION_DIAL);
        in4.setData(Uri.parse("tel:116 123"));
        startActivity(in4);
    }
}
