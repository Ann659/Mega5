package com.example.mega;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderItem implements Parcelable {
    private String productId;
    private String productName;
    private double price;
    private int quantity;

    public OrderItem(int productId, String productName, int quantity, double price) {
        this.productId = String.valueOf(productId);
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    protected OrderItem(Parcel in) {
        productId = in.readString();
        productName = in.readString();
        quantity = in.readInt();
        price = in.readDouble();
    }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeInt(quantity);
        dest.writeDouble(price);
    }
}
