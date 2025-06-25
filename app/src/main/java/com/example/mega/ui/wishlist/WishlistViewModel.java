package com.example.mega.ui.wishlist;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WishlistViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public WishlistViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is wishlist fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
