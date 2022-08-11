package com.example.safehaven;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Bundle;
import android.view.View;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import android.os.Build;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.app.PendingIntent;

public class siren extends AppCompatActivity implements theMediaPlayer.theMediaPlayerInterface {

    Button stop, play;
    TextView stopText, playText;

    public static final String CHANNEL_ID = "channel_for_siren";
    public static final String CHANNEL_NAME = "siren_noise";
    public static final String CHANNEL_DESC= "play_and_pause_siren";
    NotificationManagerCompat notificationManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siren);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notiChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notiChannel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notiChannel);
        }

        playText = findViewById(R.id.txt_view3);
        stopText = findViewById(R.id.txt_view4);
        play = findViewById(R.id.b5);
        stop = findViewById(R.id.b6);

        decide(theMediaPlayer.decidingNumber);
    }

    @Override
    protected void onResume() {
        super.onResume();
        decide(theMediaPlayer.decidingNumber);
        theMediaPlayer.getInstance(this,this);
    }

    private void displaySirenNotification(){
        Intent intent = new Intent(this, siren.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playIntent = new Intent(this, NotificationReceiver.class);
        playIntent.setAction("play_button_clicked");
        PendingIntent playPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        2,
                        playIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class);
        pauseIntent.setAction("pause_button_clicked");
        PendingIntent pausePendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        2,
                        pauseIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        //Sets a widget on notification pane to allow for easy playing and pausing of siren noise
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.logo1)
                .setContentTitle("Siren noise")
                .setContentText("Play/Pause siren")
                .setPriority(Notification.PRIORITY_LOW)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .addAction(R.mipmap.stop,"PLAY",playPendingIntent)
                .addAction(R.mipmap.stop,"PAUSE",pausePendingIntent);

        notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1,mBuilder.build());
    }

    public void decide(int number) {
        if (number == 1) {
            play.setVisibility(View.INVISIBLE);
            playText.setVisibility(View.INVISIBLE);

            stop.setVisibility(View.VISIBLE);
            stopText.setVisibility(View.VISIBLE);

        } else {
            stop.setVisibility(View.INVISIBLE);
            stopText.setVisibility(View.INVISIBLE);

            play.setVisibility(View.VISIBLE);
            playText.setVisibility(View.VISIBLE);
        }
    }

    public void play(View view) {
        theMediaPlayer.getInstance(this,this).play();
        theMediaPlayer.decidingNumber = 1;
        decide(theMediaPlayer.decidingNumber);

        displaySirenNotification();
    }

    public void stop(View v) {
        theMediaPlayer.getInstance(this,this).stopPlayer();
        theMediaPlayer.decidingNumber = 0;
        decide(theMediaPlayer.decidingNumber);
    }

    @Override
    public void onPlayClicked() {
        decide(1);
    }

    @Override
    public void onPauseClicked() {
        Toast.makeText(this,"Siren has been paused",Toast.LENGTH_SHORT).show();
        decide(2);
    }
}