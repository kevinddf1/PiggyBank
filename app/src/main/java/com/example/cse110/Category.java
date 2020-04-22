package com.example.cse110;

import java.util.ArrayList;
import java.util.Date;

public class Category {
    private int budget;
    private String name;
    // This must be always be kept sorted by the expense's date
    private ArrayList<Expense> expenses;
    private int nextExpenseId;

    /*
    Constructor for an empty Category.
    */
    public Category() {
        nextExpenseId = 0;
        budget = 0;
        name = "";
    }

    /*
    Constructor for a Category loaded from the database.
    */
    public Category(int budget, String name, ArrayList<Expense> expenses) {
        // TODO: set nextExpenseId to be the (max of all IDs in expenses) + 1
    }

    public void addExpense(String name, int cost, String category, Date date) {
        // TODO
    }

    public void removeExpense(int id) {
        // TODO
    }

    public Expense getExpense(int id) {
        // TODO
        return new Expense();
    }
}
