package com.example.mega;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mega.R;
import com.example.mega.ui.account.AccountFragment;
import com.example.mega.ui.home.HomeFragment;
import com.example.mega.ui.orders.OrdersFragment;
import com.example.mega.ui.wishlist.WishlistFragment;

public class MainActivity extends AppCompatActivity {
    private ImageView homeBtn, wishlistBtn, ordersBtn, accountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeBtn = findViewById(R.id.home_btn);
        wishlistBtn = findViewById(R.id.wishlist_btn);
        ordersBtn = findViewById(R.id.orders_btn);
        accountBtn = findViewById(R.id.account_btn);

        loadFragment(new HomeFragment());

        homeBtn.setOnClickListener(v -> {
            setActiveButton(homeBtn);
            loadFragment(new HomeFragment());
        });

        wishlistBtn.setOnClickListener(v -> {
            setActiveButton(wishlistBtn);
            loadFragment(new WishlistFragment());
        });

        ordersBtn.setOnClickListener(v -> {
            setActiveButton(ordersBtn);
            loadFragment(new OrdersFragment());
        });

        accountBtn.setOnClickListener(v -> {
            setActiveButton(accountBtn);
            loadFragment(new AccountFragment());
        });
    }

    private void loadFragment(Fragment fragment) {

    }

    private void setActiveButton(ImageView activeButton) {
        homeBtn.setColorFilter(ContextCompat.getColor(this, R.color.nav_inactive));
        wishlistBtn.setColorFilter(ContextCompat.getColor(this, R.color.nav_inactive));
        ordersBtn.setColorFilter(ContextCompat.getColor(this, R.color.nav_inactive));
        accountBtn.setColorFilter(ContextCompat.getColor(this, R.color.nav_inactive));

        activeButton.setColorFilter(ContextCompat.getColor(this, R.color.nav_active));
    }
}

