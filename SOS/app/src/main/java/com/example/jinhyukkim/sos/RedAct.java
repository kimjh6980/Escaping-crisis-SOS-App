package com.example.jinhyukkim.sos;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import java.util.Date;

import static java.lang.System.currentTimeMillis;

public class RedAct extends AppCompatActivity {
    MediaPlayer mp = new MediaPlayer(); // 사이렌을 울리기 위해

    SharedPreferences soundsetting;
    SharedPreferences.Editor soundeditor;

    String setsound = null;

    Intent intent = getIntent();

    boolean onoff = false;
    String smsText = "응급상황이에요";
    String setnum = "010-2516-6987";

    String setsoundalarm = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red);

        soundsetting = getSharedPreferences("soundsetting", 0);
        setsound = soundsetting.getString("soundset", "");

//        Toast.makeText(this, setsound, Toast.LENGTH_SHORT).show();

        switch (setsound)   {
            default:
                setsoundalarm = "사이렌";
                mp = MediaPlayer.create(this, R.raw.siren);
                break;
            case "1":
                setsoundalarm = "사이렌";
                mp = MediaPlayer.create(this, R.raw.siren);
                break;
            case "2":
                setsoundalarm = "호루라기";
                mp = MediaPlayer.create(this, R.raw.siren2);
                break;
            case "3":
                setsoundalarm = "여자비명";
                mp = MediaPlayer.create(this, R.raw.siren3);
                break;
        }
        mp.setLooping(true);
        Toast.makeText(this, "현재 설정된 소리는 ["+setsoundalarm+"]입니다", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void call(View v) {  //응급상황 전화및 문자
        // 전화걸기
        Intent callintent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + setnum));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else{   startActivity(callintent);  }

        // 문자보내기
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                Location lastLocation = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastLocation != null) {   // 한번 출력
                    Double latitude = lastLocation.getLatitude();
                    Double longitude = lastLocation.getLongitude();

                    long nowT = currentTimeMillis();
                    Date date = new Date(nowT);
                    SimpleDateFormat nowtime = new SimpleDateFormat("mm:ss");
                    String getTime = nowtime.format(date);

                    smsText = getTime + "에 제 위치는\n" + latitude + " / " + longitude;
                    sendSMS(setnum, smsText);
                }

            }
        } catch (SecurityException ex) {
        } catch (Exception ex) {
        }

    }

    public void sendSMS(String smsNumber, String smsText) {
        PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT_ACTION"), 0);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED_ACTION"), 0);


        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        // 전송 성공
                        Toast.makeText(RedAct.this, "전송 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        // 전송 실패
                        Toast.makeText(RedAct.this, "전송 실패", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // 서비스 지역 아님
                        Toast.makeText(RedAct.this, "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // 무선 꺼짐
                        Toast.makeText(RedAct.this, "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        // PDU 실패
                        Toast.makeText(RedAct.this, "PDU Null", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT_ACTION"));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        // 도착 완료
                        Toast.makeText(RedAct.this, "SMS 도착 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // 도착 안됨
                        Toast.makeText(RedAct.this, "SMS 도착 실패", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_DELIVERED_ACTION"));


        SmsManager mSmsManager = SmsManager.getDefault();
        mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);
    }

    public void siren(View v) { // 비상사이렌 울리기
        if(onoff == false)    // 소리 키기
        {
            Toast.makeText(this, setsoundalarm, Toast.LENGTH_SHORT).show();
            mp.start();
            onoff = true;
        }
        else if(onoff == true)    // 소리 끄기
        {
            mp.stop();

            switch (setsound)   {
                case "0":
                    setsoundalarm = "사이렌";
                    mp = MediaPlayer.create(this, R.raw.siren);
                    break;
                case "1":
                    setsoundalarm = "사이렌";
                    mp = MediaPlayer.create(this, R.raw.siren);
                    break;
                case "2":
                    setsoundalarm = "호루라기";
                    mp = MediaPlayer.create(this, R.raw.siren2);
                    break;
                case "3":
                    setsoundalarm = "여자비명";
                    mp = MediaPlayer.create(this, R.raw.siren3);
                    break;
            }
            mp.setLooping(true);
            onoff = false;
        }
    }

    @Override
    protected void onDestroy() {    // 종료시
        mp.stop();
        mp.release();
        super.onDestroy();
    }
}
