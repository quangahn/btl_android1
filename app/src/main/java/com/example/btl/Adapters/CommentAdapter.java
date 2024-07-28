package com.example.btl.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl.R;
import com.example.btl.databinding.ItemCommentBinding;
import com.example.btl.entites.Comment;
import com.example.btl.interfaces.OnClickItemComment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{

    private List<Comment> comments;
    private Map<String, String> userNamesMap;
    private Map<String, String> userAvatarsMap;
    private OnClickItemComment mOnClickItemComment;

    public CommentAdapter(List<Comment> comments, Map<String, String> userNamesMap, Map<String, String> userAvatarsMap, OnClickItemComment onClickItemComment) {
        this.comments = comments;
        this.userNamesMap = userNamesMap;
        this.userAvatarsMap = userAvatarsMap;
        this.mOnClickItemComment = onClickItemComment;
    }

    public void updateComments(List<Comment> newCommentList) {
        comments.clear();
        comments.addAll(newCommentList);
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
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        if (comment == null) {
            return;
        }
        holder.binding.comment.setText(comment.getComment());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = dateFormat.format(comment.getDate());
        holder.binding.times.setText(formattedDate);
        holder.binding.username.setText(userNamesMap.get(comment.getUserID()));
        Glide.with(holder.binding.avatarCmt.getContext())
                .load(userAvatarsMap.get(comment.getUserID()))
                .placeholder(R.drawable.no_avatar)
                .error(R.drawable.no_avatar)
                .into(holder.binding.avatarCmt);

        holder.binding.commentItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickItemComment.onClickItemComment(comment);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (comments != null) {
            return comments.size();
        }
        return 0;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        private final ItemCommentBinding binding;
        public CommentViewHolder(@NonNull ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
