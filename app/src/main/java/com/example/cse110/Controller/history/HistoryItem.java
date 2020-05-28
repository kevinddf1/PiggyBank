package com.example.cse110.Controller.history;

public class HistoryItem {
    /**
     * The variable to hold the month and year to display, separated by 1 space character
     */
    private String monthYear;

    /**
     * The variable to hold the budget to display
     */
    private String totalBudget;

    /**
     * The variable to hold the expenses to display
     */
    private String totalExpenses;

    /**
     * The only constructor.
     * @param monthYear month year display
     * @param totalBudget budget display
     * @param totalExpenses expenses display
     */
    public HistoryItem(String monthYear, String totalBudget, String totalExpenses){
        this.monthYear = monthYear;
        this.totalBudget = totalBudget;
        this.totalExpenses = totalExpenses;
    }

    /**
     * Getter for month and year display
     * @return
     */
    public String getMonthYear(){
        return monthYear;
    }

    /**
     * Getter for budget display
     * @return
     */
    public String getTotalBudget(){
        return totalBudget;
    }

    /**
     * Getter for expenses display
     */
    public String getTotalExpenses(){
        return totalExpenses;
    }

}
