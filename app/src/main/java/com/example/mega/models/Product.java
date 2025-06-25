package com.example.mega.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Product implements Parcelable {
    private String productId;
    private String name;
    private String description;
    private double price;
    private int categoryId;
    private String brand;
    private float rating;
    private int reviewCount;
    private String imagePath;

    public Product(JSONObject json) throws JSONException {
        this.productId = json.getString("product_id");
        this.name = json.getString("name");
        this.description = json.optString("description", "");
        this.price = json.getDouble("price");
        this.categoryId = json.getInt("category_id");
        this.brand = json.optString("brand", "");
        this.rating = (float) json.optDouble("rating", 0);
        this.reviewCount = json.optInt("review_count", 0);
        this.imagePath = json.optString("image_path", "");
    }

    public int getId() { return Integer.parseInt(productId); }
    public void setId(String id) { this.productId = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public float getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getImagePath() {
        return Integer.parseInt(imagePath);
    }

    public Product() {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.brand = brand;
        this.reviewCount = reviewCount;
        this.rating = rating;
        this.imagePath = imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    protected Product(Parcel in) {
        productId = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        categoryId = in.readInt();
        brand = in.readString();
        rating = (float) in.readDouble();
        reviewCount = in.readInt();
        imagePath = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeDouble(rating);
        dest.writeInt(reviewCount);
        dest.writeString(imagePath);
        dest.writeInt(categoryId);
        dest.writeString(brand);
    }


}