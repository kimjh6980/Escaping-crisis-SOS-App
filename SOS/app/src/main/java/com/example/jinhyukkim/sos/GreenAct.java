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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import static com.example.jinhyukkim.sos.R.id.map;
import static java.lang.System.currentTimeMillis;

// 책보고 처음부터 다시할것
public class GreenAct extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // 지도 사용 선언

    SQLiteDatabase db;


    SharedPreferences msgsetting;
    SharedPreferences.Editor msgeditor;

    int settime;
    boolean timestart = false;      // 타이머 시작인지 확인
    int listcount = 0;              // 리스트 카운트
    int timeend = 0;

    ArrayList<String> data;         // 리스트를 위한 data
    ArrayAdapter<String> Adapter;   // 시간 갱신을 위한 Adapter

    String name;
    String number;
    private Timer timer;

    String smsText;
    String carName;
    String setmsg = "지금 저는";

    // 에러나서 일단 뺌 boolean liston = false;         // 리스트가 있는지 없는지 확인
    //  private LocationManager locationManager;
    //  private String provider;
    //  boolean locationflag = false;   // 반복 여부

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        final EditText TimerText = (EditText) findViewById(R.id.TImerText);    // 레이아웃 내 타이머 텍스트창 id값 받아서 TimerText에 넣기
        //ListView locationlist = (ListView) findViewById(R.id.locationList);  // 레이아웃 내 리스트뷰 id값 받아서 locationlist에 넣기

        final Button timerbtn = (Button) findViewById(R.id.SetTimer);
        Button nowlocbtn = (Button) findViewById(R.id.locationSend);

        msgsetting = getSharedPreferences("msgset", 0);
        setmsg = msgsetting.getString("msg", "");

        timerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timenum = TimerText.getText().toString();
                if (timenum.length() == 0) {
                    Toast.makeText(GreenAct.this, "시간기입필요", Toast.LENGTH_LONG).show();
                } else {
                    settime = Integer.parseInt(timenum);
                    if (settime == 0) {
                        // 시간 기입하라고 팝업창
                        Toast.makeText(GreenAct.this, "time0", Toast.LENGTH_LONG).show();
                    } else {
                        if (timerbtn.getText().equals("타이머 시작")) {
                            Toast.makeText(GreenAct.this, settime + "분 기입됨", Toast.LENGTH_LONG).show();
                            timerbtn.setText("타이머 작동중");
                            timeend = settime * 3;
                            timestart = true;
                            data = new ArrayList<>();
                            Adapter = new ArrayAdapter<String>(GreenAct.this, android.R.layout.simple_list_item_1, data);
                            //------- db생성되야됨

                            phonebook();
                        } else {
                            Toast.makeText(GreenAct.this, "타이머 중지요청", Toast.LENGTH_LONG).show();
                            timerbtn.setText("타이머 시작");
                            listcount = 0;
                            timeend = 0;
                            timestart = false;
                        }
                        // gps타이머 시작
                        //----------------------------- 클릭시 바로시작 요청
                    }
                }
            }
        });

        nowlocbtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             startLocationService();
                                         }
                                     }
        );
    }

    // 버튼 클릭시 현 위치 바로 받기
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startLocationService() {
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

                    smsText = setmsg + getTime + "\n" + latitude + " / " + longitude;
                    phonebook();
                    Toast.makeText(this, "send time :" + getTime + "\nX:" + latitude + " /Y:" + longitude, Toast.LENGTH_SHORT).show();
                }

            }
        } catch (SecurityException ex) {
        } catch (Exception ex) {
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); // 임시추가
        LocationListener locationListener = new LocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onLocationChanged(Location location) {
                updateMap();    // 한번 줘보자
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                alertStatus(provider);
            }

            @Override
            public void onProviderEnabled(String provider) {
                alertProvider(provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                checkPrvider(provider);
            }

        };

        String locationPorivder = LocationManager.NETWORK_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            locationManager.requestLocationUpdates(locationPorivder, 2000, 0, locationListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateMap() {
//        Toast.makeText(this, "updateMap", Toast.LENGTH_SHORT).show();
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
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                String locationProvider = LocationManager.NETWORK_PROVIDER;
                locationManager.requestLocationUpdates(locationProvider, 5000, 0, locationListener);

                Button timerbtn = (Button) findViewById(R.id.SetTimer);

                if (location != null) {   // 반복 출력
                    double latitude = 0;
                    double longitude = 0;

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
//                    Toast.makeText(this, "위도경도받음", Toast.LENGTH_SHORT).show();

                    final LatLng LOC = new LatLng(latitude, longitude); // 위도경도 위치 받아서 LOC에 기입

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LOC, 16)); // 카메라 포커싱 변경

                    // 기존마커지우기 필요
                    mMap.clear();
//                    Toast.makeText(this, "update", Toast.LENGTH_SHORT).show();

                    Marker mk = mMap.addMarker(new MarkerOptions()  // LCO위치에 마커 추가
                            .position(LOC)
                            .title("현재 위치"));
                    mk.showInfoWindow();    // 윈도우창에 보여주기
//                    Toast.makeText(this, "위치찍음", Toast.LENGTH_SHORT).show();
                    if (timestart == true) {
                        nowlocate(latitude, longitude);
                    }
                }

            }
        } catch (SecurityException ex) {
        } catch (Exception ex) {
        }

        //-----------------------
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    }

    public void checkPrvider(String provider) {
        Toast.makeText(this, provider + " 의한 위치서비스가 꺼져 있습니다. 켜주세요", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public void alertProvider(String provider) {
        Toast.makeText(this, provider + "서비스가 켜졌습니다!", Toast.LENGTH_LONG).show();
    }

    public void alertStatus(String provider) {
        Toast.makeText(this, "위치서비스가" + provider + "로 변경되었습니다!", Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void nowlocate(double latitude, double longitude) {
        final Button timerbtn = (Button) findViewById(R.id.SetTimer);
        long nowT = currentTimeMillis();
        Date date = new Date(nowT);
        SimpleDateFormat nowtime = new SimpleDateFormat("mm:ss");
        String getTime = nowtime.format(date);
        if (listcount != timeend) {
//            Toast.makeText(this, "timeend=" + timeend + "/ listcoount" + listcount, Toast.LENGTH_LONG).show();
            //------------------
            listcount++;
            data.add(listcount + "/" + getTime + "/" + latitude + "/" + longitude);

            //---------여기서 해당 리스트의 db에 값이 저장되게 해야됨
            ListView locationlist = (ListView) findViewById(R.id.locationList);
            locationlist.setAdapter(Adapter);
        } else {
            DBHandler dbhandler = DBHandler.open(this);
            timestart = false;
            TextView getnumber = (TextView) findViewById(R.id.setnumber);
            number = getnumber.getText().toString();
            carName = getTime + "/ X : " + latitude + "/ Y : " + longitude;
            smsText = setmsg + getTime + "\n" + latitude + " / " + longitude;

//            Toast.makeText(this, "timeend=" + "timeend" + timestart, Toast.LENGTH_LONG).show();
            listcount = 0;
            timeend = 0;
            sendSMS(number, smsText);
            timerbtn.setText("타이머 시작");

            long cnt = dbhandler.insert(carName);

            if (cnt == -1) {
                Toast.makeText(this,
                        "db가 테이블에 추가되지 않았습니다.",
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(this,
                        "db가 테이블에 추가되었습니다.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }



    public void phonebook() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Cursor cursor = getContentResolver().query(data.getData(), new String[]{
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER
            }, null, null, null);
            cursor.moveToFirst();
            name = cursor.getString(0);
            number = cursor.getString(1);
            cursor.close();
//            Toast.makeText(this, "timestart ; " + timestart, Toast.LENGTH_SHORT).show();
            if (timestart == true) {
                TextView setname = (TextView) findViewById(R.id.setname);
                TextView setnumber = (TextView) findViewById(R.id.setnumber);
                setname.setText(name);
                setnumber.setText(number);
                Toast.makeText(this, name + "/" + number, Toast.LENGTH_SHORT).show();
                starting();
            } else if (timestart == false) {
//                Toast.makeText(this, "일회용PhoneBook", Toast.LENGTH_SHORT).show();
                sendSMS(number, smsText);  // 시간 nowTime, nowX, nowY
            }
            super.onActivityResult(requestCode, resultCode, data);
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
                        Toast.makeText(GreenAct.this, "전송 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        // 전송 실패
                        Toast.makeText(GreenAct.this, "전송 실패", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // 서비스 지역 아님
                        Toast.makeText(GreenAct.this, "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // 무선 꺼짐
                        Toast.makeText(GreenAct.this, "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        // PDU 실패
                        Toast.makeText(GreenAct.this, "PDU Null", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(GreenAct.this, "SMS 도착 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // 도착 안됨
                        Toast.makeText(GreenAct.this, "SMS 도착 실패", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_DELIVERED_ACTION"));


        SmsManager mSmsManager = SmsManager.getDefault();
        mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void starting() {
        final Button timerbtn = (Button) findViewById(R.id.SetTimer);
        if (timestart == true) {
            if (listcount == timeend) {
                timestart = false;
//                Toast.makeText(this, "timeend=" + "timeend" + timestart, Toast.LENGTH_LONG).show();
                listcount = 0;
                timerbtn.setText("타이머 시작");
            } else {
//                Toast.makeText(this, "nowlcate실행", Toast.LENGTH_SHORT).show();
                updateMap();

            }

        }
    }
}
