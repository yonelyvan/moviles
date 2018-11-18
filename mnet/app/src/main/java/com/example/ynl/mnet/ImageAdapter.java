package com.example.ynl.mnet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Post> mPosts;
    public static final String TAG = "ImageAdapter";

    public ImageAdapter(Context context, List<Post> posts) {
        mContext = context;
        mPosts = posts;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        Post postCurrent = mPosts.get(position);

        //--setting username
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Log.e(TAG+"Query uid:",postCurrent.get_user_id());
        Query query = reference.child("users").child(postCurrent.get_user_id());//.child("matches");;//.child(postCurrent.get_user_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User u = dataSnapshot.getValue(User.class);
                    //String username =  (String) dataSnapshot.child("_name").getValue();
                    holder.textViewUsername.setText(u.get_name());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //------


        Long unix = Long.parseLong(postCurrent.getUnix_time_str() );
        Date date = new Date(unix*1000L);
        SimpleDateFormat jdf = new SimpleDateFormat("HH:mm  dd-MM-yyyy");
        String java_date = jdf.format(date);

        holder.textViewDate.setText(java_date );
        holder.textViewComentario.setText(postCurrent.get_comment());
        Picasso.get()
                .load(postCurrent.get_img_url())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);
        Log.e(TAG+"-UNIX:",postCurrent.getUnix_time_str());
        Log.e(TAG+"-URL:",postCurrent.get_img_url());
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewComentario;
        public TextView textViewUsername;
        public TextView textViewDate;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewUsername= itemView.findViewById(R.id.text_view_username);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewComentario = itemView.findViewById(R.id.text_view_comentario);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }




}
