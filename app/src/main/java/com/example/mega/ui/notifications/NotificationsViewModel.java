package com.example.mega.ui.notifications;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mega.OrderRepository;
import com.example.mega.R;
import com.example.mega.models.Order;
import com.example.mega.models.Notification;
import com.example.mega.NotificationsAdapter;
import com.example.mega.ui.notifications.NotificationsFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsViewModel extends ViewModel {
    private final MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();
    private final OrderRepository orderRepository = OrderRepository.getInstance();

    public NotificationsViewModel() {
        loadNotifications();
    }

    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    public void addNewOrderNotification(Order order) {
        List<Notification> current = notifications.getValue() != null ?
                new ArrayList<>(notifications.getValue()) : new ArrayList<>();

        current.add(createOrderNotification(order));
        notifications.setValue(current);
    }

    public void loadNotifications() {
        List<Notification> result = new ArrayList<>();

        for (Order order : orderRepository.getCachedOrders()) {
            result.add(createOrderNotification(order));
        }

        result.addAll(getSystemNotifications());

        if (result.isEmpty()) {
            result.add(createDemoNotification());
        }

        notifications.setValue(result);
    }
    private Notification createOrderNotification(Order order) {
        return new Notification(
                Notification.generateId(),
                "Заказ #" + order.getId(),
                "Статус: " + order.getStatus(),
                order.getDate(),
                getStatusIcon(order.getStatus()),
                false,
                order.getId()
        );
    }

    private List<Notification> getSystemNotifications() {
        return Collections.emptyList();
    }

    private Notification createDemoNotification() {
        return new Notification(
                0,
                "Нет активных заказов",
                "Здесь будут появляться ваши уведомления",
                System.currentTimeMillis(),
                R.drawable.notification,
                true,
                -1
        );
    }

    private int getStatusIcon(String status) {
        switch (status.toLowerCase()) {
            case "доставлен": return R.drawable.delivered;
            case "в обработке": return R.drawable.processing;
            case "отправлен": return R.drawable.shipped;
            default: return R.drawable.order_default;
        }
    }
    public void markNotificationAsRead(int notificationId) {
        List<Notification> current = notifications.getValue();
        if (current != null) {
            for (Notification notification : current) {
                if (notification.getId() == notificationId) {
                    notification.setRead(true);
                    break;
                }
            }
            notifications.setValue(new ArrayList<>(current));
        }
    }

    public void getCloseable(int id, NotificationsFragment.OrderCallback заказНеНайден) {
        getNotifications();
    }
}