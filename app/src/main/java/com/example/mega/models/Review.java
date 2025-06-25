package com.example.mega.models;

public class Review {
    private String userName;
    private float rating;
    private String text;
    private String date;

    public Review(String userName, float rating, String text, String date) {
        this.userName = userName;
        this.rating = rating;
        this.text = text;
        this.date = date;
    }

    public String getUserName() { return userName; }
    public float getRating() { return rating; }
    public String getText() { return text; }
    public String getDate() { return date; }
}
