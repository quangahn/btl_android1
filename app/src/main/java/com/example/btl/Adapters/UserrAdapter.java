package com.example.btl.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.databinding.ActivityUserrClientBinding;
import com.example.btl.databinding.ItemPostedBinding;
import com.example.btl.databinding.ItemUserrBinding;
import com.example.btl.entites.PostEntity;
import com.example.btl.entites.Restaurant;
import com.example.btl.entites.Userr;

import java.util.List;

public class UserrAdapter extends RecyclerView.Adapter<UserrAdapter.UserrViewHolder> {
    private List<Userr> userr;

    public UserrAdapter(List<Userr> userr) {
        this.userr = userr;
    }
    public void setUserr(List<Userr> userr) {
        this.userr = userr;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public UserrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserrBinding binding = ItemUserrBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserrAdapter.UserrViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserrViewHolder holder, int position) {
        Userr userr1 = userr.get(position);
        holder.binding.userName.setText(userr1.getName());
        holder.binding.userAge.setText(userr1.getAge());
        holder.binding.userAddred.setText(userr1.getAddress());
    }

    @Override
    public int getItemCount() {
        if (userr != null)
            return userr.size();
        return 0;
    }

    public class UserrViewHolder extends RecyclerView.ViewHolder {
        private ItemUserrBinding binding;

        public UserrViewHolder(@NonNull ItemUserrBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
