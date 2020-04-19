package com.example.cse110;

import java.util.ArrayList;

class ExpenseItem {

    //declare private data instead of public to ensure the privacy of data field of each class
    private String cost, name, category;
    private double costValue;
    private int month, position;
    private static final int  DECIMAL_PLACES = 2;


    //Constructor
    public ExpenseItem(String cost, String name, int month) {

        // Add formatting for whole numbers
        if(cost.indexOf('.') == -1){
            cost = cost.concat(".00");
        }else{
            //Ensure only valid input
            cost = cost.substring(0, cost.indexOf(".") + DECIMAL_PLACES + 1 );
        }

        //Store value of cost
        this.costValue = Double.parseDouble(cost);

        //Format display of costs and name
        this.cost = "-$ "+ cost + " ";
        this.name = name;
        this.month = month;
    }

    //retrieve Expense's Cost
    public String getExpenseCost(){
        return cost;
    }

    //Sets Expense's Cost
    public void setExpenseCost(double value){
        this.cost = "-$ "+ value + " ";

    }

    //Retrieve Expense's Value
    public double getExpenseValue(){
        return costValue;
    }


    //Set Expense's Value
    public void setExpenseValue(double value){
        this.costValue = value;
    }

    //retrieve Expense's Name
    public String getExpenseName(){
        return name;
    }

    //Set position in the array
    public void setPosition(int pos){
        this.position = pos;
    }

    //Get position in the array
    public int getPosition(){
        return this.position;
    }

    //Edit Category of the item
    public void setCategory(String category){
        if(category != null) {
            this.category = category;
        }

        // Update Category with other classes

    }

    // Retrieve Category of the item
    public String getCategory(){
        return category;
    }

    public int getMonth(){
        return month;
    }
    // Return an empty ArrayList of ExpenseItem objects
    public static ArrayList<ExpenseItem> getItems() {
        return new ArrayList<>();
    }
}