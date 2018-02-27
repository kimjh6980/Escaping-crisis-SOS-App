package com.example.jinhyukkim.sos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Message_Activity extends ActionBarActivity {

    TextView systemsettv;
    TextView usersettv;

    SharedPreferences msgsetting;
    SharedPreferences.Editor msgeditor;

    String usersetmsg = "직접 입력";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_);

        final String[] usermsgset = {null};

        systemsettv = (TextView)findViewById(R.id.systemsetmessage);

        String[] str = getResources().getStringArray(R.array.msgset_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, str);
        Spinner msgspinner = (Spinner)findViewById(R.id.msgspinner);
        msgspinner.setAdapter(adapter);

        msgspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()   {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                systemsettv.setText((CharSequence) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }


    public void msgok(View view) {
        msgsetting = getSharedPreferences("msgset", 0);
        msgeditor = msgsetting.edit();
        msgeditor.putString("msg", systemsettv.getText().toString());
        msgeditor.commit();
        Toast.makeText(this, "메세지가 설정되었습니다 :" + msgsetting.getString("msg", ""), Toast.LENGTH_SHORT).show();
        this.finish();
    }

    public void msgcancel(View view) {
        Toast.makeText(this, "설정이 취소되었습니다", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
