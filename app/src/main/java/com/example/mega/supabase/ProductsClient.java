package com.example.mega.supabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductsClient {
    private static final String SUPABASE_URL = "https://tfkqryzcarmldinfgvum.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRma3FyeXpjYXJtbGRpbmZndnVtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkwMDU5OTUsImV4cCI6MjA2NDU4MTk5NX0.35UFLF36l_weEbdBCLXJl-UIuGEUPMZG0zMu0Q4Wue4";
    private OkHttpClient httpClient;

    public ProductsClient() {
        this.httpClient = new OkHttpClient();
    }

    private void makeGetRequest(String url, ProductsCallback callback) {
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
                        String errorBody = response.body() != null ? response.body().string() : "No error body";
                        callback.onError("Server error " + response.code() + ": " + errorBody);
                        return;
                    }

                    String responseData = response.body().string();
                    JSONArray products = new JSONArray(responseData);
                    callback.onSuccess(products);
                } catch (Exception e) {
                    callback.onError("Data processing error: " + e.getMessage());
                }
            }
        });
    }

    public void getAllProducts(ProductsCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/Products?select=*";
        makeGetRequest(url, callback);
    }

    public void getProductsByCategory(int categoryId, ProductsCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/Products?category_id=eq." + categoryId + "&select=*";
        makeGetRequest(url, callback);
    }

    public void getFavoriteProducts(String userId, ProductsCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/Products?is_favorite=eq.true&user_id=eq." + userId;
        makeGetRequest(url, callback);
    }

    public void getWishlistProducts(String userId, ProductsCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/Wishlist?user_id=eq." + userId + "&select=Products(*)";
        makeGetRequest(url, callback);
    }

    public void searchProducts(String query, ProductsCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/Products?name=ilike.%25" + query + "%25";
        makeGetRequest(url, callback);
    }

    public void getProductDetails(int productId, ProductDetailsCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/Products?product_id=eq." + productId;
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
                    JSONArray products = new JSONArray(responseData);
                    if (products.length() > 0) {
                        callback.onSuccess(products.getJSONObject(0));
                    } else {
                        callback.onError("Product not found");
                    }
                } catch (Exception e) {
                    callback.onError("Data processing error: " + e.getMessage());
                }
            }
        });
    }

    public interface ProductsCallback {
        void onSuccess(JSONArray response);
        void onError(String errorMessage);
    }

    public interface ProductDetailsCallback {
        void onSuccess(JSONObject productDetails);
        void onError(String errorMessage);
    }
}