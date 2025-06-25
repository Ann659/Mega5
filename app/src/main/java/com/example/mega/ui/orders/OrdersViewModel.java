package com.example.mega.ui.orders;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mega.OrderRepository;
import com.example.mega.models.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrdersViewModel extends ViewModel {
    private final MutableLiveData<List<Order>> orders = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final OrderRepository orderRepository;

    public OrdersViewModel(String userId) {
        this.orderRepository = OrderRepository.getInstance();
        loadOrders();
    }

    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadOrders() {
        orderRepository.getOrders(new OrderRepository.OrdersCallback() {
            @Override
            public void onSuccess(List<Order> orderList) {
                orders.postValue(orderList);
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue(error);
            }
        });
    }

    public void searchOrders(String query) {
        if (query == null || query.trim().isEmpty()) {
            orders.postValue(orders.getValue());
            return;
        }

        List<Order> currentOrders = orders.getValue();
        if (currentOrders == null || currentOrders.isEmpty()) {
            return;
        }

        final String lowerQuery = query.toLowerCase();
        List<Order> filtered = new ArrayList<>();

        for (Order order : currentOrders) {
            try {
                String id = String.valueOf(order.getId());
                boolean idMatches = id != null && id.toLowerCase().contains(lowerQuery);

                String status = order.getStatus();
                boolean statusMatches = status != null && status.toLowerCase().contains(lowerQuery);

                if (idMatches || statusMatches) {
                    filtered.add(order);
                }
            } catch (Exception e) {
                Log.e("OrderSearch", "Error processing order: " + e.getMessage());
            }
        }

        orders.postValue(filtered);
    }

    public void updateOrderStatus(int orderId, String newStatus) {
        orderRepository.updateOrderStatus(String.valueOf(orderId), newStatus, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order order) {
                loadOrders();
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue(error);
            }
        });
    }
}