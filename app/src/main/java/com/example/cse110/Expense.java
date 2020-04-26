package com.example.cse110;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

/**
 * An Expense should be instantiated only by Category.
 */
public class Expense implements Parcelable {
    private String name;
    private int cost;
    private int year, month, day;
    private int id;
    // parentCategoryName should not be saved in the database
    private String parentCategoryName;

    //private Database base; // create a Database object

    public Expense(int id, String name, int cost, int year, int month, int day, String parentCategoryName) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.year = year;
        this.month = month;
        this.day = day;
        this.parentCategoryName = parentCategoryName;

//        this.base = new Database();
//        base.insertExpenseName(name);
//        base.insertExpenseCost(cost);
    }


    protected Expense(Parcel in) {
        name = in.readString();
        cost = in.readInt();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        id = in.readInt();
        parentCategoryName = in.readString();
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

//    /**
//     * Update the database to reflect changes in Expense's fields.
//     * This is called every time any field in Expense is modified.
//     */
//    public void updateToDatabase() {
//        // String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        // TODO: uid uniquely identifies the user; use it to update the database; Expense.id uniquely identifies the Expense within the Category
//        // use uid, id, and parentCategoryName
//    }

    public String getName() {
        return name;
    }
    public int getCost() {
        return cost;
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
        //updateToDatabase();
        //base.insertExpenseName(name);
    }
    public void setCost(int cost) {
        this.cost = cost;
        //updateToDatabase();
        //base.insertExpenseCost(cost);
    }
    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        //updateToDatabase();
    }
    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(cost);
        parcel.writeInt(year);
        parcel.writeInt(month);
        parcel.writeInt(day);
        parcel.writeInt(id);
        parcel.writeString(parentCategoryName);
    }
}
