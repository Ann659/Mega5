package com.example.mega;

import com.example.mega.models.Order;
import com.example.mega.supabase.OrdersClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OrderRepository {
    private static OrderRepository instance;
    private final OrdersClient ordersClient;
    private final List<Order> cachedOrders = new ArrayList<>();

    private OrderRepository() {
        this.ordersClient = new OrdersClient();
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    public void getOrders(OrdersCallback callback) {
        ordersClient.getAllOrders(new OrdersClient.OrderCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    List<Order> result = new ArrayList<>();

                    if (response.has("orders")) {
                        JSONArray ordersArray = response.getJSONArray("orders");
                        for (int i = 0; i < ordersArray.length(); i++) {
                            result.add(parseOrderFromJson(ordersArray.getJSONObject(i)));
                        }
                    }
                    else {
                        try {
                            JSONArray ordersArray = new JSONArray(response.toString());
                            for (int i = 0; i < ordersArray.length(); i++) {
                                result.add(parseOrderFromJson(ordersArray.getJSONObject(i)));
                            }
                        } catch (JSONException e) {
                            result.add(parseOrderFromJson(response));
                        }
                    }

                    synchronized (cachedOrders) {
                        cachedOrders.clear();
                        cachedOrders.addAll(result);
                    }
                    callback.onSuccess(result);
                } catch (Exception e) {
                    callback.onError("Failed to parse orders: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError("Network error: " + errorMessage);
            }
        });
    }

    public List<Order> getCachedOrders() {
        return new ArrayList<>(cachedOrders);
    }

    public void getOrder(String orderId, OrderCallback callback) {
        ordersClient.getOrderById(Integer.parseInt(orderId), new OrdersClient.OrderCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Order order = parseOrderFromJson(response);
                    callback.onSuccess(order);
                } catch (JSONException e) {
                    callback.onError("Error parsing order data");
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void updateOrderStatus(String orderId, String newStatus, OrderCallback callback) {
        if (orderId == null || newStatus == null) {
            callback.onError("Invalid parameters");
            return;
        }

        ordersClient.updateOrderStatus(Integer.parseInt(orderId), newStatus, new OrdersClient.OrderCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Order updatedOrder = parseOrderFromJson(response);
                    synchronized (cachedOrders) {
                        for (int i = 0; i < cachedOrders.size(); i++) {
                            String currentId = String.valueOf(cachedOrders.get(i).getId());
                            if (orderId.equals(currentId)) {
                                cachedOrders.set(i, updatedOrder);
                                break;
                            }
                        }
                    }
                    callback.onSuccess(updatedOrder);
                } catch (Exception e) {
                    callback.onError("Failed to parse order: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError("API error: " + errorMessage);
            }
        });
    }
    private List<Order> parseOrdersFromJson(JSONArray jsonArray) throws JSONException {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            orders.add(parseOrderFromJson(jsonArray.getJSONObject(i)));
        }
        return orders;
    }

    private Order parseOrderFromJson(JSONObject json) throws JSONException {
        String id = json.getString("id");
        String status = json.getString("status");
        double total = json.getDouble("total");
        long date;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            date = sdf.parse(json.getString("created_at")).getTime();
        } catch (ParseException e) {
            date = System.currentTimeMillis();
        }

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

    public interface OrdersCallback {
        void onSuccess(List<Order> orders);
        void onError(String error);
    }

    public interface OrderCallback {
        void onSuccess(Order order);
        void onError(String error);
    }
}