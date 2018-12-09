package com.kerimovscreations.billsplitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ShoppingItem extends RealmObject implements Serializable {

    @SerializedName("Id")
    @Expose
    private int id;

    @SerializedName("IsComplete")
    @Expose
    private boolean isComplete;

    @SerializedName("Price")
    @Expose
    private float price;

    @SerializedName("Product")
    @Expose
    private Product product;

    @SerializedName("CreatedAt")
    @Expose
    private String date;

    @SerializedName("Buyer")
    @Expose
    private Person buyer;

    @SerializedName("PurchaseMembers")
    @Expose
    private RealmList<Person> sharedMembers = new RealmList<>();

    public ShoppingItem() {

    }

    private boolean isHeader = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public RealmList<Person> getSharedMembers() {
        return sharedMembers;
    }

    public void setSharedMembers(RealmList<Person> sharedMembers) {
        this.sharedMembers = sharedMembers;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void toggleComplete() {
        this.isComplete = !this.isComplete;
    }

    public Person getBuyer() {
        return buyer;
    }

    public void setBuyer(Person buyer) {
        this.buyer = buyer;
    }
}
