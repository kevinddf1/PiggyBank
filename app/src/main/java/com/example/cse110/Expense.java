package com.example.cse110;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * An Expense should be instantiated only by Category.
 */
public class Expense implements Parcelable {
    private String name;
    private int cost;
    private String category;
    private int year, month, day;
    private int id;

    public Expense(int id) {
        this.id = id;
    }

    protected Expense(Parcel in) {
        name = in.readString();
        cost = in.readInt();
        category = in.readString();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        id = in.readInt();
    }

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    public String getName() {
        return name;
    }
    public int getCost() {
        return cost;
    }
    public String getCategory() {
        return category;
    }
    public int getYear() {
        return year;
    }
    public int getMonth() {
        return month;
    }
    public int getDay() {
        return day;
    }
    public int getId() {
        return id;
    }
    public String getCostAsString() {
        // TODO
        return Integer.toString(cost);
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
    public void setYear(int year) {
        this.year = year;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(cost);
        parcel.writeString(category);
        parcel.writeInt(year);
        parcel.writeInt(month);
        parcel.writeInt(day);
        parcel.writeInt(id);
    }
}
