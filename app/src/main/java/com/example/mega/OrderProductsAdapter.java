package com.example.mega;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class OrderProductsAdapter extends RecyclerView.Adapter<OrderProductsAdapter.ProductViewHolder> {
    private List<CartItem> items;
    private List<OrderItem> items2;

    public OrderProductsAdapter(List<OrderItem> items) {
        this.items2 = items;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView productNameText;
        private final TextView productPriceText;
        private final TextView productQuantityText;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameText = itemView.findViewById(R.id.productNameText);
            productPriceText = itemView.findViewById(R.id.productPriceText);
            productQuantityText = itemView.findViewById(R.id.productQuantityText);
        }

        public void bind(CartItem item) {
            productNameText.setText(item.getProduct().getName());
            productPriceText.setText(String.format(Locale.getDefault(), "%.0f руб", item.getProduct().getPrice()));
            productQuantityText.setText(String.format(Locale.getDefault(), "x%d", item.getQuantity()));
        }
    }
}