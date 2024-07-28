package com.example.btl.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.btl.Adapters.RestaurantAdapter;
import com.example.btl.Client.DetailRestaurant;
import com.example.btl.Client.EditPost;
import com.example.btl.R;
import com.example.btl.databinding.FragmentRestaurantBinding;
import com.example.btl.entites.PostEntity;
import com.example.btl.interfaces.OnClickItemRestaurant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class Restaurant extends Fragment {
    private FragmentRestaurantBinding binding;
    private View view;
    private FirebaseFirestore db;
    private RestaurantAdapter adapter;
    private List<com.example.btl.entites.Restaurant> restaurantList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestaurantBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = binding.rcvListPost;
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new RestaurantAdapter(new ArrayList<>(), new OnClickItemRestaurant() {
            @Override
            public void OnClickItemRestaurant(com.example.btl.entites.Restaurant restaurant) {
                Intent intent = new Intent(getActivity(), DetailRestaurant.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("restaurant", restaurant);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        restaurantList = new ArrayList<>();
        loadRestaurant();
        return view;
    }

    private void loadRestaurant() {
        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            com.example.btl.entites.Restaurant restaurant = document.toObject(com.example.btl.entites.Restaurant.class);
                            restaurantList.add(restaurant);
                        }
                        adapter.setRestaurants(restaurantList);
                        adapter.notifyDataSetChanged();
                    } else {

                    }
                });
    }
}