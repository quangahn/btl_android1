package com.example.btl.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl.R;
import com.example.btl.databinding.ItemRestaurantBinding;
import com.example.btl.entites.Restaurant;
import com.example.btl.interfaces.OnClickItemRestaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestauranViewHolder> {
    private List<Restaurant> restaurants;
    private OnClickItemRestaurant itemRestaurant;

    public RestaurantAdapter(List<Restaurant> restaurants, OnClickItemRestaurant itemRestaurant) {
        this.restaurants = restaurants;
        this.itemRestaurant = itemRestaurant;
    }
    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RestauranViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRestaurantBinding binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RestauranViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestauranViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.binding.userName.setText(restaurant.getName());
        holder.binding.tweetText.setText(restaurant.getAddress());
        Glide.with(holder.binding.tweetImage.getContext())
                .load(restaurant.getImage())
                .placeholder(R.drawable.ic_noimage)
                .error(R.drawable.ic_noimage)
                .into(holder.binding.tweetImage);
        holder.binding.layoutItem.setOnClickListener(v -> {itemRestaurant.OnClickItemRestaurant(restaurant);});
    }

    @Override
    public int getItemCount() {
        if (restaurants != null)
            return restaurants.size();
        return 0;
    }

    public static class RestauranViewHolder extends RecyclerView.ViewHolder {
        private final ItemRestaurantBinding binding;

        public RestauranViewHolder(@NonNull ItemRestaurantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
