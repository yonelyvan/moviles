package com.example.ynl.mnet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MnetActivity extends AppCompatActivity {
    FragmentNotificaciones fragmentNotificaciones;
    FragmentDashboard fragmentDashboard;
    FragmentPlus fragmentPlus;
    FragmentMuro fragmentMuro;
    FragmentChat fragmentChat;

    public static final String TAG = "MNET";
    public String IMGPATH="null";
    private static int RESULT_LOAD_IMAGE = 1;




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_notifications:
                    Log.e(TAG,"Notificaciones");
                    setFragment(fragmentNotificaciones);
                    return true;
                case R.id.navigation_dashboard:
                    Log.e(TAG,"dashboard");
                    setFragment(fragmentDashboard);
                    return true;
                case R.id.navigation_plus:
                    Log.e(TAG,"plus");
                    setFragment(fragmentPlus);
                    return true;
                case R.id.navigation_muro:
                    setFragment(fragmentMuro);
                    Log.e(TAG,"MURO");
                    return true;
                case R.id.navigation_chat:
                    Log.e(TAG,"CHAT");
                    setFragment(fragmentChat);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnet);
        //
        getSupportActionBar().hide();

        //
        fragmentNotificaciones = new FragmentNotificaciones();
        fragmentDashboard = new FragmentDashboard();
        fragmentPlus = new FragmentPlus();
        fragmentMuro = new FragmentMuro();
        fragmentChat = new FragmentChat();

        //
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



    }

    public void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
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
            Log.e("SHOWIMG::",imgpath);
            IMGPATH = imgpath;
            ImageView imageView = (ImageView) findViewById(R.id.img_to_upĺoad);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));



            //intent.putExtra(DATA_IMG, data);

            Log.e(TAG, "IMAGEN GALLERY "+picturePath );//URI
            //startActivity(intent);
        }
    }






}