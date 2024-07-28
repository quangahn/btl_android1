package com.example.btl.Client;

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

import com.example.btl.Adapters.RestaurantAdapter;
import com.example.btl.Adapters.UserrAdapter;
import com.example.btl.R;
import com.example.btl.databinding.ActivityUserrClientBinding;
import com.example.btl.entites.Userr;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserrClient extends AppCompatActivity {
    private ActivityUserrClientBinding binding;
    private FirebaseFirestore db;
    private View view;
    private UserrAdapter adapter;
    private List<Userr> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserrClientBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = binding.rcvListUserr;
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new UserrAdapter(new ArrayList<>());
        loadUserr();
    }

    private void loadUserr() {
        db.collection("userr")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            com.example.btl.entites.Userr userr = document.toObject(com.example.btl.entites.Userr.class);
                            list.add(userr);
                        }
                        adapter.setUserr(list);
                        adapter.notifyDataSetChanged();
                    } else {

                    }
                });
    }

}