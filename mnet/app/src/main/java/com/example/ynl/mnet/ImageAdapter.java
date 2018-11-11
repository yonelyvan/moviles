package com.example.ynl.mnet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser user;
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
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        Post postCurrent = mPosts.get(position);
        holder.textViewUsername.setText(user.getDisplayName());
        holder.textViewDate.setText("fecha:");
        holder.textViewComentario.setText(postCurrent.get_comment());
        Picasso.get()
                .load(postCurrent.get_img_url())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imageView);

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
