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

public class CommunitiesMain extends RecyclerView.Adapter<CommunitiesMain.MainCommunitiesViewHolders>{
    Context context;
    List<CommunitiesData> MaincommunitiesDatalist;

    public CommunitiesMain(Context context, List<CommunitiesData> communitiesDataList) {
        this.context = context;
        this.MaincommunitiesDatalist = communitiesDataList;
    }

    @NonNull
    @Override
    public MainCommunitiesViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.communities_main, parent,false);
        return new MainCommunitiesViewHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainCommunitiesViewHolders holder, int position) {
    holder.MainCommunityName.setText(MaincommunitiesDatalist.get(position).getCommunityName());
    holder.MainCommunityImage.setImageResource(MaincommunitiesDatalist.get(position).getImageUrl());
    }

    @Override
    public int getItemCount() {
        return MaincommunitiesDatalist.size();
    }

    public static final class MainCommunitiesViewHolders extends RecyclerView.ViewHolder{
        ImageView MainCommunityImage;
        TextView MainCommunityName;
        public MainCommunitiesViewHolders(@NonNull View itemView) {
            super(itemView);
            MainCommunityImage=itemView.findViewById(R.id.communityImage);
            MainCommunityName=itemView.findViewById(R.id.communityName);

        }
    }
}
