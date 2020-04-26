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

    private Database base; // create a Database object

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

        this.base = new Database();
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

        for (Expense e : expenses) {
            nextExpenseId = Math.max(nextExpenseId, e.getId());
        }
        nextExpenseId++;

        //this.base = new Database();
    }

    protected Category(Parcel in) {
        budget = in.readInt();
        name = in.readString();
        expenses = in.readArrayList(Expense.class.getClassLoader());
        nextExpenseId = in.readInt();
        month = in.readInt();
        year = in.readInt();
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


//    /**
//     * Update the database to reflect changes in Category's budget or name ONLY (not necessarily the expenses).
//     * This is called when any of the Category's properties is modified, and when an Expense is created or deleted.
//     */
//    public void updateToDatabase() {
//        // String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        // TODO: uid uniquely identifies the user; use it to update the database
//    }

    public Expense createExpense(String name, int cost, int year, int month, int day) {

        this.base.insertExpenseName(name);
        this.base.insertExpenseCost(cost);

        Expense expense = new Expense(nextExpenseId++, name, cost, year, month, day, this.name);
        // TODO: insert while keeping sorted order
        expenses.add(expense);
        //updateToDatabase();

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
        //updateToDatabase();
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
        //updateToDatabase();
        this.base.insertCategoryName(name); // update the new category to database
    }

    public void setBudget(int budget) {
        this.budget = budget;
        //updateToDatabase();
        this.base.insertCategoryBudget(budget); // update the new category to database
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
    }
}
