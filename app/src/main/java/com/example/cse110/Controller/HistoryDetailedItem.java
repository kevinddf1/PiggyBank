package com.example.cse110.Controller;

public class HistoryDetailedItem {

    /**
     * The two items that need to be displayed to the item in the list.
     */
    String name, cost;

    /**
     * The only constructor to assign the name and cost of the value.
     * @param name
     * @param cost
     */
    public HistoryDetailedItem(String name, String cost){
        this.name = name;
        this.cost = cost;


    }

    //Getters
    public String getCost(){
        return cost;
    }

    public String getName() {
        return name;
    }
}
