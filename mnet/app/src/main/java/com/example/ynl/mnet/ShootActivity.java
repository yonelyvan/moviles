package com.example.ynl.mnet;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class ShootActivity extends AppCompatActivity {

    public static final String TAG = "MESSAGE";
    public static final String IMGPATH="null"; //static se crea una ves para n objetos
    public static final Intent DATA_IMG =null;
    //public static final int REQUEST_IMAGE_CAPTURE= 1;
    private static int RESULT_LOAD_IMAGE = 1;
    public static final int REQUEST_IMAGE_CAPTURE= 1;

    //static Camera camera = null;
    FrameLayout frameLayout;
    CameraPreview mPreview;
    public int id_camera = 0;
    private static final int PICK_IMAGE =100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_shoot);
        }catch (Exception e){

            Log.e(TAG,e.getMessage());
        }

        //full screem
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

        //String imgpath_lastimg = mPreview.LAST_IMG_TAKEN;

        //message img path and go to an activity
       /// Intent intent1 = getIntent();
       //// Intent intent = new Intent(this,UploadPhoto.class);

        //intent.putExtra("MC_LAST_IMG_TAKEN",imgpath_lastimg );//mPreview.LAST_IMG_TAKEN);
        //@TODO
        //Log.e(MainActivity.TAG, "LASSST:: "+imgpath_lastimg);//URI
        //startActivity(intent);

    }



    public void openGallery(View v){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void switchCamera(View v) {
        mPreview.switchCamera();
    }

    //seleccion de imagen de galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            Log.e(TAG,selectedImage.toString());//URI

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //message img path and go to an activity
            //@TODO got
            ///Intent intent = new Intent(this,UploadPhoto.class);
            ///intent.putExtra(IMGPATH,picturePath);
            //intent.putExtra(DATA_IMG, data);

            Log.e(TAG, "IMAGEN GALLERY "+picturePath );//URI
            ///startActivity(intent);
        }
    }


}
