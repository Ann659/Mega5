package com.example.mega;

import com.example.mega.models.Product;
import com.example.mega.supabase.ProductsClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductRepository {
    private static ProductRepository instance;
    private final ProductsClient productsClient;
    private final AuthManager authManager;

    private ProductRepository() {
        productsClient = new ProductsClient();
        authManager = AuthManager.getInstance();
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    public void getAllProducts(ProductsCallback callback) {
        productsClient.getAllProducts(new ProductsClient.ProductsCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    List<Product> products = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject productJson = response.getJSONObject(i);
                        Product product = new Product(productJson);
                        products.add(product);
                    }
                    callback.onSuccess(products);
                } catch (Exception e) {
                    callback.onError("Error parsing products: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void searchProducts(String query, ProductsCallback callback) {
        productsClient.searchProducts(query, new ProductsClient.ProductsCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    List<Product> results = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject productJson = response.getJSONObject(i);
                        Product product = new Product(productJson);
                        results.add(product);
                    }
                    callback.onSuccess(results);
                } catch (Exception e) {
                    callback.onError("Error parsing search results: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public interface ProductsCallback {
        void onSuccess(List<Product> products);
        void onError(String errorMessage);
    }
}
