package com.example.ynl.mnet;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ItemUserAdapter extends RecyclerView.Adapter<ItemUserAdapter.ImageViewHolder> {
    private Context mContext;
    private List<User> mUsers;

    public static final String TAG = "ItemUserAdapter";

    public ItemUserAdapter(Context context, List<User> users) {
        mContext = context;
        mUsers = users;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        final User u = mUsers.get(position);
        holder.set_user(u);
        holder.textViewUsername.setText(u.get_name());
        //
        //TODO: implementar listener
        /*
        itemView.findViewById(R.id.btn_add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "clicked on " +position, Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewUsername;
        public Button btn_follow;
        public ImageView btn_add_friend;
        public User u;

        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.user_name);
            btn_follow = (Button) itemView.findViewById(R.id.btn_follow);
            btn_add_friend = (ImageView) itemView.findViewById(R.id.btn_add_friend);

            btn_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG,"FOLLOW "+u.get_name());
                    add_follower(u);
                }
            });

            btn_add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG,"ADD FRIEND "+u.get_name());
                    add_friend(u);
                }
            });
        }
        public void set_user(User user){
            u=user;
        }

        public void add_follower(User u){
            FirebaseUser f_user = FirebaseAuth.getInstance().getCurrentUser();
            String db_name_followers = mContext.getString(R.string.db_name_followers);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(db_name_followers).child(f_user.getUid());//en el usuario actual
            //String uploadId = ref.push().getKey();
            Follower f = new Follower(u.get_id());
            ref.setValue(f);//save
            Log.e(TAG,"Siguiendo a "+ f.getUid());
        }

        public void add_friend(User u){
            FirebaseUser f_user = FirebaseAuth.getInstance().getCurrentUser();
            String db_name_followers = mContext.getString(R.string.db_name_friends);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(db_name_followers).child(f_user.getUid());//en el usuario actual
            //String uploadId = ref.push().getKey();
            Follower f = new Follower(u.get_id());
            ref.setValue(f);//save
            Log.e(TAG,"Siguiendo a "+ f.getUid());
        }

    }


}
