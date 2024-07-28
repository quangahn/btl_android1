package com.example.btl.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.btl.entites.Product;

import java.util.List;

public class ProductaSprinnerAdapter extends BaseAdapter {
    private List<Product> productList;
    private LayoutInflater inflater;

    public ProductaSprinnerAdapter(Context context, List<Product> productList) {
        this.productList = productList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = productList.get(position);
        holder.textView.setText(product.getName());

        return convertView;
    }
    private static class ViewHolder {
        TextView textView;
    }
}
