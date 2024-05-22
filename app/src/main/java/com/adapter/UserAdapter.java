package com.adapter;


import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sallaapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.FirebaseDatabase;
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
        // Handle delete button click
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRef(holder.getAdapterPosition()).removeValue();
                // Dialog
                // new AlertDialog.Builder(v.getContext())
                //                        .setTitle("Delete User")
                //                        .setMessage("Are you sure you want to delete this user?")
                //                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                //                            public void onClick(DialogInterface dialog, int which) {
                //                                // Continue with delete operation
                //                                getRef(holder.getAdapterPosition()).removeValue();
                //                            }
                //                        })
                //                        .setNegativeButton(android.R.string.no, null)
                //                        .setIcon(android.R.drawable.ic_dialog_alert)
                //                        .show();
            }
        });
        // Handle make admin button click
        holder.makeAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update isAdmin value in Firebase
                // Update isAdmin value in Firebase
                getRef(holder.getAdapterPosition()).child("isAdmin").setValue(true);
            }
        });
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from((parent.getContext())).inflate(R.layout.user_item,parent,false));
    }






    public class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageUrl;
        TextView viewUserName, dateOfBirth,userEmail;
        FloatingActionButton deleteButton;
        Button makeAdminButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageUrl=itemView.findViewById(R.id.imageUrl);
            viewUserName=itemView.findViewById(R.id.viewusername);
            dateOfBirth=itemView.findViewById(R.id.viewDate);
            userEmail=itemView.findViewById(R.id.viewemail);
            deleteButton = itemView.findViewById(R.id.delete);
            makeAdminButton = itemView.findViewById(R.id.makeAdmin);

        }
    }

}
