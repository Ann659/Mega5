package com.example.mega;

import com.example.mega.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartRepository {
    private static CartRepository instance;
    private List<CartItem> cartItems = new ArrayList<>();

    public static synchronized CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
            instance.initializeSampleData();
        }
        return instance;
    }

    private void initializeSampleData() {
        Product product1 = new Product();
        product1.setId("1");
        product1.setName("Тестовый товар 1");
        product1.setPrice(1000);

        Product product2 = new Product();
        product2.setId("2");
        product2.setName("Тестовый товар 2");
        product2.setPrice(2000);

        CartItem item1 = new CartItem();
        item1.setProduct(product1);
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        item2.setProduct(product2);
        item2.setQuantity(1);

        cartItems.add(item1);
        cartItems.add(item2);
    }

    public void getCartItems(CartCallback callback) {
        callback.onSuccess(new ArrayList<>(cartItems));
    }

    public void clearCart(CartCallback cartCallback) {

    }

    public interface CartCallback {
        void onSuccess(List<CartItem> items);
        void onError(String error);
    }
}
