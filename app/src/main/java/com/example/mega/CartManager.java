package com.example.mega;

import androidx.lifecycle.MutableLiveData;

import com.example.mega.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems = new ArrayList<>();
    private final MutableLiveData<List<CartItem>> cartItemsLiveData = new MutableLiveData<>();

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Product product, int quantity, int color) {
    }

    public void removeFromCart(Product product) {
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void clearCart() {
        clearCart();
    }
    public void updateQuantity(Product product, int newQuantity) {
        cartItemsLiveData.setValue(new ArrayList<>(cartItems));
    }
}
