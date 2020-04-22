package com.example.cse110;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A Category should be instantiated only by MonthlyData.
 */
public class Category implements Parcelable {
    private int budget;
    private String name;
    private ArrayList<Expense> expenses;
    private int nextExpenseId;

    /*
    Constructor for an empty Category.
    */
    public Category() {
        nextExpenseId = 0;
        budget = 0;
        name = "";
        expenses = new ArrayList<Expense>();
    }

    /*
    Constructor for a Category loaded from the database.
    */
    public Category(int budget, String name, ArrayList<Expense> expenses) {
        // TODO: set nextExpenseId to be the (max of all IDs in expenses) + 1
    }

    protected Category(Parcel in) {
        budget = in.readInt();
        name = in.readString();
        expenses = in.readArrayList(Expense.class.getClassLoader());
        nextExpenseId = in.readInt();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public Expense createExpense(String name, int cost, int year, int month, int day) {
        Expense expense = new Expense(nextExpenseId++);
        expense.setYear(year);
        expense.setMonth(month);
        expense.setDay(day);
        expense.setName(name);
        expense.setCost(cost);
        // TODO: insert while keeping sorted order
        expenses.add(expense);
        return expense;
    }

    public void deleteExpense(int id) {
        // TODO: optimized search
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId() == id) {
                expenses.remove(i);
                break;
            }
        }
    }

    /**
     * Gets a list of Expenses sorted decreasing by the expenses' date.
     */
    public final ArrayList<Expense> getExpenses() {
        // TODO: sorted by date
        return expenses;
    }

    public String getName() {
        return name;
    }

    public String getBudgetAsString() {
        // TODO
        return Integer.toString(budget);
    }

    public int getBudget() {
        return budget;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(budget);
        parcel.writeString(name);
        parcel.writeList(expenses);
        parcel.writeInt(nextExpenseId);
    }
}
