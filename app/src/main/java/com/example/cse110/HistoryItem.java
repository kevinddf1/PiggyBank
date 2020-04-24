package com.example.cse110;

/**
 * Wrapper class to display data in History page's list.
 * @author Peter Gonzalez
 * @version 4.23
 */
public class HistoryItem {

    //Declare our local variables, passed into constructor

    /**
     * Hold's the name of the Category this item displays.
     */
    private String name;

    /**
     * Hold's the budget of the Category this item displays.
     */
    private int budget;

    /**
     * Hold's the total value of expenses in the Category this item displays.
     */
    private  double totalExpenses;

    //Constructor

    /**
     * The only constructor for a HistoryItem. Hold's all information to display for one Category.
     * @param name The name of the Category.
     * @param budget The budget of the Category.
     * @param totalExpenses The sum of the total value of expenses for one Category.
     */
    public HistoryItem(String name, int budget, double totalExpenses){
        this.name = name;
        this.budget = budget;
        this.totalExpenses = totalExpenses;
    }

    //Getters


    /**
     * Get the value of the total expenses.
     * @return The value this item is holding for the Category's expenses.
     */
    public double getTotalExpenses() {
        return totalExpenses;
    }

    /**
     * Get the value of the Category's budget.
     * @return The value this item is holding for the Category's budget.
     */
    public int getBudget() {
        return budget;
    }

    /**
     * Get the name of the Category this item displays.
     * @return The value this item is holding for the Category's budget.
     */
    public String getName(){
        return name;
    }
}
