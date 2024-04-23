package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sallaapplication.R;
import com.model.CommunitiesData;

import java.util.List;

public class CommunitiesMain extends RecyclerView.Adapter<CommunitiesMain.CommunitiesViewHolders>{
    Context context;
    List<CommunitiesData> communitiesDatalist;

    public CommunitiesMain(Context context, List<CommunitiesData> communitiesDataList) {
        this.context = context;
        this.communitiesDatalist = communitiesDataList;
    }

    @NonNull
    @Override
    public CommunitiesViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.communities_main, parent,false);
        return new CommunitiesViewHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunitiesViewHolders holder, int position) {
    holder.communityName.setText(communitiesDatalist.get(position).getCommunityName());
    holder.communityImage.setImageResource(communitiesDatalist.get(position).getImageUrl());
    }

    @Override
    public int getItemCount() {
        return communitiesDatalist.size();
    }

    public static final class CommunitiesViewHolders extends RecyclerView.ViewHolder{
        ImageView communityImage;
        TextView communityName;
        public CommunitiesViewHolders(@NonNull View itemView) {
            super(itemView);
            communityImage=itemView.findViewById(R.id.communityImage);
            communityName=itemView.findViewById(R.id.communityName);

        }
    }
}
