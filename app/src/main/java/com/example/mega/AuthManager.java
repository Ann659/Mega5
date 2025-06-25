package com.example.mega;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static AuthManager instance;
    private SharedPreferences sharedPreferences;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    private AuthManager() {
        if (appContext == null) {
            throw new IllegalStateException("AuthManager must be initialized with Context first");
        }
        sharedPreferences = appContext.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE);
    }

    public static synchronized AuthManager getInstance() {
        if (instance == null) {
            if (appContext == null) {
                throw new IllegalStateException("Call AuthManager.init(context) first");
            }
            instance = new AuthManager();
        }
        return instance;
    }

    public void saveUserData(String userId, String fullName, long registrationDate) {
        sharedPreferences.edit()
                .putString("user_id", userId)
                .putString("user_name", fullName)
                .putLong("registration_date", registrationDate)
                .apply();
    }

    public String getUserId() {
        return sharedPreferences.getString("user_id", "");
    }

    public void saveUserCredentials(String email, String password) {
        sharedPreferences.edit()
                .putString("user_email", email)
                .putString("user_password", password)
                .apply();
    }

    public void saveUserToken(String token) {
        sharedPreferences.edit().putString("user_token", token).apply();
    }

    public String getUserToken() {
        return sharedPreferences.getString("user_token", null);
    }

    public void saveOtpCode(String otpCode) {
        sharedPreferences.edit()
                .putString("otp_code", otpCode)
                .apply();
    }

    public String getOtpCode() {
        return sharedPreferences.getString("otp_code", null);
    }
}