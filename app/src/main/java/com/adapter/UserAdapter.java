package com.adapter;


import static androidx.core.content.ContextCompat.getSystemService;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.model.Users;
import com.squareup.picasso.Picasso;

public class UserAdapter extends FirebaseRecyclerAdapter<Users,UserAdapter.ViewHolder> {
    Context context;
//    private void deleteUser(String uid, Context context) {
//        if (uid == null) {
//            Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Delete user data from Realtime Database
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
//        userRef.removeValue().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                // Get the user by UID and delete from Authentication
//                FirebaseAuth auth = FirebaseAuth.getInstance();
//                auth.getCurrentUser().delete().addOnCompleteListener(task1 -> {
//                    if (task1.isSuccessful()) {
//                        // Delete user files from Storage if necessary
//                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users").child(uid);
//                        storageRef.delete().addOnCompleteListener(task2 -> {
//                            if (task2.isSuccessful()) {
//                                Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(context, "Failed to delete user files", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else {
//                        Toast.makeText(context, "Failed to delete user from authentication", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                Toast.makeText(context, "Failed to delete user data", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
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
            // Load profile image using your preferred image loading library, e.g., Picasso, Glide
            Picasso.get().load(model.getImageUrl()).placeholder(R.drawable.profileicon).into(holder.imageUrl);
        }else {
            holder.imageUrl.setImageResource(R.drawable.profileicon);
        }
        // Handle delete button click
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getRef(holder.getAdapterPosition()).removeValue();
                // Dialog
                if (!isNetworkAvailable(context)) {
                    Toast.makeText(context, "No internet connection. Deletion cannot proceed.", Toast.LENGTH_SHORT).show();
                    return;
                }
                 new AlertDialog.Builder(v.getContext())
                                        .setTitle("Delete User")
                                        .setMessage("Are you sure you want to delete this user?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation
                                                getRef(holder.getAdapterPosition()).removeValue();
                                                // Continue with delete operation
                                              //  deleteUser(getRef(holder.getAdapterPosition()).getKey(), v.getContext());
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
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


        }
    }
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
