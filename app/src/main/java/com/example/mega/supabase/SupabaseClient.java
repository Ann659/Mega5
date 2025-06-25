package com.example.mega.supabase;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class SupabaseClient {
    private static final String SUPABASE_URL = "https://tfkqryzcarmldinfgvum.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRma3FyeXpjYXJtbGRpbmZ" +
            "ndnVtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkwMDU5OTUsImV4cCI6MjA2NDU4MTk5NX0.35UFLF36l_weEbdBCLXJl-UIuGEUPMZG0zMu0Q4Wue4";
    private OkHttpClient httpClient;

    public SupabaseClient() {
        this.httpClient = new OkHttpClient();
    }

    public void getUserByEmail(String email, final SupabaseCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/Users?email=eq." + email + "&select=user_id,email,password,otp_code";

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
                    JSONArray users = new JSONArray(responseData);
                    callback.onSuccess(users);
                } catch (Exception e) {
                    callback.onError("Data processing error: " + e.getMessage());
                }
            }
        });
    }
    public void createUser(String email, String password, String fullName, SupabaseCallback callback) {
        try {
            String otpCode = String.format("%06d", new Random().nextInt(999999));

            JSONObject userData = new JSONObject();
            userData.put("email", email);
            userData.put("password", password);
            userData.put("full_name", fullName);
            userData.put("otp_code", otpCode);
            userData.put("created_at", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .format(new Date()));

            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, userData.toString());

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/Users")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=minimal")
                    .post(body)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String location = response.header("Location");
                            String userId = location != null ? location.substring(location.lastIndexOf("/") + 1) : "";
                            JSONObject result = new JSONObject().put("user_id", userId);
                            callback.onSuccess(new JSONArray().put(result));
                        } catch (JSONException e) {
                            callback.onError("JSON parsing error");
                        }
                    } else {
                        callback.onError("Server error: " + response.code());
                    }
                }
            });
        } catch (JSONException e) {
            callback.onError("JSON creation error: " + e.getMessage());
        }
    }
    public void getUserById(String userId, final SupabaseCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/Users?user_id=eq." + userId + "&select=full_name,email";

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
                    JSONArray users = new JSONArray(responseData);
                    callback.onSuccess(users);
                } catch (Exception e) {
                    callback.onError("Data processing error: " + e.getMessage());
                }
            }
        });
    }
    public interface SupabaseCallback {
        void onSuccess(JSONArray response);
        void onError(String errorMessage);
    }
}
