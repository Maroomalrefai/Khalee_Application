package com.adapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> dataList;
    private DatabaseReference likereference;
    private Boolean testClick = false;
    private boolean isAdmin;
    private String currentCommunityId;

    public PostAdapter(Context context, List<Post> dataList, boolean isAdmin, String currentCommunityId) {
        this.context = context;
        this.dataList = dataList;
        this.isAdmin = isAdmin;
        this.currentCommunityId = currentCommunityId;
        likereference = FirebaseDatabase.getInstance().getReference("likes");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post data = dataList.get(position);

        holder.postBody.setText(data.getPostText());
        holder.profileName.setText(data.getProfileName());

        if (data.getProfileImage() != null) {
            Picasso.get().load(data.getProfileImage()).placeholder(R.drawable.profileicon).into(holder.profileImage);
        }

        // Load the post image if available
        if (data.getPhoto() != null) {
            Picasso.get().load(data.getPhoto()).placeholder(R.drawable.emptyimage).into(holder.postImage);
            holder.postImage.setVisibility(View.VISIBLE);
        } else {
            // If no post image, hide the ImageView
            holder.postImage.setVisibility(View.GONE);
        }
        holder.getLikeButtonStatus(data.getPostKey(), getCurrentUserId());

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update like status when like button is clicked
                testClick = true;
                DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference("Communities").child(currentCommunityId).child("Posts").child(data.getPostKey()).child("likes");
                likeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (testClick) {
                            if (snapshot.child(getCurrentUserId()).exists()) {
                                likeReference.child(getCurrentUserId()).removeValue();
                            } else {
                                likeReference.child(getCurrentUserId()).setValue(true);
                            }
                            testClick = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }
                });
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
                if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < dataList.size()) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete Post")
                            .setMessage("Are you sure you want to delete this post?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    deletePost(data.getPostKey(), adapterPosition);
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
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage, delete;
        ImageView like;
        TextView profileName;
        TextView postBody;
        ImageView postImage;
        CardView post;
        TextView no_Likes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.delete);
            profileImage = itemView.findViewById(R.id.profileImage);
            like = itemView.findViewById(R.id.like);
            profileName = itemView.findViewById(R.id.profileName);
            postBody = itemView.findViewById(R.id.postBody);
            postImage = itemView.findViewById(R.id.postImage);
            post = itemView.findViewById(R.id.post);
            no_Likes = itemView.findViewById(R.id.no_likes);
        }

        public void getLikeButtonStatus(final String postKey, final String userId) {
            DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference("Communities").child(currentCommunityId).child("Posts").child(postKey).child("likes");
            likeReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(userId)) {
                        int likeCount = (int) snapshot.getChildrenCount();
                        no_Likes.setText(likeCount + " likes");
                        like.setImageResource(R.drawable.favorite);
                    } else {
                        int likeCount = (int) snapshot.getChildrenCount();
                        no_Likes.setText(likeCount + " likes");
                        like.setImageResource(R.drawable.favorite_border);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid(); // Return the current user's ID
        } else {
            return ""; // Return a default value or handle the error appropriately
        }
    }

    public void deletePost(String postKey, int position) {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("Communities")
                .child(currentCommunityId).child("Posts").child(postKey);

        postReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (position >= 0 && position < dataList.size()) {
                    dataList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Invalid position", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
