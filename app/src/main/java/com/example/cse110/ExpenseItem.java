package com.example.cse110;

import java.util.ArrayList;

class ExpenseItem {

    //declare private data instead of public to ensure the privacy of data field of each class
    private String  name;
    private int budget;


    //Constructor
    public ExpenseItem(String name, int budget) {
        this.name = name;
        this.budget = budget;

    }

    public String getCategory(){
        return name;

    }
    public int getBudget(){
        return budget;
    }

}