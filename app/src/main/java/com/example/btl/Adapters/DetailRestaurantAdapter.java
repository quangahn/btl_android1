package com.example.btl.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class DetailRestaurantAdapter extends RecyclerView.Adapter<DetailRestaurantAdapter.DetailRestauranViewHolder> {
    private List<PostEntity> posts;
    private Map<String, String> userNamesMap;
    private Map<String, String> userAvatarsMap;
    private OnClickIconComment mOnClickIconComment;

    public DetailRestaurantAdapter(List<PostEntity> posts, Map<String, String> userNamesMap, Map<String, String> userAvatarsMap, OnClickIconComment onClickIconComment) {
        this.posts = posts;
        this.userNamesMap = userNamesMap;
        this.userAvatarsMap = userAvatarsMap;
        this.mOnClickIconComment = onClickIconComment;
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

    public void updateUserAvatarsMap(Map<String, String> newUserAvatarsMap) {
        userAvatarsMap.clear();
        userAvatarsMap.putAll(newUserAvatarsMap);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailRestauranViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DetailRestaurantAdapter.DetailRestauranViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailRestauranViewHolder holder, int position) {
        PostEntity post = posts.get(position);
        if (post == null) {
            return;
        }
        holder.binding.tweetText.setText(post.getTitle());
        holder.binding.tweetDetails.setText(post.getContent());
        holder.binding.userName.setText(userNamesMap.get(post.getUserId()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(post.getCreated_at());

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

        holder.binding.comments.setText(String.valueOf(post.getCommentCount()));

        holder.binding.postItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickIconComment.onClickIconComment(post);
            }
        });

        actionLike(holder, post);

    }

    private void actionLike(DetailRestauranViewHolder holder, PostEntity post) {
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
        if (posts != null) {
            return posts.size();
        }
        return 0;
    }

    public static class DetailRestauranViewHolder extends RecyclerView.ViewHolder {
        private final ItemPostBinding binding;

        public DetailRestauranViewHolder(@NonNull ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
