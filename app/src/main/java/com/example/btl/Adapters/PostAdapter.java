package com.example.btl.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl.R;
import com.example.btl.databinding.ItemPostBinding;
import com.example.btl.entites.PostEntity;
import com.example.btl.interfaces.OnClickIconComment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<PostEntity> posts;
    private Map<String, String> userNamesMap;
    private Map<String, String> userAvatarsMap;
    private Map<String, String> restaurantMap;
    private OnClickIconComment mOnClickIconComment;

    public PostAdapter(List<PostEntity> posts, Map<String, String> userNamesMap, Map<String, String> userAvatarsMap, Map<String, String> restaurantMap, OnClickIconComment onClickIconComment) {
        this.posts = posts;
        this.userNamesMap = userNamesMap;
        this.userAvatarsMap = userAvatarsMap;
        this.mOnClickIconComment = onClickIconComment;
        this.restaurantMap = restaurantMap;
    }

    public void updatePostList(List<PostEntity> newPostList) {
        posts.clear();
        posts.addAll(newPostList);
        notifyDataSetChanged();
    }

    public void updateUserNamesMap(Map<String, String> newUserNamesMap) {
        userNamesMap.clear();
        userNamesMap.putAll(newUserNamesMap);
        notifyDataSetChanged();
    }

    public void updateRestaurantMap(Map<String, String> newRestaurantMap) {
        restaurantMap.clear();
        restaurantMap.putAll(newRestaurantMap);
        notifyDataSetChanged();
    }

    public void updateUserAvatarsMap(Map<String, String> newUserAvatarsMap) {
        userAvatarsMap.clear();
        userAvatarsMap.putAll(newUserAvatarsMap);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostEntity post = posts.get(position);
        if (post == null) {
            return;
        }
        String address = restaurantMap.get(post.getRestaurant_id());
        holder.binding.tweetText.setText(post.getTitle());
        holder.binding.tweetDetails.setText(post.getContent());
        holder.binding.userName.setText(userNamesMap.get(post.getUserId()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(post.getCreated_at());
        holder.binding.address.setText(restaurantMap.get((post.getRestaurant_id())));
        holder.binding.timestamp.setText(formattedDate);
        Glide.with(holder.binding.tweetImage.getContext())
                .load(post.getImage())
                .placeholder(R.drawable.ic_noimage)
                .error(R.drawable.ic_noimage)
                .into(holder.binding.tweetImage);
        Glide.with(holder.binding.profileImage.getContext())
                .load(userAvatarsMap.get(post.getUserId()))
                .placeholder(R.drawable.ic_noimage)
                .error(R.drawable.ic_noimage)
                .into(holder.binding.profileImage);
        holder.binding.address.setOnClickListener(v -> {
            openGoogleMaps(holder.binding.address.getContext(), address);
        });

        actionLike(post, holder);
        holder.binding.likes.setText(post.getLikes() != null ? String.valueOf(post.getLikes().size()) : "0");
        // Cập nhật TextView số lượng bình luận
        holder.binding.comments.setText(String.valueOf(post.getCommentCount()));
        holder.binding.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickIconComment.onClickIconComment(post);
            }
        });

    }

    private void actionLike(PostEntity post, PostViewHolder holder) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boolean isLiked = post.getLikes() != null && post.getLikes().contains(currentUserId);
        holder.binding.likeButton.setImageResource(isLiked ? R.drawable.un_like : R.drawable.icon_like);
        holder.binding.likes.setText(post.getLikes() != null ? String.valueOf(post.getLikes().size()) : "0");

        holder.binding.likeButton.setOnClickListener(v -> {
            List<String> likes = post.getLikes();
            if (likes == null) {
                likes = new ArrayList<>();
            }

            if (likes.contains(currentUserId)) {
                likes.remove(currentUserId);
            } else {
                likes.add(currentUserId);
            }

            post.setLikes(likes);
            FirebaseFirestore.getInstance().collection("posts").document(post.getId())
                    .update("likes", likes)
                    .addOnSuccessListener(aVoid -> notifyDataSetChanged())
                    .addOnFailureListener(e -> {
                    });
        });
    }

    @Override
    public int getItemCount() {
        if (posts != null)
            return posts.size();
        return 0;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private final ItemPostBinding binding;

        public PostViewHolder(@NonNull ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void openGoogleMaps(Context context, String address) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            Toast.makeText(context, "Google Maps chưa được cài đặt.", Toast.LENGTH_SHORT).show();
        }
    }
}
