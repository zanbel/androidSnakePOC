package com.example.zorg.androidsnakepoc;

/**
 * Created by roy on 26-Dec-14.
 */

import android.media.MediaPlayer;

public class SoundManager {

    private SnakeScreen snakeActivity;

    // media players
    private MediaPlayer mpDie;
    private MediaPlayer mpPlayAgain;
    private MediaPlayer mpBackground;

    public SoundManager(SnakeScreen snakeActivity) {

        this.snakeActivity = snakeActivity;

        // media player init
        mpPlayAgain = MediaPlayer.create(snakeActivity, R.raw.toplayagain);
        mpDie = MediaPlayer.create(snakeActivity, R.raw.failbuzzer);
        mpBackground = MediaPlayer.create(snakeActivity, R.raw.iron_man_01);
        mpBackground.setLooping(true);
    }

    public void playBackground(){
        mpBackground.start();
    }

    public void stopBackGround(){
        mpBackground.stop();
    }

    public void playDie(){
        mpDie.start();
        try {
            Thread.currentThread();
            Thread.sleep(500);
        } catch (Exception e) {

        }
    }

    public void stopDie(){
        mpDie.stop();
    }

    public void playPlayAgain(){
        mpPlayAgain.start();
        try {
            Thread.currentThread();
            Thread.sleep(3000);
        } catch (Exception e) {

        }
    }

    public void stopPlayAgain(){
        mpPlayAgain.stop();
    }

    public void releaseAll(){

        mpBackground.release();
        mpDie.release();
        mpPlayAgain.release();
    }
}

