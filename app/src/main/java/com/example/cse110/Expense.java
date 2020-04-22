package com.example.cse110;

import java.util.Date;

public class Expense {
    private String name;
    private int cost;
    private String category;
    private Date date;
    private int id;

    public String getName() {
        return name;
    }
    public int getCost() {
        return cost;
    }
    public String getCategory() {
        return category;
    }
    public Date getDate() {
        return date;
    }
    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setDate(Date date) {
        this.date = date;
    }
}
