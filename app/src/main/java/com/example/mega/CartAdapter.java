package com.example.mega;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mega.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> cartItems;
    private CartItemListener listener;

    public void updateItems(List<CartItem> filteredList) {
    }

    public interface CartItemListener {
        void onQuantityChanged(int position, int newQuantity);
        void onRemoveItem(int position);
        void onItemClicked(int position);
    }

    public CartAdapter(List<CartItem> cartItems, CartItemListener listener) {
        this.cartItems = new ArrayList<>(cartItems);
        this.listener = listener;
    }

    public void updateList(List<CartItem> newList) {
        cartItems.clear();
        cartItems.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        Product product = item.getProduct();

        holder.productImage.setImageResource(product.getImagePath());
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format(Locale.getDefault(), "%.0f руб", product.getPrice()));
        holder.quantityText.setText(String.valueOf(item.getQuantity()));
        holder.colorText.setText(getColorName(item.getColor()));
        holder.ratingBar.setRating(product.getRating());
        holder.reviewsCount.setText(String.format("(%d)", product.getReviewCount()));
    }

    private String getColorName(int color) {
        switch (color) {
            case 0: return "Чёрный";
            case 1: return "Белый";
            case 2: return "Голубой";
            default: return "";
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView quantityText;
        Button minusButton;
        Button plusButton;
        TextView colorText;
        RatingBar ratingBar;
        TextView reviewsCount;
        Button removeButton;

        public ViewHolder(View view) {
            super(view);
            productImage = view.findViewById(R.id.product_image);
            productName = view.findViewById(R.id.product_name);
            productPrice = view.findViewById(R.id.product_price);
            quantityText = view.findViewById(R.id.quantity_text);
            minusButton = view.findViewById(R.id.minus_button);
            plusButton = view.findViewById(R.id.plus_button);
            colorText = view.findViewById(R.id.color_text);
            ratingBar = view.findViewById(R.id.rating_bar);
            reviewsCount = view.findViewById(R.id.reviews_count);
            removeButton = view.findViewById(R.id.remove_button);

            view.setOnClickListener(v -> listener.onItemClicked(getAdapterPosition()));
            removeButton.setOnClickListener(v -> listener.onRemoveItem(getAdapterPosition()));

            minusButton.setOnClickListener(v -> {
                int newQuantity = Integer.parseInt(quantityText.getText().toString()) - 1;
                if (newQuantity > 0) {
                    quantityText.setText(String.valueOf(newQuantity));
                    listener.onQuantityChanged(getAdapterPosition(), newQuantity);
                }
            });

            plusButton.setOnClickListener(v -> {
                int newQuantity = Integer.parseInt(quantityText.getText().toString()) + 1;
                quantityText.setText(String.valueOf(newQuantity));
                listener.onQuantityChanged(getAdapterPosition(), newQuantity);
            });
        }
    }
}
