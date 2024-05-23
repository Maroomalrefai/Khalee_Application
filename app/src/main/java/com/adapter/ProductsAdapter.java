package com.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sallaapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.model.ProductData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.RecentViewHolder> {

    private final Context context;
    private final List<ProductData> recentsDataList;
    private final boolean isAdmin;

    public ProductsAdapter(Context context, List<ProductData> recentsDataList, boolean isAdmin) {
        this.context = context;
        this.recentsDataList = recentsDataList;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recents_row_item, parent, false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        ProductData productData = recentsDataList.get(position);

        holder.product_name.setText(productData.getProductName());
        holder.company_name.setText(productData.getCompanyName());
        Picasso.get()
                .load(productData.getImageUrl())
                .placeholder(R.drawable.emptyimage) // Placeholder image while loading
                .error(R.drawable.whitecard) // Error image if loading fails
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

        // Set visibility of delete button based on admin status
        if (isAdmin) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable(context)) {
                    Toast.makeText(context, "No internet connection. Deletion cannot proceed.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete Post")
                            .setMessage("Are you sure you want to delete this post?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                    deletePost(productData.getProductKey(), adapterPosition);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            }
        });
        }

    @Override
    public int getItemCount() {
        return recentsDataList.size();
    }

    public static final class RecentViewHolder extends RecyclerView.ViewHolder {
        ImageView product_image, delete;
        TextView product_name, company_name;
        CardView cardView;

        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.delete);
            product_image = itemView.findViewById(R.id.productImage);
            product_name = itemView.findViewById(R.id.productName);
            company_name = itemView.findViewById(R.id.companyName);
            cardView = itemView.findViewById(R.id.itemCard);
        }
    }
        private boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    private void deletePost(String postKey, int position) {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("Android Tutorials").child(postKey);
        postReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                recentsDataList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
            }
        });
    }


    }
