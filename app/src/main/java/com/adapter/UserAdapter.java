package com.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sallaapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.model.Users;
import com.squareup.picasso.Picasso;

public class UserAdapter extends FirebaseRecyclerAdapter<Users, UserAdapter.ViewHolder> {
    private Context context;

    public UserAdapter(@NonNull FirebaseRecyclerOptions<Users> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position, @NonNull Users model) {
        holder.viewUserName.setText(model.getUsername());
        holder.dateOfBirth.setText(model.getDateOfBirth());
        holder.userEmail.setText(model.getEmail());
        if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
            Picasso.get().load(model.getImageUrl()).placeholder(R.drawable.profileicon).into(holder.imageUrl);
        } else {
            holder.imageUrl.setImageResource(R.drawable.profileicon);
        }
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false));
    }






    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageUrl;
        TextView viewUserName, dateOfBirth, userEmail;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUrl = itemView.findViewById(R.id.imageUrl);
            viewUserName = itemView.findViewById(R.id.viewusername);
            dateOfBirth = itemView.findViewById(R.id.viewDate);
            userEmail = itemView.findViewById(R.id.viewemail);

        }
    }
}
