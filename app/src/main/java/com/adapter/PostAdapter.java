package com.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sallaapplication.R;
import com.example.sallaapplication.DetailCommunity;
import com.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> dataList;

    public PostAdapter(Context context, List<Post> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post data = dataList.get(position);

        holder.postBody.setText(data.getPostText());
//        holder.profileName.setText(data.getUserProfileImage());
        Picasso.get()
                .load(data.getUserProfileImage())
                .placeholder(R.drawable.profileicon) // Placeholder image while loading
                .error(R.drawable.profileicon) // Error image if loading fails
                .into(holder.profileImage);

        Picasso.get()
                .load(data.getPhoto())
                .placeholder(R.drawable.generalfinal) // Placeholder image while loading
                .error(R.drawable.emptyimage) // Error image if loading fails
                .into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        ImageView like;
        TextView profileName;
        TextView postBody;
        ImageView postImage;
        CardView post;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            like = itemView.findViewById(R.id.like);
            profileName = itemView.findViewById(R.id.profileName);
            postBody = itemView.findViewById(R.id.postBody);
            postImage = itemView.findViewById(R.id.postImage);
            post = itemView.findViewById(R.id.post);
        }
    }
}
