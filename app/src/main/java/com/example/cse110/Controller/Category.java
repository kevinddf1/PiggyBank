package com.example.cse110.Controller;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.cse110.Model.Database;

import java.util.ArrayList;

/**
 * A Category item which holds user's expenses. (Backend object)
 *
 * @author Peter Gonzalez and Thuycam Nguyen
 */
public class Category implements Parcelable {

    /**
     * Instance variable for a class
     */
    private final int month;
    private final int year;
    private int budget;
    private String name;
    private long totalExpenses = 0; // Start at 0


    /**
     * Primary data structure for all expenses associated w/ a category
     */
    private final ArrayList<Expense> expenses;
    private int nextExpenseId;

    /**
     * Our database singleton
     */
    private final Database base = Database.Database(); // create a Database object

    /**
     * Constructor for an empty Category.
     */
    public Category(int month, int year) {

        //Set global scope
        this.month = month;
        this.year = year;
        nextExpenseId = 0;
        budget = 0;
        name = "";
        expenses = new ArrayList<>();
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

        //Identify id
        for (Expense e : expenses) {
            nextExpenseId = Math.max(nextExpenseId, e.getId());
        }
    }

    /**
     * Uses parcel deserialize to read in a particular category.
     * @param in The incoming parcel to deserialize.
     */
    @SuppressWarnings("unchecked")
    private Category(Parcel in) {
        budget = in.readInt();
        name = in.readString();
        expenses = in.readArrayList(Expense.class.getClassLoader());
        nextExpenseId = in.readInt();
        month = in.readInt();
        year = in.readInt();
        totalExpenses = in.readLong();

        //this.base = new Database();
    }

    //THUYCAM
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

    /**
     * Creates a new Expense object to associate w/ the Category
     * @param name The name of the expense
     * @param cost The price of the expense(not as cents)
     * @param year The year the item as added
     * @param month The month the item was added
     * @param day The day the item was added
     * @return
     */
    public Expense createExpense(String name, double cost, int year, int month, int day) {

        //Create a new backend object
        Expense expense = new Expense(nextExpenseId++, name, cost, year, month, day, this.name);
        expenses.add(expense);

        //Update the database
        this.base.insertExpense(expense.getCost(), name, this.name, year, month, day, nextExpenseId); // update category to database

        //Update total expenses so far
        this.totalExpenses = totalExpenses + expense.getCost();
        return expense;
    }

    /**
     * Delete an expense from the category.
     *
     * @param id The id of the expense to remove from this category.
     */
    public void deleteExpense(int id) {
        base.delete_exp(name, id, year, month); // delete expense from database

        /**
         * Delete search for the specific expense in the Category.
         */
       for (int i = 0; i < expenses.size(); i++) {

           //Search for matching ids
            if (expenses.get(i).getId() == id) {
                this.totalExpenses = totalExpenses - expenses.get(i).getCost();

                base.delete_exp(name, id, year, month); // delete expense from database

                //Remove from local object
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

    /**
     * Getter for the category name.
     * @return name The name of the category
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the budget of the category as a String
     * @return The budget of the Category as a string
     */
    public String getBudgetAsString() {
        return Integer.toString(budget);
    }


    /**
     * Getter for the budget of the category.
     * @return The budget of the category as an int
     */
    public int getBudget() {
        return budget;
    }

    /**
     * Setter for the name of the category
     * @param name
     */
    public void setName(String name) {
        this.name = name;

        //Adjust the name of the category among all the expenses
        for (Expense e : expenses) {
            e.setParentCategoryName(name);
        }
    }

    /**
     * Setter for the budget of the category, updates database.
     * @param budget The budget for the category.
     */
    public void setBudget(int budget) {
        //Update local object
        this.budget = budget;

        //Update the database
        base.insertCategoryBudget(budget, this.getName(), year, month);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write Category object to a parcel to pass data between classes.
     * @param parcel T
     * @param i
     */
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

    /**
     * Getter for total expenses, local object.
     * @return
     */
    public long getTotalExpenses() {
        return this.totalExpenses;
    }

    /**
     * Calculates totalExpenses for this object. Is a costly operation as it is O(n).
     */
    public void setTotalExpenses() {
        for (Expense expense : this.expenses) {
            this.totalExpenses += expense.getCost();
        }

    }
}
