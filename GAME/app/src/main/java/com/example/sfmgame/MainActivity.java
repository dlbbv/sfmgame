package com.example.sfmgame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;
    private Dialog dialogQuit;
    private Dialog dialogQues;
    private Dialog dialogDonate;
    private Dialog dialogFacts;
    private ImageView quit;
    private ImageView ques;
    public String category;
    private ImageView donate;
    MediaPlayer mainmusic;
    private int backpress = 0;
    private ImageView buttonyes;
    private ImageView buttonno;
    private TextView by;
    private int b = 1;
    private ImageView facts;
    List <String> arrayFacts;
    private boolean musicPlay;
    private String checkDoubleFact;
    private int changeMenu = 1;
    private float x1, x2, y1, y2;


    public void onResume() {
        super.onResume();
        if (musicPlay) {
            musicOn2();
        }
        checkMusic();
    }

    public void onPause() {
        super.onPause();
            if (musicPlay) {
                musicOff2();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainmusic.release();
    }

    @Override
    public void onBackPressed() {
        quitBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataBaseHelper mDBHelper;
        mDBHelper = new DataBaseHelper(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }


        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        musicPlay = mPrefs.getBoolean("music", true);


        buttonyes = findViewById(R.id.sound);
        buttonno = findViewById(R.id.soundno);
        setMusic();
        buttonyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicOff1();
            }
        });
        buttonno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicOn1();
            }
        });



        setDialogQuit();
        Button Okay = dialogQuit.findViewById(R.id.btn_yes);
        Button Cancel = dialogQuit.findViewById(R.id.btn_cancel);
        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogQuit.dismiss();
                goQuit();
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogQuit.dismiss();
            }
        });
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ques = dialogQuit.findViewById(R.id.textView2);
                ques.setText("Уже напились?");
                dialogQuit.show();
            }
        });



        setDialogQues();
        Button accept = dialogQues.findViewById(R.id.btn_go);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogQues.dismiss();
            }
        });
        ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ques = dialogQues.findViewById(R.id.textView2);
                ques.setText(R.string.ques1_text);
                dialogQues.show();
            }
        });




        setDialogDonate();
        Button close = dialogDonate.findViewById(R.id.btn_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDonate.dismiss();
            }
        });
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDonate.show();
            }
        });
        Button godonate = dialogDonate.findViewById(R.id.btn_go);
        godonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLinkDonate();
            }
        });



        by = findViewById(R.id.bytext);
        setBy();
        by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLinkBy();
            }
        });


        arrayFacts = strFacts();
        facts = findViewById(R.id.imageView21);
        setDialogFacts();
        Button okbtn = dialogFacts.findViewById(R.id.btnok);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFacts.dismiss();
            }
        });
        TextView changeFact = dialogFacts.findViewById(R.id.textView2);
        changeFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFact();
            }
        });
        facts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFacts.show();
                getFact();
            }
        });


        Button category = findViewById(R.id.button_play);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCategory();
            }
        });

        goAnim();
        titleAnim();
        slideMe();
        swipeInfo();
    }

    ///FACTS///

    public List <String> strFacts() {
        Cursor cursor = mDb.rawQuery("SELECT * FROM FACTS", null);
        cursor.moveToFirst();
        List<String> array = new ArrayList<String>();
        do {@SuppressLint("Range") String ufact = cursor.getString(cursor.getColumnIndex("fact"));
            array.add(ufact);}
        while(cursor.moveToNext());
        return array;
    }

    public int generateFact() {
        Random rand = new Random();
        return rand.nextInt(arrayFacts.size());
    }

    public void getFact() {
        setFact(generateFact());
    }

    public void setFact(int i) {
        while (arrayFacts.get(i).equals(checkDoubleFact)) {
            i = generateFact();
        }
        TextView fact = dialogFacts.findViewById(R.id.textView2);
        fact.setText(arrayFacts.get(i));
        checkDoubleFact = arrayFacts.get(i);
    }

    public void changeBy (int i) {
        if(i==0) {
            by = findViewById(R.id.bytext);
            by.setText(R.string.dlbbv);
        }
        else if(i==1) {
            by = findViewById(R.id.bytext);
            by.setText(R.string.olegotrip);
        }
        else if(i==2) {
            by = findViewById(R.id.bytext);
            by.setText(R.string.minako);
        }
        else {
            by = findViewById(R.id.bytext);
            by.setText(R.string.cha);
        }
    }

    ///-FACTS///


    ///QUIT///

    public void goQuit() {
        this.finishAffinity();
        arrayFacts.clear();
    }

    ///-QUIT///


    ///MENU///

    public void goToCategory() {
        if(changeMenu==1) {
            Intent goHouseQ = new Intent(this, activity_house.class);
            startActivity(goHouseQ);
            category = "house";
            getCategory();
            arrayFacts.clear();
            finish();
        }
        else if(changeMenu==2) {
            Intent goNatureQ = new Intent(this, activity_house.class);
            startActivity(goNatureQ);
            category = "nature";
            getCategory();
            arrayFacts.clear();
            finish();
        }
        else if(changeMenu==3) {
            Intent goHomeQ = new Intent(this, activity_house.class);
            startActivity(goHomeQ);
            category = "all";
            getCategory();
            arrayFacts.clear();
            finish();
        }
        else if(changeMenu==4) {
            Intent goMadnessQ = new Intent(this, activity_house.class);
            startActivity(goMadnessQ);
            category = "madness";
            getCategory();
            arrayFacts.clear();
            finish();
        }
    }

    public void getCategory() {
        Intent getCat = new Intent(this, activity_house.class);
        getCat.putExtra("cat", category);
        startActivity(getCat);
    }

    ///-MENU///


    ///MUSIC///

    public int randMusic() {
        Random music = new Random();
        return music.nextInt(3);
    }

    public void setMusic() {
        if (randMusic() == 0) {
            mainmusic = MediaPlayer.create(MainActivity.this, R.raw.home);
        } else if (randMusic() == 1) {
            mainmusic = MediaPlayer.create(MainActivity.this, R.raw.nature);
        } else if (randMusic() == 2) {
            mainmusic = MediaPlayer.create(MainActivity.this, R.raw.house);
        } else {
            mainmusic = MediaPlayer.create(MainActivity.this, R.raw.madness);
        }
        mainmusic.setLooping(true);
    }

    public void checkMusic() {
        if(musicPlay) {
            buttonyes.setVisibility(View.VISIBLE);
            buttonno.setVisibility(View.INVISIBLE);
        }
        else {
            buttonno.setVisibility(View.VISIBLE);
            buttonyes.setVisibility(View.INVISIBLE);
        }
    }

    public void musicOn1() {
        buttonyes.setVisibility(View.VISIBLE);
        buttonno.setVisibility(View.INVISIBLE);
        mainmusic.start();
        setMusicPlay(true);
    }

    public void musicOn2() {
        mainmusic.start();
    }

    public void musicOff1() {
        buttonno.setVisibility(View.VISIBLE);
        buttonyes.setVisibility(View.INVISIBLE);
        mainmusic.pause();
        setMusicPlay(false);
    }

    public void musicOff2() {
        mainmusic.pause();
    }

    public void setMusicPlay(boolean play) {
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        musicPlay = mPrefs.getBoolean("music", true);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putBoolean("music", play).apply();
        musicPlay = mPrefs.getBoolean("music", true);
    }

    ///-MUSIC///

    ///QUIT-BACK-PRESSED///

    public void quitBackPressed() {
        backpress = (backpress + 1);
        if (backpress == 1) {
            Toast.makeText(getApplicationContext(), "Нажми назад еще раз для выхода из приложения", Toast.LENGTH_SHORT).show();
        }
        if (backpress > 1) {
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

    ///-QUIT-BACK-PRESSED///


    ///BY-TEXT///

    public void setBy() {
        final Handler handlerby = new Handler();
        handlerby.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeBy(b);
                if (b < 3) {
                    b++;
                } else {
                    b = 0;
                }
                handlerby.postDelayed(this, 5000);
            }
        }, 5000);
    }

    public void setLinkBy() {
        Intent goTg = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/sfmgames"));
        startActivity(goTg);
    }

    ///-BY-TEXT///


    ///DONATE///

    public void setLinkDonate() {
        Intent goDonate = new Intent(Intent.ACTION_VIEW, Uri.parse("https://donate.stream/sfmgame"));
        startActivity(goDonate);
    }

    ///-DONATE///


    ///SET-DIALOG///


    public void setDialogQuit() {
        quit = findViewById(R.id.imageView);
        dialogQuit = new Dialog(this);
        dialogQuit.setContentView(R.layout.layout_backtomain);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogQuit.getWindow().setBackgroundDrawable(getDrawable(R.drawable.backgroundquit));
        }
        dialogQuit.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogQuit.setCancelable(false);
    }

    public void setDialogQues() {
        ques = findViewById(R.id.imageView11);
        dialogQues = new Dialog(this);
        dialogQues.setContentView(R.layout.layout_ques2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogQues.getWindow().setBackgroundDrawable(getDrawable(R.drawable.backgroundquit));
        }
        dialogQues.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogQues.setCancelable(false);
    }

    public void setDialogDonate() {
        donate = findViewById(R.id.imageView17);
        dialogDonate = new Dialog(this);
        dialogDonate.setContentView(R.layout.layout_donate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogDonate.getWindow().setBackgroundDrawable(getDrawable(R.drawable.backgroundquit));
        }
        dialogDonate.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogDonate.setCancelable(false);
    }

    public void setDialogFacts() {
        dialogFacts = new Dialog(this);
        dialogFacts.setContentView(R.layout.layout_book);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogFacts.getWindow().setBackgroundDrawable(getDrawable(R.drawable.backgroundquit));
        }
        dialogFacts.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogFacts.setCancelable(false);
    }

    ///-SET-DIALOG///

    ///NEXT-PREV MENU///

    public void changeMenu(boolean i) {
        titleAnim();
        TextView title = findViewById(R.id.main_title);
        TextView des = findViewById(R.id.main_des);
        GifImageView img = findViewById(R.id.main_img);
        if(i) {
            if(changeMenu==1) {
                changeMenu++;
                title.setText(R.string.nature_text);
                des.setText(R.string.naturel_text);
                img.setImageResource(R.drawable.nature2);
            }
            else if(changeMenu==2) {
                changeMenu++;
                title.setText(R.string.home_text);
                des.setText(R.string.homel_text);
                img.setImageResource(R.drawable.home2);
            }
            else if(changeMenu==3) {
                changeMenu++;
                title.setText(R.string.madness_text);
                des.setText(R.string.madnessl_text);
                img.setImageResource(R.drawable.madness2);
            }
            else if(changeMenu==4) {
                changeMenu=1;
                title.setText(R.string.house_text);
                des.setText(R.string.housel_text);
                img.setImageResource(R.drawable.house2);
            }
        }
        else {
            if(changeMenu==1) {
                changeMenu=4;
                title.setText(R.string.madness_text);
                des.setText(R.string.madnessl_text);
                img.setImageResource(R.drawable.madness2);
            }
            else if(changeMenu==2) {
                changeMenu--;
                title.setText(R.string.house_text);
                des.setText(R.string.housel_text);
                img.setImageResource(R.drawable.house2);
            }
            else if(changeMenu==3) {
                changeMenu--;
                title.setText(R.string.nature_text);
                des.setText(R.string.naturel_text);
                img.setImageResource(R.drawable.nature2);
            }
            else if(changeMenu==4) {
                changeMenu--;
                title.setText(R.string.home_text);
                des.setText(R.string.homel_text);
                img.setImageResource(R.drawable.home2);
            }
        }
    }

    ///-NEXT-PREV MENU///

    ///ANIM///

    public void goAnim() {
        Button play = findViewById(R.id.button_play);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        play.startAnimation(pulse);

        ImageView img1 = findViewById(R.id.imageView11);
        ImageView img2 = findViewById(R.id.imageView);
        ImageView img3 = findViewById(R.id.imageView21);
        ImageView img4 = findViewById(R.id.imageView17);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        img1.startAnimation(shake);
        img2.startAnimation(shake);
        img3.startAnimation(shake);
        img4.startAnimation(shake);
    }

    public void titleAnim() {
        TextView text = findViewById(R.id.main_title);
        TextView text2 = findViewById(R.id.main_des);
        ImageView img = findViewById(R.id.main_img);
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        Animation wow = AnimationUtils.loadAnimation(this, R.anim.slidedown);
        text.startAnimation(bounce);
        img.startAnimation(wow);
        text2.startAnimation(wow);
    }

    public void slideMe() {
        ImageView me = findViewById(R.id.imageView23);
        Animation slide = AnimationUtils.loadAnimation(this, R.anim.slideme);
        me.startAnimation(slide);
    }

    ///-ANIM///


    ///SWIPE///

    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchevent.getX();
                y2 = touchevent.getY();
                if(Math.abs(x1-x2)>Math.abs(y1-y2)) {
                    if (x1 < x2) {
                        changeMenu(false);
                    } else if (x1 > x2) {
                        changeMenu(true);
                    }
                }
                else {
                    if (y1 < y2) {
                        changeMenu(false);
                    } else if (y1 > y2) {
                        changeMenu(true);
                    }
                }
                break;
        }
        return false;
    }

    public void swipeInfo() {
        Toast.makeText(getApplicationContext(), "Свайпай, чтобы выбрать другой режим", Toast.LENGTH_SHORT).show();
    }

    ///-SWIPE///

}