package com.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sallaapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.model.Users;
import com.squareup.picasso.Picasso;

public class UserAdapter extends FirebaseRecyclerAdapter<Users,UserAdapter.ViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserAdapter(@NonNull FirebaseRecyclerOptions<Users> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position, @NonNull Users model) {

        holder.viewUserName.setText(model.getUsername());
        holder.dateOfBirth.setText(model.getDateOfBirth());
        holder.userEmail.setText(model.getEmail());
        if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
            // Load profile image using your preferred image loading library, e.g., Picasso, Glide
            Picasso.get().load(model.getImageUrl()).placeholder(R.drawable.profileicon).into(holder.imageUrl);
        }else {
            holder.imageUrl.setImageResource(R.drawable.profileicon);
        }

    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from((parent.getContext())).inflate(R.layout.user_item,parent,false));
    }






    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageUrl;
        TextView viewUserName, dateOfBirth,userEmail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageUrl=itemView.findViewById(R.id.imageUrl);
            viewUserName=itemView.findViewById(R.id.viewusername);
            dateOfBirth=itemView.findViewById(R.id.viewDate);
            userEmail=itemView.findViewById(R.id.viewemail);

        }
    }
}
