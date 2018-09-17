package com.example.ynl.camara;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MESSAGE";
    public static final String IMGPATH="null"; //static se crea una ves para n objetos
    //public static final int REQUEST_IMAGE_CAPTURE= 1;
    private static int RESULT_LOAD_IMAGE = 1;

    static Camera camera = null;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    public int id_camera = 0;
    private static final int PICK_IMAGE =100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        /*
        //camara
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        //abrir camara
        camera = Camera.open(id_camera);
        showCamera = new ShowCamera(this,camera);
        frameLayout.addView(showCamera);
        */
    }

    //seleccion de imagen de galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            Log.e(MainActivity.TAG,selectedImage.toString());//URI

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //message img path and go to an activity
            Intent intent = new Intent(this,UploadPhoto.class);
            intent.putExtra(IMGPATH,picturePath);
            startActivity(intent);
        }
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


/*
    public void captureImage(View v){
        if(camera!=null){
            camera.takePicture(null,null, mPictureCallback);
        }
    }

    //old mode
    public void openGallery(View v){
        Intent gallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);

        //this.sendBroadcast(gallery);
    }*/

    public void openGallery(View v){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

}
