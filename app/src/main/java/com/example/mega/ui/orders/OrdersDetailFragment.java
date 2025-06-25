package com.example.mega.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mega.CartItem;
import com.example.mega.OrderProductsAdapter;
import com.example.mega.R;
import com.example.mega.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrdersDetailFragment extends Fragment {
    private Order currentOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);

        if (getArguments() != null) {
            currentOrder = (Order) getArguments().getSerializable("order");
        }

        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        ImageView backButton = view.findViewById(R.id.back_button);
        TextView orderNumber = view.findViewById(R.id.order_number);
        TextView orderDate = view.findViewById(R.id.order_date);
        TextView orderStatus = view.findViewById(R.id.order_status);
        TextView orderTotal = view.findViewById(R.id.order_total);
        RecyclerView productsRecycler = view.findViewById(R.id.products_recycler);

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        if (currentOrder != null) {
            orderNumber.setText(String.format("Заказ #%s", currentOrder.getId()));
            orderDate.setText(currentOrder.getFormattedDate());
            orderStatus.setText(currentOrder.getStatus());
            orderTotal.setText(String.format(Locale.getDefault(), "%.0f руб", currentOrder.getTotal()));

            OrderProductsAdapter adapter = new OrderProductsAdapter(currentOrder.getItems());
            productsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            productsRecycler.setAdapter(adapter);
        }
    }
}