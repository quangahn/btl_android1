package com.example.btl.Fragments;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import com.example.btl.Adapters.PostAdapter;
import com.example.btl.Client.Resgister;
import com.example.btl.R;
import com.example.btl.Client.PostDetail;
import com.example.btl.databinding.FragmentHomeBinding;
import com.example.btl.entites.PostEntity;
import com.example.btl.interfaces.OnClickIconComment;
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
public class Home extends Fragment {
    private FragmentHomeBinding binding;
    private View view;
    private PostAdapter adapter;
    private FirebaseFirestore db;
    private List<PostEntity> postList;
    private Map<String, String> userNamesMap;
    private Map<String, String> restaurantMap;
    private Map<String, String> userAvatarsMap;
    private List<PostEntity> filteredPostList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = binding.rcvListPost;
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new PostAdapter(new ArrayList<>(), new HashMap<>(), new HashMap<>(),new HashMap<>(), new OnClickIconComment() {
            @Override
            public void onClickIconComment(PostEntity post) {
                onClickCommentShow(post);
            }
        });

        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        postList = new ArrayList<>();
        userNamesMap = new HashMap<>();
        restaurantMap = new HashMap<>();
        userAvatarsMap = new HashMap<>();
        filteredPostList = new ArrayList<>();
        loadPosts();
        binding.searchIcon.setOnClickListener(v->{
            String searchText = binding.search.getText().toString();
            if (!searchText.isEmpty()) {
                searchPosts(searchText);
            } else {
                Toast.makeText(getActivity(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });



        return view;
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
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PostEntity post = document.toObject(PostEntity.class);
                            postList.add(post);
                            getUserName(post.getUserId());
                            getAddress(post.getRestaurant_id());
                            getCommentCount(post); // Gọi hàm đếm số lượng bình luận
                        }

                    } else {

                    }
                });
    }
    private void getCommentCount(PostEntity post) {
        db.collection("comments")
                .whereEqualTo("postID", post.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        post.setCommentCount(count);
                        adapter.notifyDataSetChanged(); // Cập nhật adapter sau khi có số lượng bình luận
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
    private void getAddress(String id){
        db.collection("restaurants").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String address = task.getResult().getString("address");
                restaurantMap.put(id, address);
                adapter.updateRestaurantMap(restaurantMap);
                adapter.updatePostList(postList);
            } else {
            }
        });

    }
    private void searchPosts(String searchText) {
        db.collection("posts")
                .orderBy("title")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PostEntity post = document.toObject(PostEntity.class);
                            postList.add(post);
                            getUserName(post.getUserId());
                        }
                        adapter.updatePostList(postList);
                    } else {
                        // Xử lý lỗi nếu có
                    }
                });
    }
//    private void filterPosts(String query) {
//        filteredPostList.clear();
//        for (PostEntity post : postList) {
//            String title = post.getTitle().toLowerCase();
//            String content = post.getContent().toLowerCase();
//            String userName = userNamesMap.get(post.getUserId()).toLowerCase();
//            String address = restaurantMap.get(post.getRestaurant_id()).toLowerCase();
//
//            if (title.contains(query.toLowerCase()) ||
//                    content.contains(query.toLowerCase()) ||
//                    userName.contains(query.toLowerCase()) ||
//                    address.contains(query.toLowerCase())) {
//                filteredPostList.add(post);
//            }
//        }
//        adapter.updatePostList(filteredPostList);
//    }
}
