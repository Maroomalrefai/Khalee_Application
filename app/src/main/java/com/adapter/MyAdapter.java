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
import com.example.sallaapplication.DetailActivity;
import com.model.HistoryData;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
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
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDataDesc());
                intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getDataTitle());
                intent.putExtra("Company", dataList.get(holder.getAdapterPosition()).getDataLang());
                intent.putExtra("Key", dataList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);

            }
        });
        // Load image using Picasso library
        Picasso.get()
                .load(data.getDataImage())
                .placeholder(R.drawable.bright) // Placeholder image while loading
                .error(R.drawable.cameraiconbright) // Error image if loading fails
                .into(holder.recImage);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<HistoryData> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recImage;
        TextView recTitle;
        TextView recDesc;
        TextView recLang;
        CardView recCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recImage = itemView.findViewById(R.id.recImage);
            recTitle = itemView.findViewById(R.id.recTitle);
            recDesc = itemView.findViewById(R.id.recDesc);
            recLang = itemView.findViewById(R.id.recLang);
            recCard = itemView.findViewById(R.id.recCard);
        }
    }
}
