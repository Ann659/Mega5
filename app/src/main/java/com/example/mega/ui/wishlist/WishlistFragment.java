package com.example.mega.ui.wishlist;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mega.AuthManager;
import com.example.mega.R;
import com.example.mega.models.Product;
import com.example.mega.ProductAdapter;
import com.example.mega.supabase.ProductsClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {
    private ProductsClient productsClient;
    private AuthManager authManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        productsClient = new ProductsClient();
        authManager = AuthManager.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.title);
        GridView wishlistGrid = view.findViewById(R.id.wishlistGrid);

        title.setText("Избранное");
        setupWishlistItems(view);
    }

    private void setupWishlistItems(View view) {
        GridView wishlistGrid = view.findViewById(R.id.wishlistGrid);

        productsClient.getWishlistProducts(authManager.getUserId(), new ProductsClient.ProductsCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        List<Product> products = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject productJson = response.getJSONObject(i);
                            Product product = new Product(productJson);
                            products.add(product);
                        }

                        ProductAdapter adapter = new ProductAdapter(products);
                        wishlistGrid.setAdapter((ListAdapter) adapter);

                        wishlistGrid.setOnItemClickListener((parent, view1, position, id) -> {
                            Bundle args = new Bundle();
                            args.putParcelable("product", products.get(position));
                            safeNavigate(R.id.action_wishlistFragment_to_productDetailFragment, args);
                        });
                    } catch (Exception e) {
                        Log.e("WishlistFragment", "Error parsing wishlist products", e);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("WishlistFragment", errorMessage);
            }
        });
    }

    private void safeNavigate(int actionId, Bundle args) {
        try {
            Navigation.findNavController(requireView()).navigate(actionId, args);
        } catch (Exception e) {
            Log.e("Navigation", "Error navigating", e);
        }
    }
}