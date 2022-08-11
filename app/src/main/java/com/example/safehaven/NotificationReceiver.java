package com.example.safehaven;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.util.Log;
import android.content.Context;

public class NotificationReceiver extends BroadcastReceiver{

    final String TAG = "SIREN_NOTIFIED";

    @Override
    public void onReceive(Context context, Intent intent) {
    //Allows for siren controls on notification pane
        //Pause button clicked, siren will stop
        if (intent.getAction().equals("pause_button_clicked")) {
            theMediaPlayer.getInstance(context,null).stopPlayer();
            Log.d(TAG, "PAUSE ACTION");

            //Play button clicked, siren will sound
        }else if(intent.getAction().equals("play_button_clicked")){
            theMediaPlayer.getInstance(context,null).play();
            Log.d(TAG, "PLAY ACTION");

        } else {
            Log.d(TAG, "Error, unable to play siren");
        }
    }
}