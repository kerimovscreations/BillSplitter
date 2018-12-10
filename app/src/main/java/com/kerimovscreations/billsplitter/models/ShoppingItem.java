package com.kerimovscreations.billsplitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ShoppingItem extends RealmObject implements Serializable {

    @PrimaryKey
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

    @SerializedName("PaidByUser")
    @Expose
    private Person buyer;

    @SerializedName("PurchaseMembers")
    @Expose
    private RealmList<GroupMember> sharedMembers = new RealmList<>();

    private int groupId;

    public ShoppingItem() {

    }

    public ShoppingItem(int id, String date) {
        this.id = id;
        this.date = date;
        this.isHeader = true;
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
        this.groupId = item.groupId;
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

    public Person getBuyer() {
        return buyer;
    }

    public void setBuyer(Person buyer) {
        this.buyer = buyer;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
