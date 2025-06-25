package com.example.mega;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mega.models.Notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private List<Notification> notifications;
    private final OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationsAdapter(List<Notification> notifications, OnNotificationClickListener listener) {
        this.notifications = new ArrayList<>(notifications);
        this.listener = listener;
    }

    public void updateNotifications(List<Notification> newNotifications) {
        this.notifications = new ArrayList<>(newNotifications);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);

        holder.itemView.setOnClickListener(v -> {
            notification.setRead(true);
            notifyItemChanged(position);
            listener.onNotificationClick(notification);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView messageText;
        private final TextView timeText;
        private final ImageView iconImage;

        public ViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.notification_title);
            messageText = view.findViewById(R.id.notification_message);
            timeText = view.findViewById(R.id.notification_time);
            iconImage = view.findViewById(R.id.notification_icon);
        }

        public void bind(Notification notification) {
            titleText.setText(notification.getTitle());
            messageText.setText(notification.getMessage());
            timeText.setText(formatTime(notification.getTimestamp()));
            iconImage.setImageResource(notification.getIconRes());

            int titleColor = notification.isRead() ? Color.DKGRAY : Color.BLACK;
            int messageColor = notification.isRead() ? Color.GRAY :
                    ContextCompat.getColor(itemView.getContext(), R.color.primary_color);

            titleText.setTextColor(titleColor);
            messageText.setTextColor(messageColor);

            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(),
                    notification.isRead() ? R.color.notification_read : R.color.notification_unread));
        }

        private String formatTime(long timestamp) {
            return new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
                    .format(new Date(timestamp));
        }
    }
}