package com.example.mega.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mega.NotificationsAdapter;
import com.example.mega.OrderRepository;
import com.example.mega.R;
import com.example.mega.databinding.FragmentNotificationsBinding;
import com.example.mega.models.Order;
import com.example.mega.models.Notification;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private NotificationsViewModel viewModel;
    private NotificationsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        setupViews();
        setupObservers();
        viewModel.loadNotifications();
    }

    private void setupViews() {
        adapter = new NotificationsAdapter(new ArrayList<>(), this::onNotificationClick);
        binding.notificationsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notificationsRecycler.setAdapter(adapter);

        binding.backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.searchButton.setOnClickListener(v -> showSearch());
        binding.cartButton.setOnClickListener(v -> navigateToCart());
    }

    private void setupObservers() {
        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            adapter.updateNotifications(notifications);
            updateEmptyView(notifications.isEmpty());
        });
    }

    private void updateEmptyView(boolean isEmpty) {
        binding.emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.notificationsRecycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void onNotificationClick(Notification notification) {
        if (notification.getType().equals("order")) {
            viewModel.getCloseable(notification.getId(), new OrderCallback() {
                @Override
                public void onOrderReceived(Order order) {
                    if (order != null) {
                        navigateToOrderDetails(order);
                        viewModel.markNotificationAsRead(notification.getId());
                    } else {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Заказ не найден", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), notification.getMessage(), Toast.LENGTH_SHORT).show();
            viewModel.markNotificationAsRead(notification.getId());
        }
    }

    public interface OrderCallback {
        void onOrderReceived(Order order);
    }

    private void navigateToOrderDetails(Order order) {
        try {
            Bundle args = new Bundle();
            args.putParcelable("order", order);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_notificationsFragment_to_orderDetailsFragment, args);
        } catch (Exception e) {
            Log.e("Navigation", "Failed to navigate to order details", e);
            Toast.makeText(getContext(), "Не удалось открыть детали заказа", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSearch() {
        Toast.makeText(getContext(), "Поиск по уведомлениям", Toast.LENGTH_SHORT).show();
    }

    private void navigateToCart() {
        try {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_notificationsFragment_to_cartFragment);
        } catch (Exception e) {
            Log.e("Navigation", "Failed to navigate to cart", e);
            Toast.makeText(getContext(), "Ошибка при переходе в корзину", Toast.LENGTH_SHORT).show();
        }
    }
}