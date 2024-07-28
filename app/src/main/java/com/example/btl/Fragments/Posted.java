package com.example.btl.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.btl.Adapters.PostAdapter;
import com.example.btl.Adapters.PostedAdapter;
import com.example.btl.Client.EditPost;
import com.example.btl.Client.PostDetail;
import com.example.btl.R;
import com.example.btl.databinding.FragmentHomeBinding;
import com.example.btl.databinding.FragmentPostedBinding;
import com.example.btl.entites.PostEntity;
import com.example.btl.interfaces.OnClickIconComment;
import com.example.btl.interfaces.OnClickItemPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Posted extends Fragment {
    private FragmentPostedBinding binding;
    private View view;
    private PostedAdapter adapter;
    private FirebaseFirestore db;
    private List<PostEntity> postList;
    private Map<String, String> userNamesMap;
    private Map<String, String> userAvatarsMap;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostedBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = binding.rcvListPost;
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new PostedAdapter(new ArrayList<>(), new HashMap<>(), new HashMap<>(), new OnClickItemPost() {
            @Override
            public void onClickItemDelete(String id) {
                onDeleted(id);
            }

            @Override
            public void onClickItemEdit(PostEntity post) {
                onEdit(post);
            }
        }, new OnClickIconComment() {
            @Override
            public void onClickIconComment(PostEntity post) {
                onClickCommentShow(post);
            }
        });
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        postList = new ArrayList<>();
        userNamesMap = new HashMap<>();
        userAvatarsMap = new HashMap<>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadPosts();
        return view;
    }

    private void onEdit(PostEntity post) {
        Intent intent = new Intent(getActivity(), EditPost.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("post", post);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void onDeleted(String id) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn xóa bài viết này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    db.collection("posts").document(id).delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            postList.removeIf(postEntity -> postEntity.getId().equals(id));
                            adapter.updatePostList(postList);
                        }
                    });
                })
                .setNegativeButton("Không", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
    private void onClickCommentShow(PostEntity post) {
        Intent intent = new Intent(getActivity(), PostDetail.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("post", post);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void loadPosts() {
        db.collection("posts")
                .whereEqualTo("userId", userId)
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
//    private void getUserName(String userId) {
//        db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful() && task.getResult() != null) {
//                String userName = task.getResult().getString("name");
//                String userAvatar = task.getResult().getString("avatar");
//                userNamesMap.put(userId, userName);
//                userAvatarsMap.put(userId, userAvatar);
//                adapter.updateUserNamesMap(userNamesMap);
//                adapter.updateUserAvatarsMap(userAvatarsMap);
//                adapter.updatePostList(postList);
//            } else {
//
//            }
//        });
//    }
}
