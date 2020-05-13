package com.example.cse110;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * A Category should be instantiated only by MonthlyData.
 */
public class Category implements Parcelable {
    private int month, year;
    private int budget;
    private String name;
    private ArrayList<Expense> expenses;
    private int nextExpenseId;
    private Database base = Database.Database(); // create a Database object
    private long totalExpenses = 0;

    /**
     * Constructor for an empty Category.
     */
    public Category(int month, int year) {
        this.month = month;
        this.year = year;
        nextExpenseId = 0;
        budget = 0;
        name = "";
        expenses = new ArrayList<Expense>();

        //this.base = new Database();
    }

    /**
     * Constructor for a Category loaded from the database.
     * Only budget, name, and expenses should be stored in the database while
     * month and year are passed in from the parent MonthlyData's month and year.
     */
    public Category(int budget, String name, ArrayList<Expense> expenses, int month, int year) {
        this.budget = budget;
        this.name = name;
        this.expenses = expenses;
        this.month = month;
        this.year = year;
        nextExpenseId = 0;
        for (Expense e : expenses) {
            nextExpenseId = Math.max(nextExpenseId, e.getId());
        }
        //nextExpenseId++;

        //this.base = new Database();
    }

    protected Category(Parcel in) {
        budget = in.readInt();
        name = in.readString();
        expenses = in.readArrayList(Expense.class.getClassLoader());
        nextExpenseId = in.readInt();
        month = in.readInt();
        year = in.readInt();
        totalExpenses = in.readLong();

        //this.base = new Database();
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

    public Expense createExpense(String name, double cost, int year, int month, int day) {
        Expense expense = new Expense(nextExpenseId++, name, cost, year, month, day, this.name);
        // TODO: insert while keeping sorted order
        expenses.add(expense);
        this.base.insertExpense(expense.getCost(), name, this.name, year, month, day, nextExpenseId); // update category to database

        //Update total expenses so far
        this.totalExpenses = totalExpenses + expense.getCost();
        return expense;
    }

    public void deleteExpense(int id) {
        base.delete_exp(name, id, year, month); // delete expense from database
        // TODO: optimized search
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId() == id) {
                this.totalExpenses = totalExpenses - expenses.get(i).getCost();
                base.delete_exp(name, id, year, month); // delete expense from database
                expenses.remove(i);

//                for (int k = i; k < expenses.size(); k++) {
//                    //expenses.get(k).setId(expenses.get(k).getId() - 1);
//                    expenses.get(k).setId(k+1);
//                    //base.insertExpenseId(expenses.get(k).getName(), name, k+1);
//                }
//
//                nextExpenseId = expenses.size();

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
        for (Expense e : expenses) {
            e.setParentCategoryName(name);
        }
    }

    public void setBudget(int budget) {
        this.budget = budget;

        base.insertCategoryBudget(budget, this.getName(), year, month);
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
        parcel.writeInt(month);
        parcel.writeInt(year);
        parcel.writeLong(totalExpenses);
    }

    //Getter for total expenses
    public long getTotalExpenses(){
        return this.totalExpenses;
    }

    //Loop through all expenses to get total value
    public void setTotalExpenses(){
        for(Expense expense : this.expenses){
            this.totalExpenses += expense.getCost();
        }

    }
}
