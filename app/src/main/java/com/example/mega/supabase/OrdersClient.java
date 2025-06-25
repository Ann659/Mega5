package com.example.mega.supabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrdersClient {
    private static final String SUPABASE_URL = "https://your-supabase-url.supabase.co";
    private static final String SUPABASE_KEY = "your-supabase-key";
    private OkHttpClient httpClient;

    public OrdersClient() {
        this.httpClient = new OkHttpClient();
    }

    public void getUserOrders(String userId, OrdersCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/Orders?user_id=eq." + userId + "&select=*";
        makeGetRequest(url, callback);
    }

    public void getOrderById(int orderId, OrderCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/Orders?order_id=eq." + orderId + "&select=*";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .get()
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        callback.onError("Server error: " + response.code());
                        return;
                    }
                    String responseData = response.body().string();
                    JSONArray orders = new JSONArray(responseData);
                    if (orders.length() > 0) {
                        callback.onSuccess(orders.getJSONObject(0));
                    } else {
                        callback.onError("Order not found");
                    }
                } catch (Exception e) {
                    callback.onError("Data processing error: " + e.getMessage());
                }
            }
        });
    }

    public void createOrder(JSONObject orderData, OrderCallback callback) {
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, orderData.toString());

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/Orders")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        callback.onError("Server error: " + response.code());
                        return;
                    }
                    String responseData = response.body().string();
                    JSONArray orders = new JSONArray(responseData);
                    if (orders.length() > 0) {
                        callback.onSuccess(orders.getJSONObject(0));
                    } else {
                        callback.onError("Order creation failed");
                    }
                } catch (Exception e) {
                    callback.onError("Data processing error: " + e.getMessage());
                }
            }
        });
    }

    private void makeGetRequest(String url, OrdersCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .get()
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        callback.onError("Server error: " + response.code());
                        return;
                    }
                    String responseData = response.body().string();
                    JSONArray orders = new JSONArray(responseData);
                    callback.onSuccess(orders);
                } catch (Exception e) {
                    callback.onError("Data processing error: " + e.getMessage());
                }
            }
        });
    }
    public void updateOrderStatus(int orderId, String newStatus, OrderCallback callback) {
        try {
            JSONObject updateData = new JSONObject();
            updateData.put("status", newStatus);

            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, updateData.toString());

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/Orders?id=eq." + orderId)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=representation")
                    .patch(body)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            callback.onError("Server error: " + response.code());
                            return;
                        }
                        String responseData = response.body().string();
                        JSONArray orders = new JSONArray(responseData);
                        if (orders.length() > 0) {
                            callback.onSuccess(orders.getJSONObject(0));
                        } else {
                            callback.onError("Order not found");
                        }
                    } catch (Exception e) {
                        callback.onError("Data processing error: " + e.getMessage());
                    }
                }
            });
        } catch (JSONException e) {
            callback.onError("Error creating update JSON");
        }
    }

    public void getAllOrders(OrderCallback orderCallback) {

    }

    public interface OrdersCallback {
        void onSuccess(JSONArray response);
        void onError(String errorMessage);
    }

    public interface OrderCallback {
        void onSuccess(JSONObject response);
        void onError(String errorMessage);
    }
}
