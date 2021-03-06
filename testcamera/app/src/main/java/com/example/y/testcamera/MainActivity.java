package com.example.y.testcamera;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MESSAGE";
    public static final String IMGPATH="null"; //static se crea una ves para n objetos
    //public static final int REQUEST_IMAGE_CAPTURE= 1;
    private static int RESULT_LOAD_IMAGE = 1;
    public static final int REQUEST_IMAGE_CAPTURE= 1;

    static Camera camera = null;
    FrameLayout frameLayout;
    CameraPreview mPreview;
    public int id_camera = 0;
    private static final int PICK_IMAGE =100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        mPreview = findViewById(R.id.preview);

        /*
        //camara
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        //abrir camara
        camera = Camera.open(id_camera);
        showCamera = new ShowCamera(this,camera);
        frameLayout.addView(showCamera);
        */

    }





    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File picture_file = getOutputMediaFile();
            if(picture_file == null){
                return;
            }else{
                try {
                    FileOutputStream fos = new FileOutputStream(picture_file);
                    fos.write(data);
                    fos.close();

                    camera.startPreview();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    };

    private File getOutputMediaFile(){
        String state = Environment.getExternalStorageState();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            return null;
        }else {
            File folder_gui = new File(Environment.getExternalStorageDirectory() + "/DCIM/MYCAMERA");
            if(!folder_gui.exists()){
                folder_gui.mkdir();
            }
            File outputFile = new File(folder_gui,"temp1.jpg");
            return outputFile;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void capturePhoto(View v){
        mPreview.takePicture();
    }



    public void openGallery(View v){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void switchCamera(View v) {
        mPreview.switchCamera();
    }




}
