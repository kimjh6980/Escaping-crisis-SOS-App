package com.example.jinhyukkim.sos;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class OrangeAct extends AppCompatActivity {

    MediaPlayer mp;
    boolean onoff = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orange);

        mp = MediaPlayer.create(this, R.raw.fakecall);
        mp.setLooping(false);

    }

    public void fakecall(View view) {
        if(onoff == false)   {
            mp.start();
            onoff = true;
        }   else if(onoff == true)   {
            mp.pause();
            onoff = false;
        }
    }

    @Override
    protected void onDestroy() {
        mp.release();
        super.onDestroy();
    }
}
