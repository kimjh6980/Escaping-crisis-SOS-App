package com.example.jinhyukkim.sos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SetupActivity extends AppCompatActivity {

    SharedPreferences soundsetting;
    SharedPreferences.Editor soundeditor;

    static Uri uri;
    TextView weathertv;
    ImageView weatherimg;

    public static String setsound = null;

    AlertDialog alertDialog1;
    CharSequence[] values = {"사이렌","호루라기", "여자비명"};

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        SimpleDateFormat sdt = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();

        String serviceUrl = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData";
        String serviceKey = "YiTsCc8u8%2Fe817wYreMVBf6ChuAPG%2F9o%2B7VYQVYAGActLV2%2B3%2FtuZ6N7Gy6nhEaETBAKE8ctYMESCOQoocy02g%3D%3D";
        String test = "TEST_SERVICE_KEY";
        String base_date =sdt.format(cal.getTime());
        String base_time = "0500";
        String nx = "60";
        String ny = "74";
        String strUrl = serviceUrl + "?ServiceKey=" + serviceKey + "&ServiceKey=TEST_SERVICE_KEY" + "&base_date=" + base_date + "&base_time=" + base_time + "&nx=" + nx + "&ny="+ny
                +"&numOfRows=1&pageNo=1&_type=xml";

        new DownloadWebpageTask().execute(strUrl);
        weathertv = (TextView)findViewById(R.id.weather);
        weatherimg = (ImageView)findViewById(R.id.weathericon);

    }

    public void manual(View view) {
        Intent manual = new Intent(this, Munual_Activity.class);
        startActivity(manual);
    }

    public void message(View view) {
        Intent message = new Intent(this, Message_Activity.class);
        startActivity(message);
    }

    public void sirensound(View view) {
        sirenDialog() ;
    }

    public void sirenDialog() {    // 사이렌 설정

        AlertDialog.Builder builder = new AlertDialog.Builder(SetupActivity.this);

        builder.setTitle("Select Your Choice");

        AlertDialog.Builder builder1 = builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        Toast.makeText(SetupActivity.this, "응급소리가 사이렌으로 설정되었습니다.", Toast.LENGTH_SHORT).show();;
                        soundsetting = getSharedPreferences("soundsetting", 0);
                        soundeditor = soundsetting.edit();
                        soundeditor.putString("soundset", "1");
                        soundeditor.commit();
                        break;
                    case 1:
                        Toast.makeText(SetupActivity.this, "응급소리가 호루라기로 설정되었습니다.", Toast.LENGTH_SHORT).show();
                        soundsetting = getSharedPreferences("soundsetting", 0);
                        soundeditor = soundsetting.edit();
                        soundeditor.putString("soundset", "2");
                        soundeditor.commit();
                        break;
                    case 2:
                        Toast.makeText(SetupActivity.this, "응급소리가 여자비명으로 설정되었습니다.", Toast.LENGTH_SHORT).show();
                        soundsetting = getSharedPreferences("soundsetting", 0);
                        soundeditor = soundsetting.edit();
                        soundeditor.putString("soundset", "3");
                        soundeditor.commit();
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    public void site(View view) {
        Intent warningalarmsite = new Intent (Intent.ACTION_VIEW, Uri.parse("https://www.cppb.go.kr/"));
        startActivity(warningalarmsite);
    }

    public void setprofile(View view) {
        Intent profile = new Intent(this, ProfileActivity.class);
        startActivity(profile);
    }

    public void DBView(View view) {
        Intent dbview = new Intent(this, locationDB.class);
        startActivity(dbview);
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패";
            }
        }
        @Override
        protected void onPostExecute(String result) {
            String category = "";
            String fcstvalue;

            boolean bSet_sky = false;

            boolean bSet_category = false;
            boolean bSet_fcstvalue = false;

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                Log.e("e","1");
                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        // 찾는 태그와 같은 태그가 있는지!

                        String tag_name = xpp.getName();
                        if(tag_name.equals("category"))
                            bSet_category = true;
                        if(tag_name.equals("fcstValue")) {
                            bSet_fcstvalue = true;
                            Log.e("fcstvalue","true");
                        }

                    } else if (eventType == XmlPullParser.TEXT) {
                        // 찾은 태그값에 해당하는 내용 가져오기!
                        if(bSet_category){

                            category = xpp.getText();
                            if(category.equals("SKY")) bSet_sky = true;
                            else bSet_sky = false;
                        }

                        if(bSet_fcstvalue) {

                            fcstvalue = xpp.getText();
                            int value = Integer.valueOf(fcstvalue);
                            Log.e("값", fcstvalue);
                            switch (value % 4 + 1) {
                                case 1:
                                    weathertv.setText("오늘 날씨 : 맑음\n/ 즐거운 하루 되세요");
                                    weatherimg.setImageResource(R.drawable.weathericon1);
                                    break;
                                case 2:
                                    weathertv.setText("오늘 날씨 : 구름 조금\n/ 시원한 하루 되세요");
                                    weatherimg.setImageResource(R.drawable.weathericon2);
                                    break;
                                case 3:
                                    weathertv.setText("오늘 날씨 : 구름 많음\n/ 어두우니깐 조심하세요");
                                    weatherimg.setImageResource(R.drawable.weathericon3);
                                    break;
                                case 4:
                                    weathertv.setText("오늘 날씨 : 비\n/ 우산 챙겨가세요");
                                    weatherimg.setImageResource(R.drawable.weathericon4);
                                    break;
                            }
                            break;
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {

            }
        }

        private String downloadUrl(String myurl) throws IOException {

            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }

                return page;
            } finally {
                conn.disconnect();
            }
        }
    }

}
