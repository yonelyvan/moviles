package com.example.ynl.mnet;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPlus extends Fragment {
    private FirebaseUser user;
    public static final String TAG = "PLUS";


    public FragmentPlus() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        validar_sesion();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plus, container, false);
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






}
