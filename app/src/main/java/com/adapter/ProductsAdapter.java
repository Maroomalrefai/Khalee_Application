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
import com.model.ProductData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.RecentViewHolder> {

    Context context;
    List<ProductData> recentsDataList;

    public ProductsAdapter(Context context, List<ProductData> recentsDataList) {
        this.context = context;
        this.recentsDataList = recentsDataList;
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recents_row_item, parent,false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        ProductData productData = recentsDataList.get(position);

        holder.product_name.setText(recentsDataList.get(position).getProductName());
        holder.company_name.setText(recentsDataList.get(position).getCompanyName());
        Picasso.get()
                .load(productData.getImageUrl())
                .placeholder(R.drawable.bright) // Placeholder image while loading
                .error(R.drawable.cameraiconbright) // Error image if loading fails
                .into(holder.product_image);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String companyUrl = productData.getCompanyUrl();
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
