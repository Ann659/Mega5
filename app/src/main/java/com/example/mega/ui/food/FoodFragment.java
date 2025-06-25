package com.example.mega.ui.food;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mega.DialogUtils;
import com.example.mega.ProductAdapter;
import com.example.mega.R;
import com.example.mega.models.Product;
import com.example.mega.supabase.ProductsClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoodFragment extends Fragment {
    private ProductsClient productsClient;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private String currentSort = "Популярное";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        productsClient = new ProductsClient();

        RecyclerView recyclerView = view.findViewById(R.id.productsRecyclerView);
        EditText searchField = view.findViewById(R.id.searchField);
        ImageView cartButton = view.findViewById(R.id.cartButton);
        Button filterButton = view.findViewById(R.id.filterButton);
        ImageView backButton = view.findViewById(R.id.backButton);
        TextView resultsCount = view.findViewById(R.id.resultsCount);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdapter(filteredProducts);
        recyclerView.setAdapter(adapter);

        loadProducts(1);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    String query = s.toString().trim();
                    filterProducts(query);
                    updateResultsCount();
                };
                searchHandler.postDelayed(searchRunnable, 300);
            }
        });

        filterButton.setOnClickListener(v -> {
            try {
                Bundle args = new Bundle();
                args.putString("currentSort", currentSort);
                Navigation.findNavController(v).navigate(R.id.action_to_filters, args);
            } catch (Exception e) {
                Log.e("Navigation", "Failed to navigate to filters", e);
                Toast.makeText(v.getContext(), "Ошибка при открытии фильтров", Toast.LENGTH_SHORT).show();
            }
        });

        cartButton.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigate(R.id.action_to_cartFragment);
            } catch (Exception e) {
                Log.e("Navigation", "Failed to navigate to cart", e);
                Toast.makeText(v.getContext(), "Ошибка при открытии корзины", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        getParentFragmentManager().setFragmentResultListener("filters", this, (requestKey, result) -> {
            String priceFrom = result.getString("priceFrom");
            String priceTo = result.getString("priceTo");
            currentSort = result.getString("sortType", "Популярное");

            applyFiltersAndSort(priceFrom, priceTo, currentSort);
            updateResultsCount();
        });

        return view;
    }

    private void loadProducts(int categoryId) {
        productsClient.getProductsByCategory(categoryId, new ProductsClient.ProductsCallback() {
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

                        filteredProducts.clear();
                        filteredProducts.addAll(allProducts);
                        adapter.notifyDataSetChanged();
                        updateResultsCount();

                    } catch (Exception e) {
                        Log.e("FoodFragment", "Error parsing products", e);
                        Toast.makeText(getContext(), "Error loading products", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    Log.e("FoodFragment", errorMessage);
                    Toast.makeText(getContext(), "Error loading products: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();

        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Product product : allProducts) {
                if (product.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredProducts.add(product);
                }
            }

            if (filteredProducts.isEmpty()) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Ничего не найдено")
                        .setMessage("По запросу \"" + query + "\" товаров не найдено")
                        .setPositiveButton("OK", null)
                        .show();

                filteredProducts.addAll(allProducts);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateResultsCount() {
        if (getView() != null) {
            TextView resultsCount = getView().findViewById(R.id.resultsCount);
            if (resultsCount != null) {
                resultsCount.setText("Найдено товаров: " + filteredProducts.size());
            }
        }
    }

    private void applyFiltersAndSort(String priceFrom, String priceTo, String sortType) {
        filterByPrice(priceFrom, priceTo);
        sortProducts(sortType);
        adapter.notifyDataSetChanged();
    }

    private void filterByPrice(String priceFrom, String priceTo) {
        filteredProducts.clear();

        try {
            double minPrice = priceFrom.isEmpty() ? 0 : Double.parseDouble(priceFrom);
            double maxPrice = priceTo.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(priceTo);

            if (minPrice > maxPrice) {
                double temp = minPrice;
                minPrice = maxPrice;
                maxPrice = temp;
            }

            for (Product product : allProducts) {
                if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                    filteredProducts.add(product);
                }
            }

            if (filteredProducts.isEmpty()) {
                filteredProducts.addAll(allProducts);
                Toast.makeText(getContext(), "Нет товаров в выбранном диапазоне цен", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            filteredProducts.addAll(allProducts);
            Toast.makeText(getContext(), "Введите корректные значения цен", Toast.LENGTH_SHORT).show();
        }
    }

    private void sortProducts(String sortType) {
        switch (sortType) {
            case "Сначала дешевые":
                Collections.sort(filteredProducts, (p1, p2) ->
                        Double.compare(p1.getPrice(), p2.getPrice()));
                break;
            case "Сначала дорогие":
                Collections.sort(filteredProducts, (p1, p2) ->
                        Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case "Высокий рейтинг":
                Collections.sort(filteredProducts, (p1, p2) ->
                        Float.compare(p2.getRating(), p1.getRating()));
                break;
            case "Популярное":
            default:
                filteredProducts.clear();
                filteredProducts.addAll(allProducts);
                break;
        }
    }
}