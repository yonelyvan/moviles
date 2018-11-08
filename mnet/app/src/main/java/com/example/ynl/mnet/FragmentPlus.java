package com.example.ynl.mnet;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;


public class FragmentPlus extends Fragment {
    private FirebaseUser user;
    public static final String TAG = "PLUS";

    private StorageReference m_storage;
    public String IMGPATH="null";
    public Uri IMGURI;

    private static int RESULT_LOAD_IMAGE = 1;

    private View view;

    //Database
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;
    private ProgressBar mProgressBar;


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
        mProgressBar = view.findViewById(R.id.progress_bar);
        m_storage = FirebaseStorage.getInstance().getReference();
        //database
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");


        return view;
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

    //button upload
    public void conf_btn_upload_photo(){
        view.findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"Upload photo");
                //cargar_foto();
                uploadFile();
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
            IMGURI = returnUri;
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

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e(TAG,userUid);

        StorageReference filePath = m_storage.child("galeria/user-"+userUid).child(uri.getLastPathSegment());
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
    }




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

    private String getFileExtension(Uri uri){
        ContentResolver cR = this.getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void uploadFile() {
        if (IMGURI != null) {
            String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final String file_name = userUid +"-"+ System.currentTimeMillis() + "." + getFileExtension(IMGURI); //filename
            //final StorageReference fileReference = mStorageRef.child( "uploads" ); //file_name

            //Uri file_uri = Uri.fromFile(new File(IMGPATH));
            //UploadTask uploadTask = mStorageRef.putFile(file_uri);


            final StorageReference ref = mStorageRef.child(file_name);
            UploadTask  uploadTask = ref.putFile(IMGURI);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String downloadURL = downloadUri.toString();
                        Log.e(TAG+"ULR",downloadURL);
                        //
                        Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                        //Upload upload = new Upload(IMGURI.getLastPathSegment(), fileReference.getDownloadUrl().toString());
                        Upload upload = new Upload(file_name, downloadURL);
                        String uploadId = mDatabaseRef.push().getKey();
                        mDatabaseRef.child(uploadId).setValue(upload);

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });




            /*
            mUploadTask = fileReference.putFile(IMGURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                            //Upload upload = new Upload(IMGURI.getLastPathSegment(), fileReference.getDownloadUrl().toString());
                            Upload upload = new Upload(file_name, fileReference.getDownloadUrl().toString());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });

            */
        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }




}
