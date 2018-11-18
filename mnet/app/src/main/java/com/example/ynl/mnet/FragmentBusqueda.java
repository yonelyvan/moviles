package com.example.ynl.mnet;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
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


public class FragmentBusqueda extends Fragment {
    private RecyclerView mRecyclerView;
    private ItemUserAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private DatabaseReference mDatabaseRef;
    private List<User> mUsers;

    //
    private View view;
    public static final String TAG = "FragmentBusqueda";

    //
    private Context mContext;

    public FragmentBusqueda() {
        // Required empty public constructor
        mContext = getContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();

        view = inflater.inflate(R.layout.fragment_busqueda, container, false);
        //
        ver_imagenes(view);
        return view;
    }


    public void ver_imagenes(final View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_item_user_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mProgressCircle = view.findViewById(R.id.progress_circle);

        mUsers = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");//get images from a shared "table"
        //Query Q = mDatabaseRef.limitToLast(15);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (dataSnapshot.exists()) {
                        User u = postSnapshot.getValue(User.class);
                        Log.e(TAG, u.get_name());
                        if(u.get_id().equals( FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                            continue;
                        }
                        mUsers.add(0, u);
                    }
                }

                mAdapter = new ItemUserAdapter(mContext, mUsers);

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

}