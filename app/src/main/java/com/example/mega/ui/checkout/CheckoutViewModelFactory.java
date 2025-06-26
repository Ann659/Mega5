package com.example.mega.ui.checkout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;

public class CheckoutViewModelFactory implements ViewModelProvider.Factory {
    private final String orderId;

    public CheckoutViewModelFactory(String orderId) {
        this.orderId = orderId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CheckoutViewModel.class)) {
            return (T) new CheckoutViewModel();
        }
        Log.w("CheckoutViewModelFactory", "Unknown ViewModel class: " + modelClass.getName());
        return null;
    }
}