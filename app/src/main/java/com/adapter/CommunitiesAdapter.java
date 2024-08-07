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
import com.model.CommunitiesData;

import java.util.ArrayList;
import java.util.List;

public class CommunitiesAdapter extends RecyclerView.Adapter<CommunitiesAdapter.CommunitiesViewHolders>{
    Context context;
    List<CommunitiesData> communitiesDatalist;
    boolean isAdmin;

    public CommunitiesAdapter(Context context, List<CommunitiesData> communitiesDataList, boolean isAdmin) {
        this.context = context;
        this.communitiesDatalist = communitiesDataList;
        this.isAdmin = isAdmin;
    }

    public void setFilteredList(ArrayList<CommunitiesData>filteredList) {
        communitiesDatalist=filteredList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public CommunitiesViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.communitie_row_item, parent,false);
        return new CommunitiesViewHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunitiesViewHolders holder, int position) {
        CommunitiesData currentCommunity = communitiesDatalist.get(position);
        if (isAdmin) {
            holder.comCard.setCardBackgroundColor(context.getResources().getColor(R.color.light_orange));
        } else {
            holder.comCard.setCardBackgroundColor(context.getResources().getColor(R.color.light_blue));
        }
        holder.comCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DetailCommunity.class);
                intent.putExtra("communityId", currentCommunity.getCommunityID());
                intent.putExtra("communityName",currentCommunity.getCommunityName());
                context.startActivity(intent);
            }
        });
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
        CardView comCard;


        public CommunitiesViewHolders(@NonNull View itemView) {
            super(itemView);
            communityImage=itemView.findViewById(R.id.communityImage);
            communityName=itemView.findViewById(R.id.communityName);
            comCard = itemView.findViewById(R.id.comCard);

        }
    }
}
