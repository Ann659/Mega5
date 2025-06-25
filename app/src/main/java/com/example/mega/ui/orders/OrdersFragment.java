package com.example.mega.ui.orders;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mega.AuthManager;
import com.example.mega.CartItem;
import com.example.mega.OrderItem;
import com.example.mega.OrdersAdapter;
import com.example.mega.R;
import com.example.mega.databinding.FragmentOrdersBinding;
import com.example.mega.models.Order;
import com.example.mega.models.Product;
import com.example.mega.ProductAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersFragment extends Fragment {
    private FragmentOrdersBinding binding;
    private OrdersViewModel viewModel;
    private OrdersAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String userId = AuthManager.getInstance().getUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        viewModel = new ViewModelProvider(this, new OrdersViewModelFactory(userId)).get(OrdersViewModel.class);

        setupViews();
        setupObservers();
        viewModel.loadOrders();
    }

    private void setupViews() {
        adapter = new OrdersAdapter(new ArrayList<>(), this::showOrderDetails);
        binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewOrders.setAdapter(adapter);

        binding.buttonSearch.setOnClickListener(v -> performSearch());
    }

    private void setupObservers() {
        viewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            if (orders == null || orders.isEmpty()) {
                binding.textEmpty.setVisibility(View.VISIBLE);
                binding.recyclerViewOrders.setVisibility(View.GONE);
            } else {
                binding.textEmpty.setVisibility(View.GONE);
                binding.recyclerViewOrders.setVisibility(View.VISIBLE);
                adapter.updateOrders(orders);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch() {
        String query = binding.editTextSearch.getText().toString().trim();
        if (!query.isEmpty()) {
            viewModel.searchOrders(query);
        }
    }

    private void showOrderDetails(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Order #" + order.getId());

        StringBuilder message = new StringBuilder();
        message.append("Date: ").append(order.getFormattedDate()).append("\n");
        message.append("Status: ").append(order.getStatus()).append("\n");
        message.append("Total: $").append(String.format(Locale.US, "%.2f", order.getTotal())).append("\n\n");
        message.append("Items:\n");

        for (OrderItem item : order.getItems()) {
            message.append("- ").append(item.getProductName())
                    .append(" x").append(item.getQuantity())
                    .append(" ($").append(String.format(Locale.US, "%.2f", item.getPrice())).append(")\n");
        }

        builder.setMessage(message.toString());
        builder.setPositiveButton("OK", null);

        if ("processing".equals(order.getStatus())) {
            builder.setNegativeButton("Cancel Order", (dialog, which) -> {
                viewModel.updateOrderStatus(order.getId(), "cancelled");
            });
        }

        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}