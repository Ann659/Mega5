package com.example.mega;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.navigation.Navigation;

public class NavigationUtils {
    public static void safeNavigate(View view, int actionId) {
        try {
            Navigation.findNavController(view).navigate(actionId);
        } catch (Exception e) {
            Log.e("Navigation", "Failed to navigate", e);
        }
    }

    public static void safeNavigate(View view, int actionId, Bundle args) {
        try {
            Navigation.findNavController(view).navigate(actionId, args);
        } catch (Exception e) {
            Log.e("Navigation", "Failed to navigate with args", e);
        }
    }
}
