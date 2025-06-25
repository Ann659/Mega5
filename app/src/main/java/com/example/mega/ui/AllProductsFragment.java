package com.example.mega.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.mega.GridSpacingItemDecoration;
import com.example.mega.ProductAdapter;
import com.example.mega.R;
import com.example.mega.models.Product;
import com.example.mega.supabase.ProductsClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllProductsFragment extends Fragment {
    private FragmentAllProductsBinding binding;
    private ProductAdapter adapter;
    private ProductsClient productsClient;
    private List<Product> allProducts = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productsClient = new ProductsClient();

        setupToolbar();
        setupRecyclerView();
        setupSearch();
        loadProducts();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                requireActivity().onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(new ArrayList<>());
        adapter.setOnItemClickListener(this::navigateToProductDetails);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), calculateSpanCount());
        binding.productsRecyclerView.setLayoutManager(layoutManager);
        binding.productsRecyclerView.setAdapter(adapter);
        binding.productsRecyclerView.addItemDecoration(
                new GridSpacingItemDecoration(calculateSpanCount(), 16, true));
    }

    private int calculateSpanCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return Math.max((int) (screenWidthDp / 180), 2);
    }

    private void setupSearch() {
        binding.searchIcon.setOnClickListener(v -> performSearch());

        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    loadProducts();
                }
            }
        });
    }

    private void performSearch() {
        String query = binding.searchInput.getText().toString().trim();
        if (query.isEmpty()) {
            loadProducts();
            return;
        }

        showLoading(true);
        productsClient.searchProducts(query, new ProductsClient.ProductsCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        List<Product> filteredProducts = parseProducts(response);
                        adapter.updateProducts(filteredProducts);
                        binding.emptyState.setVisibility(filteredProducts.isEmpty() ? View.VISIBLE : View.GONE);
                    } catch (JSONException e) {
                        showError("Ошибка обработки данных");
                    } finally {
                        showLoading(false);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    showError("Ошибка поиска: " + errorMessage);
                    showLoading(false);
                });
            }
        });
    }

    private void loadProducts() {
        showLoading(true);
        productsClient.getAllProducts(new ProductsClient.ProductsCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        allProducts = parseProducts(response);
                        adapter.updateProducts(allProducts);
                        binding.emptyState.setVisibility(allProducts.isEmpty() ? View.VISIBLE : View.GONE);
                    } catch (JSONException e) {
                        showError("Ошибка обработки данных");
                    } finally {
                        showLoading(false);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    showError("Ошибка загрузки: " + errorMessage);
                    showLoading(false);
                });
            }
        });
    }

    private List<Product> parseProducts(JSONArray jsonArray) throws JSONException {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonProduct = jsonArray.getJSONObject(i);
            Product product = new Product(
            );
            products.add(product);
        }
        return products;
    }

    private void navigateToProductDetails(Product product) {
        try {
            Bundle args = new Bundle();
            args.putParcelable("product", product);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_allProducts_to_productDetail, args);
        } catch (Exception e) {
            Log.e("Navigation", "Error navigating to product details", e);
            showError("Не удалось открыть детали товара");
        }
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.productsRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Ошибка")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}