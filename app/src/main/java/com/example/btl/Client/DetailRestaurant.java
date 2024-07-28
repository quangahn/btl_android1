package com.example.btl.Client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl.Adapters.DetailRestaurantAdapter;
import com.example.btl.R;
import com.example.btl.databinding.ActivityDetailRestaurantBinding;
import com.example.btl.entites.PostEntity;
import com.example.btl.entites.Restaurant;
import com.example.btl.interfaces.OnClickIconComment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailRestaurant extends AppCompatActivity {
private ActivityDetailRestaurantBinding binding;
    private Map<String, String> userNamesMap;
    private Map<String, String> userAvatarsMap;
    private DetailRestaurantAdapter adapter;
    private List<PostEntity> postList;
    private View view;
    private PostEntity post;
private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;
        Restaurant restaurant = (Restaurant) bundle.getSerializable("restaurant");
        binding.name.setText(restaurant.getName());
        binding.address.setText(restaurant.getAddress());
        Glide.with(binding.image.getContext())
                .load(restaurant.getImage())
                .placeholder(R.drawable.ic_noimage)
                .error(R.drawable.ic_noimage)
                .into(binding.image);
        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = binding.rcvListPost;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new DetailRestaurantAdapter(new ArrayList<>(), new HashMap<>(), new HashMap<>(), new OnClickIconComment() {
            @Override
            public void onClickIconComment(PostEntity post) {
                onClickCommentShow(post);
            }
        });
        recyclerView.setAdapter(adapter);
        postList = new ArrayList<>();
        userNamesMap = new HashMap<>();
        userAvatarsMap = new HashMap<>();
        loadPosts(restaurant.getId());

    }
    private void onClickCommentShow(PostEntity post) {
        Intent intent = new Intent(this, PostDetail.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("post", post);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void loadPosts(String restaurantId) {
        db.collection("posts")
//                .orderBy("created_at", Query.Direction.DESCENDING)
                .whereEqualTo("restaurant_id", restaurantId)
//                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PostEntity post = document.toObject(PostEntity.class);
//                            postList.add(post);
//                            getUserName(post.getUserId());
                            getUserNameAndComments(post);

                        }

                    } else {

                    }
                });
    }
    private void getUserNameAndComments(PostEntity post) {
        // Lấy tên người dùng
        db.collection("Users").document(post.getUserId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String userName = task.getResult().getString("name");
                String userAvatar = task.getResult().getString("avatar");
                userNamesMap.put(post.getUserId(), userName);
                userAvatarsMap.put(post.getUserId(), userAvatar);

                // Sau khi lấy tên người dùng xong, đếm số lượng bình luận
                db.collection("comments")
                        .whereEqualTo("postID", post.getId())
                        .get()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                int count = task1.getResult().size();
                                post.setCommentCount(count);
                                // Thêm bài viết vào danh sách sau khi lấy được số lượng bình luận
                                postList.add(post);
                                // Cập nhật Adapter sau khi thêm bài viết
                                adapter.updateUserNamesMap(userNamesMap);
                                adapter.updateUserAvatarsMap(userAvatarsMap);
                                adapter.updatePostList(postList);
                            } else {
                                // Xử lý lỗi nếu có
                            }
                        });
            } else {
                // Xử lý lỗi nếu có
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
                adapter.updatePostList(postList);
            } else {

            }
        });
    }
}