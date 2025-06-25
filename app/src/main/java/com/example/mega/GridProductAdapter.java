package com.example.mega;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mega.models.Product;

import java.util.List;
import java.util.Locale;

public class GridProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> products;

    public GridProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void updateData(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Product getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_product_grid, parent, false);
        }

        Product product = getItem(position);
        ImageView image = convertView.findViewById(R.id.productImage);
        TextView name = convertView.findViewById(R.id.productName);
        TextView price = convertView.findViewById(R.id.productPrice);

        image.setImageResource(product.getImagePath());
        name.setText(product.getName());
        price.setText(String.format(Locale.getDefault(), "%.2f ₽", product.getPrice()));

        return convertView;
    }
}