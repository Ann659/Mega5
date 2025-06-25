package com.example.mega;

import com.example.mega.models.Product;

import java.io.Serializable;

public class CartItem implements Serializable {
    private Product product;
    private int quantity;
    private int color;

    public CartItem() {
        this.product = product;
        this.quantity = quantity;
        this.color = color;
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
