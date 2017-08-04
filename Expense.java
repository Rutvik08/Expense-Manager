package com.expensemanager.entities;


public class Expense  {

    private String id;
    private String description;
    private String date;
    private String category;
    private float total;
    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", category='" + category + '\'' +
                ", total=" + total +
                ", userid='" + userid + '\'' +
                '}';
    }

    public Expense() {
    }

    public Expense(String id, String description, String date, String category, float total) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.category = category;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

}
