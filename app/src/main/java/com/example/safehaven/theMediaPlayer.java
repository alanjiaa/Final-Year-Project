package com.example.safehaven;

import android.media.MediaPlayer;

import android.content.Context;

public class theMediaPlayer {

    static int decidingNumber;
    private static MediaPlayer player = null;
    private static theMediaPlayer single_inst = null;
    private static theMediaPlayerInterface playerInterface = null;

    public interface theMediaPlayerInterface{
        public void onPlayClicked();
        public void onPauseClicked();
    }

    //initialize the siren player
    private theMediaPlayer(Context context, theMediaPlayerInterface playerInterface) {
        player = MediaPlayer.create(context, R.raw.sirensound);
        if(playerInterface!=null)
            this.playerInterface = playerInterface;
    }

    //Creating only one instance of theMediaPlayer class
    public static theMediaPlayer getInstance(Context context, theMediaPlayerInterface playerInterface) {
        if (single_inst == null) {
            single_inst = new theMediaPlayer(context,playerInterface);
        }
        return single_inst;
    }

    //To play the siren noise
    public void play() {
        if (player != null) {
            player.setLooping(true);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
            player.start();
            if(playerInterface != null)
                playerInterface.onPlayClicked();
        }
        decidingNumber = 1;
    }

    //To stop siren noise
    void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            if(playerInterface != null)
                playerInterface.onPauseClicked();
        }
        single_inst = null;
        decidingNumber = 2;
    }
}