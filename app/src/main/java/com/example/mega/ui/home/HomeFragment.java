package com.example.mega.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.example.mega.CategoryAdapter;
import com.example.mega.DialogUtils;
import com.example.mega.GridProductAdapter;
import com.example.mega.R;
import com.example.mega.models.Category;
import com.example.mega.models.Category1;
import com.example.mega.models.Product;
import com.example.mega.ProductAdapter;
import com.example.mega.supabase.CategoriesClient;
import com.example.mega.supabase.ProductsClient;
import com.example.mega.ui.wishlist.WishlistFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private ProductsClient productsClient;
    private CategoriesClient categoriesClient;
    private List<Product> allProducts = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        productsClient = new ProductsClient();
        categoriesClient = new CategoriesClient();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView notificationIcon = view.findViewById(R.id.notificationIcon);
        ImageView cartIcon = view.findViewById(R.id.cartIcon);
        EditText searchEditText = view.findViewById(R.id.searchEditText);
        ImageView searchIcon = view.findViewById(R.id.searchIcon);
        GridView searchResultsGrid = view.findViewById(R.id.searchResultsGrid);
        LinearLayout featuredContainer = view.findViewById(R.id.featuredContainer);
        TextView viewAllCategories = view.findViewById(R.id.viewAllCategories);
        GridView categoriesGrid = view.findViewById(R.id.categoriesGrid);
        TextView viewAllProducts = view.findViewById(R.id.viewAllProducts);
        GridView productsGrid = view.findViewById(R.id.productsGrid);

        searchResultsGrid.setVisibility(View.GONE);

        loadProducts(productsGrid, searchResultsGrid);
        loadCategories(categoriesGrid);
        setupFeaturedItems(featuredContainer);

        notificationIcon.setOnClickListener(v ->
                safeNavigate(R.id.action_homeFragment_to_notificationsFragment));

        cartIcon.setOnClickListener(v ->
                safeNavigate(R.id.action_homeFragment_to_cartFragment));

        viewAllCategories.setOnClickListener(v ->
                safeNavigate(R.id.action_homeFragment_to_allCategoriesFragment));

        viewAllProducts.setOnClickListener(v ->
                safeNavigate(R.id.action_homeFragment_to_allProductsFragment));

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    searchResultsGrid.setVisibility(View.GONE);
                } else {
                    searchResultsGrid.setVisibility(View.VISIBLE);
                    updateSearchResults(query, searchResultsGrid);
                }
            }
        });

        searchIcon.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchResultsGrid.setVisibility(View.VISIBLE);
                updateSearchResults(query, searchResultsGrid);
            }
        });
    }

    private void loadProducts(GridView productsGrid, GridView searchResultsGrid) {
        productsClient.getAllProducts(new ProductsClient.ProductsCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        allProducts.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject productJson = response.getJSONObject(i);
                            Product product = new Product(productJson);
                            allProducts.add(product);
                        }

                        GridProductAdapter adapter = new GridProductAdapter(getContext(), allProducts);
                        productsGrid.setAdapter(adapter);
                        searchResultsGrid.setAdapter(adapter);

                        productsGrid.setOnItemClickListener((parent, view, position, id) ->
                                navigateToProductDetail(allProducts.get(position)));

                        searchResultsGrid.setOnItemClickListener((parent, view, position, id) ->
                                navigateToProductDetail(allProducts.get(position)));

                    } catch (Exception e) {
                        Log.e("HomeFragment", "Error parsing products", e);
                        Toast.makeText(getContext(), "Error loading products", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    Log.e("HomeFragment", errorMessage);
                    Toast.makeText(getContext(), "Error loading products", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadCategories(GridView categoriesGrid) {
        categoriesClient.getAllCategories(new CategoriesClient.CategoriesCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        List<Category1> categories = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject categoryJson = response.getJSONObject(i);
                            Category1 category = new Category1(
                                    categoryJson.getInt("category_id"),
                                    categoryJson.getString("name")
                            );
                            categories.add(category);
                        }

                        CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);
                        categoriesGrid.setAdapter(adapter);

                        categoriesGrid.setOnItemClickListener((parent, view, position, id) ->
                                navigateToCategoryProducts(categories.get(position)));

                    } catch (Exception e) {
                        Log.e("HomeFragment", "Error parsing categories", e);
                        Toast.makeText(getContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    Log.e("HomeFragment", errorMessage);
                    Toast.makeText(getContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupFeaturedItems(LinearLayout container) {
        container.removeAllViews();

        int[] featuredImages = {R.drawable.special1, R.drawable.special2};
        String[] featuredTitles = {"Специальное предложение", "Новинки сезона"};

        for (int i = 0; i < featuredImages.length; i++) {
            View featuredItem = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_featured, container, false);

            ImageView image = featuredItem.findViewById(R.id.featuredImage);
            TextView title = featuredItem.findViewById(R.id.featuredTitle);

            image.setImageResource(featuredImages[i]);
            title.setText(featuredTitles[i]);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.8),
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            featuredItem.setLayoutParams(params);

            container.addView(featuredItem);
        }
    }

    private void updateSearchResults(String query, GridView searchResultsGrid) {
        List<Product> searchResults = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(lowerQuery)) {
                searchResults.add(product);
            }
        }

        if (searchResults.isEmpty()) {
            Toast.makeText(getContext(), "Ничего не найдено", Toast.LENGTH_SHORT).show();
            searchResultsGrid.setVisibility(View.GONE);
        } else {
            ((GridProductAdapter) searchResultsGrid.getAdapter()).updateData(searchResults);
        }
    }

    private void navigateToProductDetail(Product product) {
        try {
            Bundle bundle = new Bundle();
            bundle.putParcelable("product", product);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_productDetailFragment, bundle);
        } catch (Exception e) {
            Log.e("Navigation", "Error navigating to product detail", e);
            Toast.makeText(getContext(), "Error opening product", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToCategoryProducts(Category1 category) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("category_id", category.getId());
            bundle.putString("category_name", category.getName());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_home_to_food_category, bundle);
        } catch (Exception e) {
            Log.e("Navigation", "Error navigating to category products", e);
            Toast.makeText(getContext(), "Error opening category", Toast.LENGTH_SHORT).show();
        }
    }

    private void safeNavigate(int actionId) {
        try {
            Navigation.findNavController(requireView()).navigate(actionId);
        } catch (Exception e) {
            Log.e("Navigation", "Error navigating", e);
            Toast.makeText(getContext(), "Navigation error", Toast.LENGTH_SHORT).show();
        }
    }
}