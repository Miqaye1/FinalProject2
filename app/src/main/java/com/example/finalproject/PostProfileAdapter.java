package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class PostProfileAdapter extends RecyclerView.Adapter<PostProfileAdapter.ViewHolder>{

    ArrayList<ProjectModel> list;
    Context context;

    public PostProfileAdapter(ArrayList<ProjectModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item3,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectModel model = list.get(position);
        String productImage = model.getProductImage();

        if (productImage != null) {
            Glide.with(context)
                    .load(productImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Handle the load failure if needed
                            // You can display a default image here
                            holder.postImage.setImageResource(R.drawable.rsz_1rsz_1rsz_1rsz_1user);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // Image is successfully loaded, perform any additional operations here
                            return false;
                        }
                    })
                    .placeholder(R.drawable.loading) // Optional: Placeholder image while loading
                    .error(R.drawable.rsz_1rsz_1rsz_1rsz_1user) // Default image to display on error
                    .into(holder.postImage);
        } else {
            // Handle the case where the image URL is null
            // You can display a default image here
            holder.postImage.setImageResource(R.drawable.rsz_1rsz_1rsz_1rsz_1user);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SinglePostActivity.class);
                intent.putExtra("singleImage", productImage);
                intent.putExtra("singleDescription", model.getDescription());
                intent.putExtra("userId", model.getUserId());
                intent.putExtra("postId", model.getPostId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView description;
        ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.item3_imageView);
        }
    }
}
