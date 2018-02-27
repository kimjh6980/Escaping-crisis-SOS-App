package com.example.jinhyukkim.sos;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class locationDB extends AppCompatActivity {

    /*
    SQLiteDatabase db;
    MYSQLiteOpenHelper helper;
    ListView locationdblistview;
    String listmsg=null;
    */
    private DBHelper helper;
    private SQLiteDatabase db;

    ArrayList<String> locatedblist;         // 리스트를 위한 data

    public static ListView locationlistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_db);  }
/*
        locationlistView = (ListView)findViewById(R.id.locationdblist);

        locatedblist = new ArrayList<>();
        ArrayAdapter<String> Adapter;
        Adapter = new ArrayAdapter<String>(locationDB.this, android.R.layout.simple_list_item_1, locatedblist);

        DBHandler dbhandler = DBHandler.open(this);

        Cursor cursor = dbhandler.select("cars");
        startManagingCursor(cursor);

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "데이터가 없습니다.", Toast.LENGTH_LONG).show();
        } else {
            String name = cursor.getString(cursor.getColumnIndex("car_name"));
            Toast.makeText(this, "자동차 이름 " + name, Toast.LENGTH_LONG).show();
            locatedblist.add(name);

            locationlistView.setAdapter(Adapter);
        }
        dbhandler.close();
    }

    public Cursor select(int id) throws SQLException {
        Cursor cursor = db.query(true, "cars", new String[]{"_id", "car_name"}, "_id"
                + "=" + id, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

        /*
        ArrayList<String> items = new ArrayList<String >();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

        locationdblistview = (ListView)findViewById(R.id.locationdblist);
        locationdblistview.setAdapter(adapter);

        helper = new MYSQLiteOpenHelper(locationDB.this,
                "Locationlist.db",
                null,
                1);

        db = helper.getReadableDatabase();
        Cursor c = db.query("location", null, null, null, null, null, null);
        while(c.moveToNext())   {
            int _id = c.getInt(c.getColumnIndex("_id"));
            String getTime = c.getString(c.getColumnIndex("getTime"));
            double longitude = c.getDouble(c.getColumnIndex("longitude"));
            double latitude = c.getDouble(c.getColumnIndex("latitude"));
            listmsg = _id + " / " + getTime + " / X:" + longitude + " / Y:" + latitude;
            items.add("List" + listmsg);
            adapter.notifyDataSetChanged();

        };*/

    public void locationDBClose(View view) {
        this.finish();
    }
}
