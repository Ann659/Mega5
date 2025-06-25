package com.example.mega.ui.account;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.mega.AuthManager;
import com.example.mega.ProductAdapter;
import com.example.mega.R;
import com.example.mega.supabase.ProductsClient;
import com.example.mega.supabase.SupabaseClient;
import com.example.mega.models.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountFragment extends Fragment {
    private TextView profileName;
    private TextView registrationDate;
    private SupabaseClient supabaseClient;
    private AuthManager authManager;
    private ProductsClient productsClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        profileName = view.findViewById(R.id.profile_name);
        registrationDate = view.findViewById(R.id.registration_date);
        supabaseClient = new SupabaseClient();
        authManager = AuthManager.getInstance();
        productsClient = new ProductsClient();

        loadCachedUserData();
        fetchUserDataFromServer();
        setupUI(view);
        setupProductsRecyclerView(view);

        return view;
    }

    private void loadCachedUserData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "Пользователь");
        long regDate = prefs.getLong("registration_date", 0L);

        profileName.setText(userName);
        if (regDate > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            registrationDate.setText(sdf.format(new Date(regDate)));
        }
    }

    private void fetchUserDataFromServer() {
        String userId = authManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            return;
        }

        supabaseClient.getUserById(userId, new SupabaseClient.SupabaseCallback() {
            @Override
            public void onSuccess(JSONArray users) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        if (users.length() > 0) {
                            JSONObject user = users.getJSONObject(0);
                            String fullName = user.getString("full_name");
                            String createdAt = user.optString("created_at", "");

                            profileName.setText(fullName);

                            if (!createdAt.isEmpty()) {
                                try {
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
                                    Date date = inputFormat.parse(createdAt);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                                    registrationDate.setText(outputFormat.format(date));
                                } catch (ParseException e) {
                                    Log.e("AccountFragment", "Error parsing date", e);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("AccountFragment", "Error parsing user data", e);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("AccountFragment", errorMessage);
            }
        });
    }

    private void setupUI(View view) {
        TextView viewAll = view.findViewById(R.id.view_all);
        viewAll.setOnClickListener(v ->
                Toast.makeText(getContext(), "Открытие списка всех товаров", Toast.LENGTH_SHORT).show());

        ImageView back = view.findViewById(R.id.back);
        back.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupProductsRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.products_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));

        productsClient.getFavoriteProducts(authManager.getUserId(), new ProductsClient.ProductsCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        List<Product> favoriteProducts = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject productJson = response.getJSONObject(i);
                            Product product = new Product(productJson);
                            favoriteProducts.add(product);
                        }

                        ProductAdapter adapter = new ProductAdapter(favoriteProducts);
                        recyclerView.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.e("AccountFragment", "Error parsing favorite products", e);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("AccountFragment", "Error loading favorites: " + errorMessage);
            }
        });
    }
}