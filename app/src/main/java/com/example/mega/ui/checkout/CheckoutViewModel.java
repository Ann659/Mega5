package com.example.mega.ui.checkout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mega.CartItem;
import com.example.mega.CartRepository;
import com.example.mega.OrderItem;
import com.example.mega.models.Order;
import com.example.mega.supabase.OrdersClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CheckoutViewModel extends ViewModel {
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final OrdersClient ordersClient = new OrdersClient();
    private final CartRepository cartRepository = CartRepository.getInstance();

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadCartItems() {
        isLoading.setValue(true);
        cartRepository.getCartItems(new CartRepository.CartCallback() {
            @Override
            public void onSuccess(List<CartItem> items) {
                isLoading.setValue(false);
                cartItems.setValue(items);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void createOrder(String paymentMethod, String address,
                            CheckoutFragment.OrderConfirmationCallback callback) {
        isLoading.setValue(true);

        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems == null || currentItems.isEmpty()) {
            isLoading.setValue(false);
            callback.onError("Cart is empty");
            return;
        }

        try {
            JSONObject orderData = new JSONObject();
            orderData.put("payment_method", paymentMethod);
            orderData.put("delivery_address", address);
            orderData.put("status", "processing");
            orderData.put("total", calculateTotal(currentItems));

            JSONArray itemsArray = new JSONArray();
            for (CartItem item : currentItems) {
                JSONObject itemJson = new JSONObject();
                itemJson.put("product_id", item.getProduct().getId());
                itemJson.put("product_name", item.getProduct().getName());
                itemJson.put("quantity", item.getQuantity());
                itemJson.put("price", item.getProduct().getPrice());
                itemsArray.put(itemJson);
            }
            orderData.put("items", itemsArray);

            ordersClient.createOrder(orderData, new OrdersClient.OrderCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        Order order = parseOrderFromJson(response);
                        isLoading.setValue(false);
                        clearCart();
                        callback.onSuccess(order);
                    } catch (JSONException e) {
                        isLoading.setValue(false);
                        callback.onError("Error parsing order response");
                    }
                }

                @Override
                public void onError(String error) {
                    isLoading.setValue(false);
                    callback.onError(error);
                }
            });
        } catch (JSONException e) {
            isLoading.setValue(false);
            callback.onError("Error creating order data");
        }
    }

    private void clearCart() {
        cartRepository.clearCart(new CartRepository.CartCallback() {
            @Override
            public void onSuccess(List<CartItem> items) {
                cartItems.setValue(Collections.emptyList());
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue("Error clearing cart: " + error);
            }
        });
    }

    private double calculateTotal(List<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    private Order parseOrderFromJson(JSONObject json) throws JSONException {
        String id = json.getString("id");
        long date = System.currentTimeMillis();
        String status = json.getString("status");
        double total = json.getDouble("total");

        List<OrderItem> items = new ArrayList<>();
        if (json.has("items")) {
            JSONArray itemsArray = json.getJSONArray("items");
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemJson = itemsArray.getJSONObject(i);
                items.add(new OrderItem(
                        itemJson.getInt("product_id"),
                        itemJson.optString("product_name", "Unknown"),
                        (int) itemJson.getDouble("price"),
                        itemJson.getInt("quantity")
                ));
            }
        }

        return new Order(id, date, status, total, items);
    }
}