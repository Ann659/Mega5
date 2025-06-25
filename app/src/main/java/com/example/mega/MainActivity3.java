package com.example.mega;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mega.ui.account.AccountFragment;
import com.example.mega.ui.home.HomeFragment;
import com.example.mega.ui.orders.OrdersFragment;
import com.example.mega.ui.wishlist.WishlistFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity3 extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private final Map<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>() {{
        put(R.id.homeFragment, new HomeFragment());
        put(R.id.wishlistFragment, new WishlistFragment());
        put(R.id.ordersFragment, new OrdersFragment());
        put(R.id.accountFragment, new AccountFragment());
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        bottomNav = findViewById(R.id.bottom_nav);

        if (savedInstanceState == null) {
            setCurrentFragment(fragmentMap.get(R.id.homeFragment));
        }

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = fragmentMap.get(item.getItemId());
            if (fragment != null) {
                setCurrentFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}