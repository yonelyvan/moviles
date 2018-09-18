package com.example.ynl.camara;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class UploadPhoto extends AppCompatActivity {

        private Button m_upload_btn;
        private StorageReference m_storage;
        private static  final int GALLERY_INTENT = 1;
        public static final String TAG = "MESSAGE";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.upload_photo);
            //
            showImage();
            //
            m_storage = FirebaseStorage.getInstance().getReference();
            m_upload_btn = (Button) findViewById(R.id.m_upload_btn);
        }

        public void btn_upload(View v){
            //abre la galeria para seleccionar una foto
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,GALLERY_INTENT);
        }


        @Override
        protected  void onActivityResult(int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode,resultCode,data);
            //verificar si la imagen fue seleccionada
            if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
                Uri uri = data.getData();
                StorageReference filePath = m_storage.child("fotos").child(uri.getLastPathSegment());

                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(com.example.ynl.camara.UploadPhoto.this,"Se subio exitosamente la foto",Toast.LENGTH_LONG).show();
                    }
                });
                //uri to path
                String path = uri_to_path(uri);
                Log.e(MainActivity.TAG,path);
                ImageView imageView = (ImageView) findViewById(R.id.imgChoosed);
                imageView.setImageBitmap(BitmapFactory.decodeFile(path));

            }
        }

    public void showImage(){
        Intent intent = getIntent();
        String imgpath = intent.getStringExtra(MainActivity.IMGPATH);
        Log.e(MainActivity.TAG,imgpath);
        ImageView imageView = (ImageView) findViewById(R.id.imgChoosed);
        imageView.setImageBitmap(BitmapFactory.decodeFile(imgpath));

    }

    public String uri_to_path(Uri uri){
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        return picturePath;
    }


}