package com.example.sfmgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class activity_launch extends AppCompatActivity {

    MediaPlayer start;
    private boolean musicPlay;


    public void onResume() {
        super.onResume();
        if (musicPlay) {
            start.start();
        }
    }

    public void onPause() {
        super.onPause();
        if (musicPlay) {
            start.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        start.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        musicPlay = mPrefs.getBoolean("music", true);

        setLogoAndMusic();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent goRun = new Intent(activity_launch.this, Attention.class);
                startActivity(goRun);
                finish();
            }
        }, 3*1000);

    }

    public boolean randrare(){
        boolean rand = false;
        Random rare = new Random();
        int r = rare.nextInt(100);
        if(r==69) {rand = true;}
        return rand;
    }

    public void setLogoAndMusic() {
        if (randrare()) {
            start = MediaPlayer.create(activity_launch.this, R.raw.rarestart);
            ImageView logo = findViewById(R.id.imageView12);
            logo.setImageResource(R.drawable.pivlogowhite);
            logo.setImageResource(R.drawable.logorare);
        } else {
            start = MediaPlayer.create(activity_launch.this, R.raw.start);
        }
    }

}