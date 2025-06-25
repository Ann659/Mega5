package com.example.mega.ui;

import android.app.AlertDialog;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mega.CartAdapter;
import com.example.mega.CartItem;
import com.example.mega.CartManager;
import com.example.mega.OrderItem;
import com.example.mega.OrderRepository;
import com.example.mega.R;
import com.example.mega.databinding.FragmentCartBinding;
import com.example.mega.models.Order;
import com.example.mega.models.Product;
import com.example.mega.ui.notifications.NotificationsViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private List<CartItem> cartItems = new ArrayList<>();
    private CartAdapter adapter;
    private double totalPrice = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews();
        loadCartItems();
    }

    private void setupViews() {
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CartAdapter(cartItems, new CartAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                updateItemQuantity(position, newQuantity);
            }

            @Override
            public void onRemoveItem(int position) {
                removeItem(position);
            }

            @Override
            public void onItemClicked(int position) {
                navigateToProductDetail(cartItems.get(position).getProduct());
            }
        });
        binding.cartRecyclerView.setAdapter(adapter);

        binding.backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.checkoutButton.setOnClickListener(v -> navigateToCheckout());
        binding.continueShoppingButton.setOnClickListener(v -> navigateToHome());

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterCartItems(s.toString());
            }
        });
    }

    private void loadCartItems() {
        cartItems.clear();
        cartItems.addAll(getCartItemsFromSource());
        adapter.notifyDataSetChanged();
        updateCartUI(!cartItems.isEmpty());
        updateTotalPrice();
    }

    private List<CartItem> getCartItemsFromSource() {
        return new ArrayList<>();
    }

    private void updateItemQuantity(int position, int newQuantity) {
        if (position >= 0 && position < cartItems.size()) {
            CartItem item = cartItems.get(position);
            item.setQuantity(newQuantity);
            adapter.notifyItemChanged(position);
            updateTotalPrice();
        }
    }

    private void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            adapter.notifyItemRemoved(position);
            updateCartUI(!cartItems.isEmpty());
            updateTotalPrice();
        }
    }

    private void filterCartItems(String query) {
        List<CartItem> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(cartItems);
        } else {
            for (CartItem item : cartItems) {
                if (item.getProduct().getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.updateItems(filteredList);
    }

    private void updateCartUI(boolean hasItems) {
        binding.emptyCartView.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        binding.cartRecyclerView.setVisibility(hasItems ? View.VISIBLE : View.GONE);
        binding.checkoutButton.setVisibility(hasItems ? View.VISIBLE : View.GONE);
        binding.searchEditText.setVisibility(hasItems ? View.VISIBLE : View.GONE);
    }

    private void updateTotalPrice() {
        totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
        binding.totalPriceText.setText(String.format(Locale.getDefault(), "%.0f руб", totalPrice));
    }

    private void navigateToProductDetail(Product product) {
        try {
            Bundle args = new Bundle();
            args.putParcelable("product", product);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_cartFragment_to_productDetailFragment, args);
        } catch (Exception e) {
            Log.e("Navigation", "Error navigating to product detail", e);
            Toast.makeText(getContext(), "Ошибка при открытии товара", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Корзина пуста", Toast.LENGTH_SHORT).show();
            return;
        }

        createOrder(new OrderCallback() {
            @Override
            public void onSuccess(Order order) {
                try {
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_cartFragment_to_checkoutFragment);
                } catch (Exception e) {
                    Log.e("Navigation", "Error navigating to checkout", e);
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createOrder(OrderCallback callback) {
        Order order = new Order(cartItems, totalPrice);
        callback.onSuccess(order);
    }

    private void navigateToHome() {
        try {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_cartFragment_to_homeFragment);
        } catch (Exception e) {
            Log.e("Navigation", "Error returning to home", e);
        }
    }

    interface OrderCallback {
        void onSuccess(Order order);
        void onError(String message);
    }
}