package com.example.ynl.mnet;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    private TextView txt_name;

    private DatabaseReference mDatabaseRef;
    private List<Post> mPosts;

    //
    private View view;
    public static final String TAG = "FragmentoMuro";

    //
    private Context mContext;

    public FragmentMuro() {
        // Required empty public constructor
        mContext = getContext();
        //mContext = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        validar_sesion();
        mContext = getContext();

        view = inflater.inflate(R.layout.fragment_muro, container, false);
         //
        ver_imagenes(view);
        return view; //inflater.inflate(R.layout.fragment_muro, container, false);
    }


    public void ver_imagenes(View view){
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mProgressCircle = view.findViewById(R.id.progress_circle);
        txt_name = view.findViewById(R.id.txt_name);
        txt_name.setText(user.getDisplayName());

        mPosts = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());//get images from a specific user
        //Query Q = mDatabaseRef.orderByPriority();

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post= postSnapshot.getValue(Post.class);
                    mPosts.add(0,post);
                }

                mAdapter = new ImageAdapter(mContext, mPosts);

                mRecyclerView.setAdapter(mAdapter);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
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
