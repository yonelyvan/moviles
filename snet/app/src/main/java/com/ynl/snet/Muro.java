package com.ynl.snet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class Muro extends AppCompatActivity {
    public static final String user="names";
    TextView txtUser;
    private FirebaseAuth mAuth;
    private static int RESULT_LOAD_IMAGE = 1;
    public static final String TAG = "MESSAGE";
    public String IMGPATH="null"; //static se crea una ves para n objetos
    private StorageReference m_storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muro);

        txtUser =(TextView)findViewById(R.id.txt_user);
        String user = getIntent().getStringExtra("names");
        txtUser.setText("¡Bienvenido "+ user +"!");
        //verificacion de session
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser myuserobj){
        Log.e("::::::AVISO::",myuserobj.getEmail());
    }


    public void sign_out(View v) {
        mAuth.signOut();
        Toast.makeText(Muro.this, "Sesión cerrada", Toast.LENGTH_LONG).show();
        try {
            updateUI(mAuth.getCurrentUser());
        } catch (Exception e) {
            Log.e("SNET", "exception", e);
        }
        go_to_login();
    }

    public void go_to_login(){
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
    }


    public void openGallery(View v){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);

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
            //Intent intent = new Intent(this,UploadPhoto.class);
            //intent.putExtra(IMGPATH,picturePath);

            //Intent intent = getIntent();
            String imgpath = picturePath; //intent.getStringExtra(MainActivity.IMGPATH);
            //Log.e("SHOWIMG::",imgpath);
            IMGPATH = imgpath;
            ImageView imageView = (ImageView) findViewById(R.id.imgProfile);
            imageView.setImageBitmap(BitmapFactory.decodeFile(IMGPATH));



            //intent.putExtra(DATA_IMG, data);

            Log.e(TAG, "IMAGEN GALLERY "+picturePath );//URI
            //startActivity(intent);
        }
    }



    public void cargar_foto(View v){
        //Intent intent = getIntent();
        String imgpath = IMGPATH;//intent.getStringExtra(MainActivity.IMGPATH);
        Log.e("ERRORRRRRRRRR::",imgpath);
        File IMG_file = new File(imgpath);
        Log.e("ERRORRRRRRRRR::",IMG_file.getPath());
        Uri uri = Uri.fromFile(IMG_file);
        //Uri uri = data.getData();

        final ProgressDialog progressDialog =new ProgressDialog(this);
        progressDialog.setTitle("Cargando...");
        progressDialog.show();


        StorageReference filePath = m_storage.child("fotos").child(uri.getLastPathSegment());


        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Muro.this,"Se subio exitosamente la foto",Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Muro.this,"Failed"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = 100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Cargando "+ (int)progress + "%");
                    }
                });


        //uri to path
        /*
        String path = MainActivity.IMGPATH; //uri_to_path(uri);
        Log.e(MainActivity.TAG,path);
        ImageView imageView = (ImageView) findViewById(R.id.imgChoosed);
        imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        */
    }


}
