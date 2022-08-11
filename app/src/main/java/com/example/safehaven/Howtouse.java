package com.example.safehaven;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.text.Html;
import android.view.Window;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Bundle;

public class Howtouse extends AppCompatActivity {
    ViewPager viewPager;
    LinearLayout linearLayout;
    private slide_changer slide_adapter;
    private TextView[] mdot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howtouse);
        Window window = Howtouse.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(Howtouse.this, R.color.black));

        viewPager = findViewById(R.id.view_page);
        linearLayout = findViewById(R.id.ll);
        slide_adapter = new slide_changer(this);
        viewPager.setAdapter(slide_adapter);
        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);
    }

    //Creates bottom dots which change for each slide
    public void addDotsIndicator(int position){
        mdot = new TextView[3];
        linearLayout.removeAllViews();
        for (int i=0;i<mdot.length;i++){
            mdot[i] = new TextView(this);
            mdot[i].setText(Html.fromHtml("&#8226;"));
            mdot[i].setTextSize(35);
            mdot[i].setTextColor(getResources().getColor(R.color.transparentwhite));
            linearLayout.addView(mdot[i]);
        }
        if(mdot.length>0){
            mdot[position].setTextColor(getResources().getColor(R.color.black));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
}
