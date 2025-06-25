package com.example.mega.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.mega.CartItem;
import com.example.mega.OrderItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Order implements Parcelable {
    private String id;
    private long date;
    private String status;
    private List<OrderItem> items;
    private double total;

    public Order(List<CartItem> cartItems, double totalPrice) {
        this.id = UUID.randomUUID().toString();
        this.date = System.currentTimeMillis();
        this.status = "processing";
        this.total = totalPrice;
        this.items = convertCartItemsToOrderItems(cartItems);
    }

    public Order(String id, long date, String status, double total, List<OrderItem> items) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.total = total;
        this.items = items;
    }

    protected Order(Parcel in) {
        id = in.readString();
        date = in.readLong();
        status = in.readString();
        total = in.readDouble();
        items = in.createTypedArrayList(OrderItem.CREATOR);
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    private List<OrderItem> convertCartItemsToOrderItems(List<CartItem> cartItems) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            orderItems.add(new OrderItem(
                    cartItem.getProduct().getId(),
                    cartItem.getProduct().getName(),
                    (int) cartItem.getProduct().getPrice(),
                    cartItem.getQuantity()
            ));
        }
        return orderItems;
    }

    public int getId() { return Integer.parseInt(id); }
    public long getDate() { return date; }
    public String getStatus() { return status; }
    public List<OrderItem> getItems() { return items; }
    public double getTotal() { return total; }

    public void setStatus(String status) { this.status = status; }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        return sdf.format(new Date(date));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(date);
        dest.writeString(status);
        dest.writeDouble(total);
        dest.writeTypedList(items);
    }
}