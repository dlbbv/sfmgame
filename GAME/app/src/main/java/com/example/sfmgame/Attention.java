package com.example.sfmgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Attention extends AppCompatActivity {

    private int backpress = 0;

    @Override
    public void onBackPressed() {
        backpress = (backpress + 1);
        if(backpress==1) {
            Toast.makeText(getApplicationContext(), "Нажми назад еще раз для выхода из приложения", Toast.LENGTH_SHORT).show();
        }
        if (backpress>1) {
            Toast.makeText(getApplicationContext(), "До скорой встречи! Открывай меня почаще!", Toast.LENGTH_SHORT).show();
            goQuit();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                backpress = 0;
            }
        }, 10000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);

        goAnim();
    }

    public void goPlayActivity(View goPlay) {
        Intent goPlayActivity = new Intent(this, MainActivity.class);
        startActivity(goPlayActivity);
        finish();
    }

    public void goQuit(){
        this.finishAffinity();
    }

    public void goAnim() {
        Button goplay = findViewById(R.id.goplay);
        TextView at = findViewById(R.id.textView);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        goplay.startAnimation(pulse);
        at.startAnimation(shake);
    }
}