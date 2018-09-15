package com.example.ynl.holamundo;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LastActivity extends AppCompatActivity {
    public int id_camera = 0;
    static  Camera camera = null;
    FrameLayout frameLayout;
    ShowCamera showCamera;

    private static final int PICK_IMAGE =100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);
        //
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        //abrir camara
        camera = Camera.open(id_camera);
        showCamera = new ShowCamera(this,camera);
        frameLayout.addView(showCamera);
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

    public void captureImage(View v){
        if(camera!=null){
            camera.takePicture(null,null, mPictureCallback);
        }
    }

    public void switchCameraClick(View v){
        //camera.stopPreview();
        //camera.release();
        //swap the id of the camera to be used
        if(id_camera == 1){
            id_camera= 0;
        }else {
            id_camera = 1;
        }
        //camera.startPreview();
        //camera = Camera.open(id_camera);
        camera = Camera.open(id_camera);

        showCamera = new ShowCamera(this,camera);
        frameLayout.addView(showCamera);
        camera.startPreview();
    }

    public void openGallery(View v){
        Intent gallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);

        //this.sendBroadcast(gallery);
    }

}