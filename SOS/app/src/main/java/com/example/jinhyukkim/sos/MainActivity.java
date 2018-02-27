package com.example.jinhyukkim.sos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
    }

    public void Greenlayout(View v) {
        Intent green = new Intent(this, GreenAct.class);
        startActivity(green);
    }

    public void orangelayout(View view) {
        Intent orange = new Intent(this, OrangeAct.class);
        startActivity(orange);
    }

    public void redlayout(View view) {
        Intent red = new Intent(this, RedAct.class);
        startActivity(red);
    }

    public void appset(View view) {
        Intent appsetup = new Intent(this, SetupActivity.class);
        startActivity(appsetup);
    }
}
