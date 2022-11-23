package com.example.sfmgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class activity_house extends AppCompatActivity {


    private SQLiteDatabase mDb;
    MediaPlayer housemusic;
    private Dialog dialogQuit;
    private ImageView quit;
    private Dialog dialogQues;
    private ImageView ques;
    private Dialog dialogDonate;
    private ImageView donate;
    private Dialog dialogFacts;
    private String cat;
    private int donatei = 0;
    private int backpress = 0;
    private ImageView buttonyes;
    private ImageView buttonno;
    List <String> textArray;
    private ImageView facts;
    List <String> arrayFacts;
    private TextView by;
    private int b = 1;
    private boolean musicPlay;
    int snailExit = 0;
    private boolean snailRare = false;
    private String checkDouble;
    private String checkDoubleFact;
    public boolean ifDouble = false;
    public int ifDoubleI = 0;



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
        housemusic.release();
    }


    @Override
    public void onBackPressed() {
        quitBackPressed();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house);


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


        setFirstWord();



        setDialogQuit();
        Button Okay = dialogQuit.findViewById(R.id.btn_yes);
        Button Cancel = dialogQuit.findViewById(R.id.btn_cancel);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ques = dialogQuit.findViewById(R.id.textView2);
                ques.setText("И все, да?");
                dialogQuit.show();
            }
        });
        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogQuit.dismiss();
                goMain();
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogQuit.dismiss();
            }
        });


        setDialogQues();
        Button accept = dialogQues.findViewById(R.id.btn_go);
        ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView ques = dialogQues.findViewById(R.id.textView2);
                ques.setText(R.string.ques2_text);
                dialogQues.show();
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogQues.dismiss();
            }
        });


        setDialogDonate();
        Button close = dialogDonate.findViewById(R.id.btn_close);
        Button goDonate = dialogDonate.findViewById(R.id.btn_go);
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDonate.show();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDonate.dismiss();
            }
        });
        goDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLinkDonate();
            }
        });


        buttonyes = findViewById(R.id.sound);
        buttonno = findViewById(R.id.soundno);
        Intent getCat = getIntent();
        cat = getCat.getStringExtra("cat");
        setMusic(cat);
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


        setDialogFacts();
        Button okbtn = dialogFacts.findViewById(R.id.btnok);
        TextView changeFact = dialogFacts.findViewById(R.id.textView2);
        arrayFacts = strFacts();
        facts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFacts.show();
                getFact();
            }
        });
        changeFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFact();
            }
        });
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFacts.dismiss();
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


        textArray = str();
        Collections.shuffle(textArray);


        ImageView sfmImg = findViewById(R.id.imageView22);
        sfmImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snailEx(snailExit);
                snailExit++;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        snailExit=0;
                    }
                }, 1000000);
            }
        });

        goAnim();
        textAnim();
    }


    public List <String> strFacts() {

        Cursor cursor = mDb.rawQuery("SELECT * FROM FACTS", null);
        cursor.moveToFirst();
        List<String> array = new ArrayList<String>();
        do {
            @SuppressLint("Range") String ufact = cursor.getString(cursor.getColumnIndex("fact"));
            array.add(ufact);
        }
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


    public void goMain() {
        Intent goMain = new Intent(this, MainActivity.class);
        startActivity(goMain);
        arrayFacts.clear();
        textArray.clear();
        finish();
    }


    public List <String> str() {

        Cursor cursor = mDb.rawQuery("SELECT * FROM textinfo", null);
        cursor.moveToFirst();
        List<String> array = new ArrayList<String>();
        do {
            @SuppressLint("Range") String fil = cursor.getString(cursor.getColumnIndex("category"));
            @SuppressLint("Range") int count = cursor.getInt(cursor.getColumnIndex("count"));
            int iCount = 0;
            if(cat.equals("madness")) {
                while(iCount<count) {
                    @SuppressLint("Range") String uname = cursor.getString(cursor.getColumnIndex("text"));
                    array.add(uname);
                    iCount++;
                }
            }
            else if (cat.equals("house")){
                if (!fil.equals("nature")) {
                    while(iCount<count) {
                        @SuppressLint("Range") String uname = cursor.getString(cursor.getColumnIndex("text"));
                        array.add(uname);
                        iCount++;
                    }
                }
            }
            else if (fil.equals(cat)){
                while(iCount<count) {
                    @SuppressLint("Range") String uname = cursor.getString(cursor.getColumnIndex("text"));
                    array.add(uname);
                    iCount++;
                }
            }
            else if(fil.equals("all")){
                while(iCount<count) {
                    @SuppressLint("Range") String uname = cursor.getString(cursor.getColumnIndex("text"));
                    array.add(uname);
                    iCount++;
                }
            }
        }
        while(cursor.moveToNext());
        return array;
    }

    public void setFirstWord() {
        TextView wordTextView = findViewById(R.id.textTask);
        wordTextView.setText("Ну, для начала выпьем за встречу, друзья!");
        checkDouble = wordTextView.getText().toString();
    }

    public int generate() {
            Random rand = new Random();
            return rand.nextInt(textArray.size());
    }

    public int getCordImg(String c) {
        ImageView img = findViewById(R.id.imageView18);
        ImageView img2 = findViewById(R.id.imageView22);
        int cord;
        if(c=="xStart"){
            cord=(int)img.getX()+(img2.getWidth()/2);}
        else if(c=="yStart"){
            cord=(int)img.getY()+(img2.getHeight()/2);}
        else if(c=="xEnd"){
            cord=img.getWidth()-(img2.getWidth()*2);
        }
        else if(c=="yEnd"){
            cord=img.getHeight()-(img2.getHeight()*2);
        }
        else {cord =0;}
        return cord;
    }

    public float randXCord(int xEnd, int xStart) {
        float x;
        int newX;
        Random randX = new Random();
        newX = randX.nextInt(xEnd)+xStart;
        x = (float) newX;
        return x;
    }

    public float randYCord(int yEnd, int yStart) {
        float y;
        int newY;
        Random randY = new Random();
        newY = randY.nextInt(yEnd)+yStart;
        y = (float) newY;
        return y;
    }


    public void nextWord(View view) { //nextWord is the onclick of the button
        getNextWord(generate());
    }

    @SuppressLint("ResourceAsColor")
    public void getNextWord(int l) {
        if (textArray.size()==1) {
            TextView wordTextView = findViewById(R.id.textTask);
            wordTextView.setText("Поздравляю! Вы дожили до конца! Остались только сильнейшие из пьющих и пьющие из сильнейших!");
            Button el3 = findViewById(R.id.button2);
            el3.setText("МЕНЮ");
            el3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goMain();
                }
            });
            snailRare = false;
            ImageView sfm = findViewById(R.id.imageView22);
            sfm.setX(getCordImg("xStart"));
            sfm.setY(getCordImg("yStart"));
            sfm.setVisibility(View.VISIBLE);
            sfm.setImageResource(R.drawable.pivlogowhite);
            if (randrare()) {
                sfm.setImageResource(R.drawable.logorare);
                snailRare = true;
            }

        } else {
        snailRare = false;
        donatei++;
        if (donatei % 20 == 0) {
            dialogDonate = new Dialog(this);
            dialogDonate.setContentView(R.layout.layout_donate);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialogDonate.getWindow().setBackgroundDrawable(getDrawable(R.drawable.backgroundquit));
            }
            dialogDonate.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            dialogDonate.setCancelable(false);
            dialogDonate.show();
            Button close = dialogDonate.findViewById(R.id.btn_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDonate.dismiss();
                }
            });
            Button godonate = dialogDonate.findViewById(R.id.btn_go);
            godonate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goDonate = new Intent(Intent.ACTION_VIEW, Uri.parse("https://donate.stream/sfmgame"));
                    startActivity(goDonate);
                }
            });

        }
        int k;
        k = 0;

        if (ifDouble) {
            ifDoubleI++;
            if(ifDoubleI>5) {
                ifDouble = false;
            }
            if (textArray.size()>20) {
                do {
                    k = generate();
                } while (k==0);
            }
        }



        if(textArray.size()>15) {
            int i =0;
            while (textArray.get(k).substring(0, 12).equals(checkDouble.substring(0, 12))) {
                if(i>10) {
                    break;
                }
                i++;
                do {
                    k = generate();
                } while (k==0);
                ifDouble = true;
            }
        }
                TextView wordTextView = findViewById(R.id.textTask);
                wordTextView.setText(textArray.get(k));
                checkDouble = textArray.get(k);
                textArray.remove(k);
                ImageView sfm = findViewById(R.id.imageView22);
                sfm.setVisibility(View.VISIBLE);
                ImageView el1 = findViewById(R.id.imageView18);
                el1.setImageResource(R.drawable.backgroundquit);
                wordTextView.setVisibility(View.INVISIBLE);
                Button el3 = findViewById(R.id.button2);
                el3.clearAnimation();
                el3.setVisibility(View.INVISIBLE);
                wordTextView.clearAnimation();

                sfm.setX(randXCord(getCordImg("xEnd"), getCordImg("xStart")));
                sfm.setY(randYCord(getCordImg("yEnd"), getCordImg("yStart")));

                sfm.setImageResource(R.drawable.pivlogowhite);
                if (randrare()) {
                    sfm.setImageResource(R.drawable.logorare);
                    snailRare = true;
                }
                Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ImageView sfm = findViewById(R.id.imageView22);
                        sfm.setVisibility(View.INVISIBLE);
                        ImageView el1 = findViewById(R.id.imageView18);
                        el1.setImageResource(R.drawable.backgroundfact);
                        TextView el2 = findViewById(R.id.textTask);
                        el2.setVisibility(View.VISIBLE);
                        Button el3 = findViewById(R.id.button2);
                        el3.startAnimation(pulse);
                        el3.setVisibility(View.VISIBLE);
                        textAnim();
                    }
                }, 500);
    }
    }

    public boolean randrare(){
        boolean rand = false;
        Random rare = new Random();
        int r = rare.nextInt(1000);
        if(r==69) {rand = true;}
        return rand;
    }

    public void goQuit(){
        arrayFacts.clear();
        textArray.clear();
        this.finishAffinity();
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


    ///MUSIC///

    public void setMusic(String cat) {
        switch (cat) {
            case "home":
                housemusic = MediaPlayer.create(activity_house.this, R.raw.home);
                break;
            case "nature":
                housemusic = MediaPlayer.create(activity_house.this, R.raw.nature);
                break;
            case "house":
                housemusic = MediaPlayer.create(activity_house.this, R.raw.house);
                break;
            default:
                housemusic = MediaPlayer.create(activity_house.this, R.raw.madness);
                break;
        }
        housemusic.setLooping(true);
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
        housemusic.start();
        setMusicPlay(true);
    }

    public void musicOn2() {
        housemusic.start();
    }

    public void musicOff1() {
        buttonno.setVisibility(View.VISIBLE);
        buttonyes.setVisibility(View.INVISIBLE);
        housemusic.pause();
        setMusicPlay(false);
    }

    public void musicOff2() {
        housemusic.pause();
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
        facts = findViewById(R.id.imageView21);
        dialogFacts = new Dialog(this);
        dialogFacts.setContentView(R.layout.layout_book);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogFacts.getWindow().setBackgroundDrawable(getDrawable(R.drawable.backgroundquit));
        }
        dialogFacts.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogFacts.setCancelable(false);
    }

    ///-SET-DIALOG///


    ///DONATE///

    public void setLinkDonate() {
        Intent goDonate = new Intent(Intent.ACTION_VIEW, Uri.parse("https://donate.stream/sfmgame"));
        startActivity(goDonate);
    }

    ///-DONATE///


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


    ///SNAIL-EXIT///

    public void snailEx(int i) {
        if(snailRare) {
            Toast.makeText(getApplicationContext(), "1000-7?", Toast.LENGTH_SHORT).show();
        }
        else {

            if (i == 0) {
                Toast.makeText(getApplicationContext(), "Что тебе от меня нужно? Я всего лишь улитка", Toast.LENGTH_SHORT).show();
            } else if (i == 1) {
                Toast.makeText(getApplicationContext(), "Еще раз нажмешь на меня и я выключу тебе игру!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "А я предупреждала, что не нужно в меня тыкать!", Toast.LENGTH_SHORT).show();
                this.finishAffinity();
                arrayFacts.clear();
                textArray.clear();
            }
        }
    }

    ///-SNAIL-EXIT///

    ///ANIM///

    public void goAnim() {
        Button next = findViewById(R.id.button2);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        next.startAnimation(pulse);

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

    public void textAnim() {
        TextView text = findViewById(R.id.textTask);
        Animation slidedown = AnimationUtils.loadAnimation(this, R.anim.slidedown);
        text.startAnimation(slidedown);
    }

    ///-ANIM///

}