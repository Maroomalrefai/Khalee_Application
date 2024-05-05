package com.adapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sallaapplication.R;
import com.model.RecentData;
import java.util.List;

public class RecentsAdapter extends RecyclerView.Adapter<RecentsAdapter.RecentViewHolder> {
    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recents_row_item, parent,false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        RecentData recentData = recentsDataList.get(position);

        holder.product_name.setText(recentsDataList.get(position).getProductName());
        holder.company_name.setText(recentsDataList.get(position).getCompanyName());
        holder.product_image.setImageResource(recentsDataList.get(position).getImageUrl());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String companyUrl = recentData.getCompanyUrl();
                // Navigate to company URL
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(companyUrl));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentsDataList.size();
    }

    Context context;
    List<RecentData> recentsDataList;

    public RecentsAdapter(Context context, List<RecentData> recentsDataList) {
        this.context = context;
        this.recentsDataList = recentsDataList;
    }

    public static final class RecentViewHolder extends RecyclerView.ViewHolder{
        ImageView product_image;
        TextView product_name,company_name;
        CardView cardView;
        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            product_image = itemView.findViewById(R.id.productImage);
            product_name = itemView.findViewById(R.id.productName);
            company_name = itemView.findViewById(R.id.companyName);
            cardView = itemView.findViewById(R.id.itemCard);
        }
    }
}
