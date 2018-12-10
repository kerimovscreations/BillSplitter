package com.kerimovscreations.billsplitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

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

    @SerializedName("Date")
    @Expose
    private String date;

    @SerializedName("Buyer")
    @Expose
    private GroupMember buyer;

    @SerializedName("BarCode")
    @Expose
    private String barCode;

    @SerializedName("PurchaseMembers")
    @Expose
    private RealmList<GroupMember> sharedMembers = new RealmList<>();

    public ShoppingItem() {

    }

    public ShoppingItem(ShoppingItem item) {
        this.id = item.id;
        this.isComplete = item.isComplete;
        this.price = item.price;
        this.product = item.product;
        this.date = item.date;
        this.buyer = item.buyer;
        this.sharedMembers = item.sharedMembers;
        this.isHeader = item.isHeader;
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

    public RealmList<GroupMember> getSharedMembers() {
        return sharedMembers;
    }

    public void setSharedMembers(RealmList<GroupMember> sharedMembers) {
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

    public GroupMember getBuyer() {
        return buyer;
    }

    public void setBuyer(GroupMember buyer) {
        this.buyer = buyer;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
