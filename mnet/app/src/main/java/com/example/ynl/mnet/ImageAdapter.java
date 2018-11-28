package com.example.ynl.mnet;

import android.content.Context;
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
        int max_intentos = 5;
        for (int i=0; i<max_intentos;i++){
            if( set_username_on_view(postCurrent,holder) ){
                break;
            }
        }


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
        public ImageView btn_like;
        public Post current_post;



        public ImageViewHolder(final View itemView) {
            super(itemView);
            textViewUsername= itemView.findViewById(R.id.text_view_username);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewComentario = itemView.findViewById(R.id.text_view_comentario);
            imageView = itemView.findViewById(R.id.image_view_upload);

            //like
            btn_like = (ImageView) itemView.findViewById(R.id.btn_like);

            btn_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String estado = "LIKE";
                    save_like_user(itemView, estado);
                    save_like_shared(itemView, estado);
                    //view.setBackgroundResource(R.mipmap.ic_like);
                    btn_like.setImageResource(R.mipmap.ic_like);
                    //Log.e(TAG,"LIKE "+u.get_name());
                    //add_follower(u, itemView);
                }
            });


        }



        public void save_like_shared(final View itemView, String estado){
            FirebaseUser f_user = FirebaseAuth.getInstance().getCurrentUser();
            //para shared db_name_shared
            String db_name_user_likes = mContext.getString(R.string.db_name_shared);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(db_name_user_likes).child(current_post.get_post_id()).child(f_user.getUid());//en el usuario actual
            Likes l = new Likes(f_user.getUid(), estado);
            ref.setValue(l);//save
            Toast.makeText(itemView.getContext(), "LIKE: "+current_post.get_post_id(), Toast.LENGTH_SHORT).show();
        }

        public void save_like_user(final View itemView, String estado){
            FirebaseUser f_user = FirebaseAuth.getInstance().getCurrentUser();
            //para usuarios
            String db_name_user_likes = mContext.getString(R.string.db_name_users_posts);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(db_name_user_likes).child(current_post.get_user_id()).child(current_post.get_post_id()).child(f_user.getUid());//en el usuario actual
            Likes l = new Likes(f_user.getUid(),estado);
            ref.setValue(l);//save
            //Toast.makeText(itemView.getContext(), "LIKE: "+current_post.get_post_id(), Toast.LENGTH_SHORT).show();
        }

        //public void get_estado_server(){
        //}

        public void set_current_post(Post m_current_post){
            current_post = m_current_post;
        }






    }

    public Boolean set_username_on_view(Post postCurrent, final ImageViewHolder holder){
        holder.set_current_post(postCurrent);
        Boolean r = true;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        try {
            String p_uid = postCurrent.get_user_id();
            Query query = reference.child("users").child(p_uid);//.child("matches");;//.child(postCurrent.get_user_id());
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
        }catch (Exception e){
            e.printStackTrace();
            r = false;
        }
        return r;
        //Log.e(TAG+"Query uid:",postCurrent.get_user_id());
        //------
    }





}
