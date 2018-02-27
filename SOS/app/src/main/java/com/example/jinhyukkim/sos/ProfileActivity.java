package com.example.jinhyukkim.sos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import java.io.File;

public class ProfileActivity extends TabActivity {
    //사진으로 전송시 되돌려 받을 번호
    static int REQUEST_PICTURE=1;
    //앨범으로 전송시 돌려받을 번호
    static int REQUEST_PHOTO_ALBUM=2;
    //첫번째 이미지 아이콘 샘플 이다.
    static String SAMPLEIMG="picicon";

    ImageView iv;
    Dialog dialog;

    final Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        TabHost tabHost = getTabHost();//(1)

        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec("Tab1").setIndicator("사용자 프로필");
        tabSpec1.setContent(R.id.tab1);
        tabHost.addTab(tabSpec1);

        iv=(ImageView) findViewById(R.id.piciconimage);
        iv.setImageResource(R.drawable.picicon);


        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("Tab2").setIndicator("개발자 안내");
        tabSpec2.setContent(R.id.tab2);
        tabHost.addTab(tabSpec2);

        Drawable img = getResources().getDrawable(R.mipmap.ic_launcher);

        tabHost.setCurrentTab(0);
    }

    public void profilehelp(View view) {
        Intent profilehelp = new Intent(this, profile_manual.class);
        startActivity(profilehelp);
    }

    public void profileok(View view) {
        this.finish();
    }
    public void profilecancel(View view) {
        this.finish();
    }

    public void selectimage(View v) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("찍을래요 고를래요?");
        alertDialogBuilder.setMessage("사진을 찍겠습니까?\n 사진을 고르시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("카메라",
                        new DialogInterface.OnClickListener()   {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                takePicture();
                            }
                        })
                .setNegativeButton("앨범",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                photoAlbum();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void photoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PHOTO_ALBUM);
    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_PICTURE);
    }
    Bitmap loadPicture()    {
        File file = new File(Environment.getExternalStorageDirectory(), SAMPLEIMG);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_PICTURE)  {
                //iv.setImageBitmap(loadPicture());
                Bitmap bmp = (Bitmap)data.getExtras().get("data");
                iv.setImageBitmap(bmp);
            }
            if(requestCode == REQUEST_PHOTO_ALBUM)  {
                iv.setImageURI(data.getData());
            }
        }
    }
}