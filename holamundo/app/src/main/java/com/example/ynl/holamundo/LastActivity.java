package com.example.ynl.holamundo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LastActivity extends AppCompatActivity {
    //public static final int REQUEST_IMAGE_CAPTURE= 1;

    static  Camera camera = null;
    FrameLayout frameLayout;
    ShowCamera showCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);
        //
        frameLayout = (FrameLayout)findViewById(R.id.camera_preview);
        //abrir camara
        camera = Camera.open();
        showCamera = new ShowCamera(this,camera);
        frameLayout.addView(showCamera);
    }




    /////////////////personalizacion de camara





}









    /*
    //abre la camara
    public void takePhoto(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected  void  onActivityResult(int requestCode, int resultcode, Intent data){
        if( requestCode == REQUEST_IMAGE_CAPTURE && resultcode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView mImageView = (ImageView) findViewById(R.id.imageView);
            mImageView.setImageBitmap(imageBitmap);


            // guardar foto
            String path = Environment.getExternalStorageDirectory().toString();
            Log.e("MY_TAG",path);
            // /storage/emulated/0/DCIM/mycamera/   INTERNO
            // /storage/3231-3834/DCIM/mycamera/    SD
            path = path +"/DCIM/mycamera";
            File photoFile = new File(path,"mi_img.jpg");
            try {
                OutputStream fOut = new FileOutputStream(photoFile);
                imageBitmap.compress(Bitmap.CompressFormat.PNG,98,fOut);
            }catch (IOException ex){
                Log.e("ERROR",ex.getMessage());
                return;
            }
        }
    }*/
