package com.example.ynl.mnet;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMuro extends Fragment {
    private FirebaseUser user;

    public FragmentMuro() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        validar_sesion();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_muro, container, false);
    }

    public void validar_sesion(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Log.e("sesion iniciada:", user.getEmail());
        } else {
            // No user is signed in
            Log.e("ERROR","no existe sesion:");
        }
    }



}