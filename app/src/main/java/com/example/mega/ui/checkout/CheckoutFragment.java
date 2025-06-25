package com.example.mega.ui.checkout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
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

import com.example.mega.CartItem;
import com.example.mega.CartManager;
import com.example.mega.CheckoutProductAdapter;
import com.example.mega.OrderRepository;
import com.example.mega.OrderItem;
import com.example.mega.R;
import com.example.mega.databinding.FragmentCheckoutBinding;
import com.example.mega.models.Order;
import com.example.mega.ui.notifications.NotificationsViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutFragment extends Fragment {
    private CheckoutViewModel viewModel;
    private CheckoutProductAdapter adapter;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_checkout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);

        setupViews();
        setupObservers();
        viewModel.loadCartItems();
    }

    private void setupViews() {
        adapter = new CheckoutProductAdapter(new ArrayList<>());
        RecyclerView productsRecycler = rootView.findViewById(R.id.products_recycler);
        productsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        productsRecycler.setAdapter(adapter);

        ImageView backButton = rootView.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        Button confirmButton = rootView.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(v -> confirmOrder());
    }

    private void setupObservers() {
        viewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null && !items.isEmpty()) {
                adapter.updateItems(items);
                TextView totalPriceText = rootView.findViewById(R.id.total_price_text);
                totalPriceText.setText(String.format(Locale.getDefault(), "%.0f руб", calculateTotal(items)));
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double calculateTotal(List<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    private void confirmOrder() {
        RadioButton cashPayment = rootView.findViewById(R.id.cash_payment);
        String paymentMethod = cashPayment.isChecked() ? "cash" : "card";

        TextView addressText = rootView.findViewById(R.id.address_text);
        String address = addressText.getText().toString();

        viewModel.createOrder(paymentMethod, address, new OrderConfirmationCallback() {
            @Override
            public void onSuccess(Order order) {
                Toast.makeText(requireContext(), "Заказ оформлен успешно", Toast.LENGTH_SHORT).show();
                navigateToOrderDetails(order);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToOrderDetails(Order order) {
        Bundle args = new Bundle();
        args.putSerializable("order", (Serializable) order);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_checkoutFragment_to_orderDetailsFragment, args);
    }

    public interface OrderConfirmationCallback {
        void onSuccess(Order order);
        void onError(String message);
    }
}