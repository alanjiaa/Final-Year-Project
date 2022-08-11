package com.example.safehaven;

import android.content.Context;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.ImageView;



import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class slide_changer extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public slide_changer(Context context){
        this.context = context;
    }

    //Uses tutorial images from res folder
    public int[] slide_images = {
            R.mipmap.use1,
            R.mipmap.use2,
            R.mipmap.use3

    };

    //Numbers for slides
    public String[] slide_heading = {
            "1",
            "2",
            "3"
    };

    //Description for slides
    public String[] slide_dis = {
            "You must first add your family or friends contact numbers.",
            "Sms with your location will be sent to registered contacts either by starting shake service or pressing emergency button",
            "If needed, you can also refer to Helpline numbers for help."
    };

    @Override
    public int getCount() {
        return slide_heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    //
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        //Instantiates XML contents from slide layout for new view object
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);
        ImageView imageView = view.findViewById(R.id.image_view);
        TextView textView1 = view.findViewById(R.id.txt_view5);
        TextView textView2 = view.findViewById(R.id.txt_view6);
        imageView.setImageResource(slide_images[position]);
        textView1.setText(slide_heading[position]);
        textView2.setText(slide_dis[position]);

        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
