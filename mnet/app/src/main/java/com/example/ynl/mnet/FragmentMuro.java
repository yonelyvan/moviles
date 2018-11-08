package com.example.ynl.mnet;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
//like imagesActivity
public class FragmentMuro extends Fragment {
    private FirebaseUser user;
    //

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;

    //
    private View view;


    public FragmentMuro() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        validar_sesion();


        view = inflater.inflate(R.layout.fragment_muro, container, false);
        //redirigir a la actividad mostra fotos (aqui! mismo)
        //openImagesActivity();
        // Inflate the layout for this fragment




        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgressCircle = view.findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }

                mAdapter = new ImageAdapter(getActivity(), mUploads);

                mRecyclerView.setAdapter(mAdapter);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });


        //
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
