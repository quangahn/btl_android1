package com.example.btl.Client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl.Adapters.CommentAdapter;
import com.example.btl.Adapters.PostAdapter;
import com.example.btl.R;
import com.example.btl.databinding.ActivityPostDetailBinding;
import com.example.btl.databinding.DialogUpdateCommentBinding;
import com.example.btl.entites.Comment;
import com.example.btl.entites.PostEntity;
import com.example.btl.interfaces.OnClickItemComment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PostDetail extends AppCompatActivity {

    private ActivityPostDetailBinding binding;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String currentUserId;
    private PostEntity post;

    private ArrayList<Comment> commentList;
    private HashMap<String, String> userNamesMap;
    private HashMap<String, String> userAvatarsMap;
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//      get current user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            return;
        }
        Uri avatar = user.getPhotoUrl();
        currentUserId = user.getUid();
        Glide.with(binding.avatarCmt.getContext())
                .load(avatar)
                .placeholder(R.drawable.no_avatar)
                .error(R.drawable.no_avatar)
                .into(binding.avatarCmt);
// get post
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        post = (PostEntity) bundle.get("post");

        if (post == null) {
            return;
        }
        db = FirebaseFirestore.getInstance();
        loadPostInfo();
        setAdapter();
        showComments();
        binding.sendCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
                showComments();
            }
        });
    }

    private void addComment() {
        String cmtId = db.collection("comments").document().getId();
        String textComment = binding.commentText.getText().toString();
        Date currentDate = new Date();
        Comment comment = new Comment(cmtId, post.getId(), currentUserId, textComment, currentDate);
        db.collection("comments").document(cmtId).set(comment)
                .addOnSuccessListener(aVoid -> {
                    binding.commentText.setText("");
                    Toast.makeText(this, "Sent", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show());
    }

    private void setAdapter() {
        RecyclerView recyclerView = binding.rcvListCmt;
        LinearLayoutManager layoutManager = new LinearLayoutManager(PostDetail.this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CommentAdapter(new ArrayList<>(), new HashMap<>(), new HashMap<>(), new OnClickItemComment() {
            @Override
            public void onClickItemComment(Comment comment) {
                String userId = comment.getUserID();
                if (Objects.equals(currentUserId, userId)) {
                    showActionDiaLog(comment);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }
    private void showComments() {
        commentList = new ArrayList<>();
        userNamesMap = new HashMap<>();
        userAvatarsMap = new HashMap<>();
        db.collection("comments")
                .whereEqualTo("postID", post.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()){
                                Comment comment = document.toObject(Comment.class);
                                commentList.add(comment);
                                getUserName(comment.getUserID());
                                binding.comments.setText(String.valueOf(commentList.size()));
                            }
                            else {
                                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                            }

                        }
                    } else {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getUserName(String userId) {
        db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String userName = task.getResult().getString("name");
                String userAvatar = task.getResult().getString("avatar");
                userNamesMap.put(userId, userName);
                userAvatarsMap.put(userId, userAvatar);
                adapter.updateUserNamesMap(userNamesMap);
                adapter.updateUserAvatarsMap(userAvatarsMap);
                adapter.updateComments(commentList);
            } else {
            }
        });
    }

    private void showActionDiaLog(Comment comment) {
        Dialog actionDialog = new Dialog(this);
        actionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionDialog.setContentView(R.layout.bottomsheet_layout);
        Objects.requireNonNull(actionDialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        actionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        actionDialog.getWindow().setGravity(Gravity.BOTTOM);

        LinearLayout deleteLayout = actionDialog.findViewById(R.id.delete_layout);
        LinearLayout editLayout = actionDialog.findViewById(R.id.edit_layout);

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Confirm delete")
                        .setMessage("Are you sure to want to delete this comment?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            db.collection("comments")
                                    .whereEqualTo("id", comment.getId())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                db.collection("comments")
                                                        .document(comment.getId())
                                                        .delete()
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                commentList.remove(comment);
                                                                adapter.updateComments(commentList);
                                                                Toast.makeText(view.getContext(), "Delete successfully", Toast.LENGTH_SHORT).show();
                                                                binding.comments.setText(String.valueOf(commentList.size()));
                                                                actionDialog.dismiss();
                                                            }
                                                            else {
                                                                Toast.makeText(view.getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog updateCmtDialog = new Dialog(PostDetail.this);
                updateCmtDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                updateCmtDialog.setContentView(R.layout.dialog_update_comment);
                Objects.requireNonNull(updateCmtDialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                updateCmtDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                updateCmtDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                updateCmtDialog.getWindow().setGravity(Gravity.CENTER);
                updateCmtDialog.setCancelable(false);

                EditText dialogEditText = updateCmtDialog.findViewById(R.id.dialog_edit_text);
                Button dialogCancelButton = updateCmtDialog.findViewById(R.id.dialog_cancel_button);
                Button dialogUpdateButton = updateCmtDialog.findViewById(R.id.dialog_update_button);

                dialogEditText.setText(comment.getComment());

                dialogUpdateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newComment = dialogEditText.getText().toString();
                        db.collection("comments")
                                .whereEqualTo("id", comment.getId())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            db.collection("comments")
                                                    .document(comment.getId())
                                                    .update("comment", newComment)
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            commentList.set(commentList.indexOf(comment),
                                                                    new Comment(comment.getId(), comment.getPostID(), comment.getUserID(), newComment, comment.getDate()));
                                                            adapter.updateComments(commentList);
                                                            Toast.makeText(view.getContext(), "Update successfully", Toast.LENGTH_SHORT).show();
                                                            updateCmtDialog.dismiss();
                                                            actionDialog.dismiss();
                                                        }
                                                        else {
                                                            Toast.makeText(view.getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                });

                dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateCmtDialog.dismiss();
                    }
                });

                updateCmtDialog.show();
            }
        });

        actionDialog.show();
    }

    private void loadPostInfo() {
        binding.tweetText.setText(post.getTitle());
        binding.tweetDetails.setText(post.getContent());
        Glide.with(binding.tweetImage.getContext())
                .load(post.getImage())
                .placeholder(R.drawable.ic_noimage)
                .error(R.drawable.ic_noimage)
                .into(binding.tweetImage);
        getUserPostInfo(post.getUserId());
        actionLike(post);
    }
    private void actionLike(PostEntity post) {
        boolean isLiked = post.getLikes() != null && post.getLikes().contains(currentUserId);
        binding.likeButton.setImageResource(isLiked ? R.drawable.un_like : R.drawable.icon_like);
        binding.likes.setText(post.getLikes() != null ? String.valueOf(post.getLikes().size()) : "0");
        binding.likeButton.setOnClickListener(v -> {
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
                    .addOnSuccessListener(aVoid -> {
                        actionLike(post);
                    })
                    .addOnFailureListener(e -> {
                    });
        });
    }
    private void getUserPostInfo(String userId) {
        db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot user = task.getResult();
                String userName = user.getString("name");
                String userAvatar = user.getString("avatar");
                binding.userName.setText(userName);
                Glide.with(binding.tweetImage.getContext())
                        .load(userAvatar)
                        .placeholder(R.drawable.ic_noimage)
                        .error(R.drawable.ic_noimage)
                        .into(binding.profileImage);
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}