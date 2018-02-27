package com.example.jinhyukkim.sos;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class Munual_Activity extends AppCompatActivity {

    int previous_x = 0;
    int next_x = 0;
    int result_x;
    int screencount = 0;
    boolean screenchange = false;

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        setContentView(R.layout.activity_munual_);
        img = (ImageView)findViewById(R.id.manual_image);

        Toast.makeText(this, "드래그 하시면 화면이 넘어갑니다.\n버튼을 누르면 화면이 바뀝니다.", Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previous_x = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                screenchange = true;
                next_x = (int) event.getX();
                //  Toast.makeText(this,"pre "+Integer.toString(UpX),Toast.LENGTH_SHORT).show();
                break;

        }
        if (screenchange) {
            SystemClock.sleep(100); // 드래그 입력이 들어갈때까지의 대기 시간!
            screenchange = false;

            if (previous_x > next_x) {
                if(screencount > 6)
                {   screencount = -1;    }
                screencount = screencount + 1;
            }
            if (previous_x < next_x) {
                if(screencount < 0)
                {   screencount = 7;    }
                screencount = screencount - 1;
            }
            switch(screencount) {
                case 0:
                    img.setBackgroundResource(R.drawable.manual_0);
                    break;
                case 1:
                    img.setBackgroundResource(R.drawable.manual_1);
                    break;
                case 2:
                    img.setBackgroundResource(R.drawable.manual_2);
                    break;
                case 3:
                    img.setBackgroundResource(R.drawable.manual_3);
                    break;
                case 4:
                    img.setBackgroundResource(R.drawable.manual_4);
                    break;
                case 5:
                    img.setBackgroundResource(R.drawable.manual_5);
                    break;
                case 6:
                    img.setBackgroundResource(R.drawable.manual_6);
                    break;
            }
        }
        return false;
    }

    public void manualg(View view) {
        screencount = 0;
        img.setBackgroundResource(R.drawable.manual_0);
    }
    public void manualo(View view) {
        screencount = 4;
        img.setBackgroundResource(R.drawable.manual_4);
    }
    public void manualr(View view) {
        screencount = 5;
        img.setBackgroundResource(R.drawable.manual_5);
    }
}