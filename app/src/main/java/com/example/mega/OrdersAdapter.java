package com.example.mega;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mega.models.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {
    private List<Order> orders;
    private final OrderClickListener clickListener;

    public OrdersAdapter(List<Order> orders, OrderClickListener clickListener) {
        this.orders = orders;
        this.clickListener = clickListener;
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
        holder.itemView.setOnClickListener(v -> clickListener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView textOrderId;
        private final TextView textOrderDate;
        private final TextView textOrderStatus;
        private final TextView textOrderTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.text_order_id);
            textOrderDate = itemView.findViewById(R.id.text_order_date);
            textOrderStatus = itemView.findViewById(R.id.text_order_status);
            textOrderTotal = itemView.findViewById(R.id.text_order_total);
        }

        public void bind(Order order) {
            textOrderId.setText("#" + order.getId());
            textOrderDate.setText(order.getFormattedDate());
            textOrderStatus.setText(order.getStatus());
            textOrderTotal.setText(String.format(Locale.US, "$%.2f", order.getTotal()));
        }
    }

    public interface OrderClickListener {
        void onOrderClick(Order order);
    }
}