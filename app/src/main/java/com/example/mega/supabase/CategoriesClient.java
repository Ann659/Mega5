package com.example.mega.supabase;
import org.json.JSONArray;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class CategoriesClient {
    private static final String SUPABASE_URL = "https://tfkqryzcarmldinfgvum.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRma3FyeXpjYXJtbGRpbmZndnVtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkwMDU5OTUsImV4cCI6MjA2NDU4MTk5NX0.35UFLF36l_weEbdBCLXJl-UIuGEUPMZG0zMu0Q4Wue4";
    private OkHttpClient httpClient;

    public CategoriesClient() {
        this.httpClient = new OkHttpClient();
    }

    public void getAllCategories(CategoriesCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/Categories?select=*";

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
                    JSONArray categories = new JSONArray(responseData);
                    callback.onSuccess(categories);
                } catch (Exception e) {
                    callback.onError("Data processing error: " + e.getMessage());
                }
            }
        });
    }

    public interface CategoriesCallback {
        void onSuccess(JSONArray response);
        void onError(String errorMessage);
    }
}