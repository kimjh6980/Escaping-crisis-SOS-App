package com.example.jinhyukkim.sos;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class profile_manual extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_manual);
        this.setFinishOnTouchOutside(true);
    }

    public void profilehelpclose(View view) {
        this.finish();
    }
}
