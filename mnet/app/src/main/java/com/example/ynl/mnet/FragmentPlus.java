package com.example.ynl.mnet;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPlus extends Fragment {
    private FirebaseUser user;
    public static final String TAG = "PLUS";

    private StorageReference m_storage;
    public String IMGPATH="null";

    private static int RESULT_LOAD_IMAGE = 1;

    private View view;


    public FragmentPlus() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        validar_sesion();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_plus, container, false);
        conf_btn_open_gallery();
        conf_btn_upload_photo();
        m_storage = FirebaseStorage.getInstance().getReference();

        return view;//inflater.inflate(R.layout.fragment_plus, container, false);
    }


    public void validar_sesion(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.e("PLUS", "cuenta: "+user.getEmail());
        } else {
            // No user is signed in
            Log.e("PLUS ERROR","no existe sesion:");
        }
    }




    public void conf_btn_open_gallery(){
        view.findViewById(R.id.img_to_upĺoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2000);
                }
                else {
                    Log.e(TAG,"open gallery");
                    openGallery();
                    //startGallery();
                }
            }
        });
    }

    public void conf_btn_upload_photo(){
        view.findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"Upload photo");
                cargar_foto();
            }
        });
    }



    public void openGallery(){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }

    private void startGallery() {
        Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT);
        cameraIntent.setType("image/*");
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, RESULT_LOAD_IMAGE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super method removed
        if (resultCode == MnetActivity.RESULT_OK && requestCode == RESULT_LOAD_IMAGE) {
            Uri returnUri = data.getData();
            IMGPATH = getRealPathFromURI(this.getContext(),returnUri);
            showImage();
            Log.e(TAG, IMGPATH);
        }
    }




    public void showImage(){
        //Log.e("SHOWIMG::",imgpath);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_to_upĺoad);
        imageView.setImageBitmap(BitmapFactory.decodeFile(IMGPATH));
    }



    public void cargar_foto(){
        //Intent intent = getIntent();
        String imgpath = IMGPATH;//intent.getStringExtra(IMGPATH);
        Log.e(TAG,imgpath);
        File IMG_file = new File(imgpath);
        Uri uri = Uri.fromFile(IMG_file);
        //Uri uri = data.getData();

        final ProgressDialog progressDialog =new ProgressDialog(getActivity());
        progressDialog.setTitle("Cargando...");
        progressDialog.show();

        //
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e(TAG,userUid);

        StorageReference filePath = m_storage.child("galeria/user-"+userUid).child(uri.getLastPathSegment());
        //StorageReference imagesRef = m_storage.child("images");


        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(),"Se subio exitosamente la foto",Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Failed"+ e.getMessage(), Toast.LENGTH_SHORT).show();
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

    }

    /**
     * Gets the file path from a given URI.
     * @param context
     * @param contentUri
     * @return
     */
    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            if (contentUri != null) {
                String[] tempArray = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, tempArray, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    return cursor.getString(column_index);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    /*
    *
    *     public void cargar_foto(){
        //Intent intent = getIntent();
        String imgpath = IMGPATH;//intent.getStringExtra(IMGPATH);
        Log.e("ERRORRRRRRRRR::",imgpath);
        File IMG_file = new File(imgpath);
        Log.e("ERRORRRRRRRRR::",IMG_file.getPath());
        Uri uri = Uri.fromFile(IMG_file);
        //Uri uri = data.getData();

        final ProgressDialog progressDialog =new ProgressDialog(getActivity());
        progressDialog.setTitle("Cargando...");
        progressDialog.show();


        StorageReference filePath = m_storage.child("fotos").child(uri.getLastPathSegment());


        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(),"Se subio exitosamente la foto",Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Failed"+ e.getMessage(), Toast.LENGTH_SHORT).show();
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

    }
    * */


}
