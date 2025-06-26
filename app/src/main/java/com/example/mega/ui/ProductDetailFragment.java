package com.example.mega.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mega.CartManager;
import com.example.mega.ProductAdapter;
import com.example.mega.R;
import com.example.mega.ReviewAdapter;
import com.example.mega.models.Product;
import com.example.mega.models.Review;
import com.example.mega.supabase.ProductsClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductDetailFragment extends Fragment {
    private Product currentProduct;
    private boolean isAddedToCart = false;
    private int selectedColor = 0;
    private int quantity = 1;
    private ProductsClient productsClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        productsClient = new ProductsClient();

        if (getArguments() != null) {
            currentProduct = getArguments().getParcelable("product");
        }

        setupUI(view);
        return view;
    }


    private void setupUI(View view) {
        ImageView backButton = view.findViewById(R.id.back_button);
        ImageView shareButton = view.findViewById(R.id.share_button);
        ImageView cartButton = view.findViewById(R.id.cart_button);

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        shareButton.setOnClickListener(v -> shareProduct());
        cartButton.setOnClickListener(v -> navigateToCart());

        ImageView productImage = view.findViewById(R.id.product_image);
        TextView productName = view.findViewById(R.id.product_name);
        TextView productPrice = view.findViewById(R.id.product_price);
        RatingBar ratingBar = view.findViewById(R.id.rating_bar);
        TextView reviewsCount = view.findViewById(R.id.reviews_count);
        TextView availableCount = view.findViewById(R.id.available_count);

        productImage.setImageResource(currentProduct.getImagePath());
        productName.setText(currentProduct.getName());
        productPrice.setText(String.format(Locale.getDefault(), "%.0f руб", currentProduct.getPrice()));
        ratingBar.setRating(currentProduct.getRating());
        reviewsCount.setText(String.format("(%d)", currentProduct.getReviewCount()));
        availableCount.setText("Доступно: 250");

        ImageView storeAvatar = view.findViewById(R.id.store_avatar);
        TextView storeName = view.findViewById(R.id.store_name);

        storeAvatar.setImageResource(R.drawable.account);
        storeName.setText("Official Store");

        TextView description = view.findViewById(R.id.product_description);
        description.setText("Динамик оснащен диафрагмой...");

        setupReviewsSystem(view);
        setupRecommendedProducts(view);
        setupCartButtons(view);
    }


    private void setupReviewsSystem(View view) {
        RecyclerView reviewsRecycler = view.findViewById(R.id.reviews_recycler);
        reviewsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review("Иван Иванов", 4.5f, "Отличный товар!", "15 мая 2023"));
        reviews.add(new Review("Анна Петрова", 5f, "Прекрасное качество за эти деньги", "10 июня 2023"));

        ReviewAdapter adapter = new ReviewAdapter(reviews);
        reviewsRecycler.setAdapter(adapter);

        RatingBar userRatingBar = view.findViewById(R.id.user_rating_bar);
        EditText reviewInput = view.findViewById(R.id.review_input);
        Button submitReview = view.findViewById(R.id.submit_review);

        submitReview.setOnClickListener(v -> {
            String reviewText = reviewInput.getText().toString();
            float rating = userRatingBar.getRating();

            if (!reviewText.isEmpty() && rating > 0) {
                String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(new Date());

                Review newReview = new Review(
                        "Вы",
                        rating,
                        reviewText,
                        currentDate
                );

                reviews.add(0, newReview);
                adapter.notifyItemInserted(0);
                reviewsRecycler.smoothScrollToPosition(0);

                reviewInput.setText("");
                userRatingBar.setRating(0);
            }
        });
        if (currentProduct != null) {
            productsClient.getProductDetails(currentProduct.getId(), new ProductsClient.ProductDetailsCallback() {
                @Override
                public void onSuccess(JSONObject productDetails) {
                    requireActivity().runOnUiThread(() -> {
                        try {
                            currentProduct.setDescription(productDetails.optString("description", ""));
                            currentProduct.setRating((float) productDetails.optDouble("rating", 0));
                            currentProduct.setReviewCount(productDetails.optInt("review_count", 0));

                            TextView description = view.findViewById(R.id.product_description);
                            description.setText(currentProduct.getDescription());

                            RatingBar ratingBar = view.findViewById(R.id.rating_bar);
                            ratingBar.setRating(currentProduct.getRating());

                            TextView reviewsCount = view.findViewById(R.id.reviews_count);
                            reviewsCount.setText(String.format("(%d)", currentProduct.getReviewCount()));

                        } catch (Exception e) {
                            Log.e("ProductDetail", "Error updating product details", e);
                        }
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("ProductDetail", errorMessage);
                }
            });
        }

        setupRecommendedProducts(view);
    }

    private void setupRecommendedProducts(View view) {
        RecyclerView recommendedRecycler = view.findViewById(R.id.recommended_recycler);
        recommendedRecycler.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));

        productsClient.getProductsByCategory(currentProduct.getId(), new ProductsClient.ProductsCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        List<Product> recommended = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject productJson = response.getJSONObject(i);
                            Product product = new Product(productJson);
                            recommended.add(product);
                        }

                        ProductAdapter adapter = new ProductAdapter(recommended);
                        recommendedRecycler.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.e("ProductDetail", "Error parsing recommended products", e);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ProductDetail", errorMessage);
            }
        });
    }


    private List<Product> getRecommendedProducts() {
        List<Product> recommended = new ArrayList<>();
        return recommended;
    }

    private void setupCartButtons(View view) {
        Button addToCartBtn = view.findViewById(R.id.add_to_cart_btn);
        Button addedToCartBtn = view.findViewById(R.id.added_to_cart_btn);

        if (isAddedToCart) {
            addToCartBtn.setVisibility(View.GONE);
            addedToCartBtn.setVisibility(View.VISIBLE);
        } else {
            addToCartBtn.setVisibility(View.VISIBLE);
            addedToCartBtn.setVisibility(View.GONE);
        }

        addToCartBtn.setOnClickListener(v -> showAddToCartDialog());
        addedToCartBtn.setOnClickListener(v -> showAddToCartDialog());
    }

    private void showAddToCartDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_add_to_cart);

        TextView quantityText = dialog.findViewById(R.id.quantity_text);
        Button minusBtn = dialog.findViewById(R.id.minus_btn);
        Button plusBtn = dialog.findViewById(R.id.plus_btn);

        minusBtn.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
            }
        });

        plusBtn.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
        });

        setupColorSelection(dialog);

        Button confirmBtn = dialog.findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(v -> {
            addProductToCart();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.close_btn).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void setupColorSelection(Dialog dialog) {
        Button blackBtn = dialog.findViewById(R.id.color_black);
        Button whiteBtn = dialog.findViewById(R.id.color_white);
        Button blueBtn = dialog.findViewById(R.id.color_blue);

        blackBtn.setOnClickListener(v -> selectColor(0, blackBtn, whiteBtn, blueBtn));
        whiteBtn.setOnClickListener(v -> selectColor(1, blackBtn, whiteBtn, blueBtn));
        blueBtn.setOnClickListener(v -> selectColor(2, blackBtn, whiteBtn, blueBtn));

        selectColor(0, blackBtn, whiteBtn, blueBtn);
    }

    private void selectColor(int color, Button... buttons) {
        selectedColor = color;
        for (int i = 0; i < buttons.length; i++) {
            if (i == color) {
                buttons[i].setBackgroundResource(R.color.nav_active);
            } else {
                buttons[i].setBackgroundResource(R.color.nav_inactive);
            }
        }
    }

    private void addProductToCart() {
        isAddedToCart = true;
        CartManager.getInstance().addToCart(currentProduct, quantity, selectedColor);
        updateCartButtonVisibility();
    }

    private void updateCartButtonVisibility() {
        if (getView() != null) {
            Button addToCartBtn = getView().findViewById(R.id.add_to_cart_btn);
            Button addedToCartBtn = getView().findViewById(R.id.added_to_cart_btn);

            addToCartBtn.setVisibility(isAddedToCart ? View.GONE : View.VISIBLE);
            addedToCartBtn.setVisibility(isAddedToCart ? View.VISIBLE : View.GONE);
        }
    }

    private void shareProduct() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        String shareText = "Посмотрите этот товар: " + currentProduct.getName() +
                "\nЦена: " + currentProduct.getPrice() + " руб." +
                "\nСсылка: https://example.com/product/" + currentProduct.getId();

        Uri imageUri = getImageUri(currentProduct.getImagePath());

        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        shareIntent.putExtra(Intent.EXTRA_TITLE, "Поделиться товаром");

        startActivity(Intent.createChooser(shareIntent, "Поделиться через"));
    }

    private Uri getImageUri(int imageResId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageResId);

        File file = new File(getContext().getExternalCacheDir(), "product_share.jpg");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return FileProvider.getUriForFile(
                getContext(),
                getContext().getPackageName() + ".fileprovider",
                file
        );
    }

    private void navigateToCart() {
        try {
            Navigation.findNavController(requireView()).navigate(R.id.action_to_cartFragment);
        } catch (Exception e) {
            Log.e("Navigation", "Error navigating to cart", e);
            Toast.makeText(getContext(), "Ошибка при переходе в корзину", Toast.LENGTH_SHORT).show();
        }
    }
}