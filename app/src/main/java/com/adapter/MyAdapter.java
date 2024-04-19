package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sallaapplication.R;
import com.model.HistoryData;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context context;
    private List<HistoryData> dataList;

    public MyAdapter(Context context, List<HistoryData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryData data = dataList.get(position);

        holder.recTitle.setText(data.getDataTitle());
        holder.recDesc.setText(data.getDataDesc());
        holder.recLang.setText(data.getDataLang());

        // Load image using Glide library
        Glide.with(context)
                .load(data.getDataImage())
                .placeholder(R.drawable.bright) // Placeholder image while loading
                .error(R.drawable.cameraiconbright) // Error image if loading fails
                .into(holder.recImage);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recImage;
        TextView recTitle;
        TextView recDesc;
        TextView recLang;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recImage = itemView.findViewById(R.id.recImage);
            recTitle = itemView.findViewById(R.id.recTitle);
            recDesc = itemView.findViewById(R.id.recDesc);
            recLang = itemView.findViewById(R.id.recLang);
        }
    }
}
