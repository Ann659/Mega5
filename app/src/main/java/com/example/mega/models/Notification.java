package com.example.mega.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

public class Notification {
    private int id;
    private String title;
    private String message;
    private long timestamp;
    private int iconRes;
    private boolean isRead;
    private int orderId;

    public Notification(int id, String title, String message, long timestamp,
                        int iconRes, boolean isRead, int orderId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.iconRes = iconRes;
        this.isRead = isRead;
        this.orderId = orderId;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public int getIconRes() { return iconRes; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public int getOrderId() { return orderId; }
    public boolean hasOrder() { return orderId != -1; }

    public static int generateId() {
        return new Random().nextInt(100000);
    }

    public Object getType() {
        return null;
    }
}