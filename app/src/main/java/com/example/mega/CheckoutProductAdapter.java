package com.example.mega;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class CheckoutProductAdapter extends RecyclerView.Adapter<CheckoutProductAdapter.ProductViewHolder> {
    private List<CartItem> items;
    private List<OrderItem> items2;

    public CheckoutProductAdapter(List<OrderItem> items) {
        this.items2 = items;
    }

    public void updateItems(List<CartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        OrderItem item = items2.get(position);
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

        public void bind(OrderItem item) {
            productNameText.setText(item.getProductName());
            productPriceText.setText(String.format(Locale.getDefault(), "%.0f руб", item.getPrice()));
            productQuantityText.setText(String.format(Locale.getDefault(), "x%d", item.getQuantity()));
        }
    }
}
