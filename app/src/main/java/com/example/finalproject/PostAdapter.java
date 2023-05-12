package com.example.finalproject;

import android.net.Uri;
import android.provider.Contacts;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PhotosViewHolder> {

    private List<Photos> mList = new ArrayList<>();
    public PostAdapter(List<Photos> mList){
        this.mList = mList;
    }

    @NonNull
    @Override
    public PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new PhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosViewHolder holder, int position) {
            Uri imageUri = mList.get(position).getImageUri();
            // Use Glide or Picasso to load the image from the URI into the ImageView
            Glide.with(holder.imageView.getContext())
                    .load(imageUri)
                    .into(holder.imageView);
        }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class PhotosViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView imageView;
        public PhotosViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_imageView);
        }
    }
}
