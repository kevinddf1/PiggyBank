package com.example.cse110.Controller;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.cse110.Model.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MonthlyData implements Parcelable {
    // Month and year should never be modified outside the constructor
    private int month;
    private int year;
    private long totalBudget, totalExpensesAsCents = 0;
    private Map<String, Category> categories;
    // This is not serialized but is repopulated from categories so that categories and categoriesArrayList refer to the same Category objects
    private ArrayList<Category> categoriesArrayList;
    // create a Database object
    private Database base = Database.Database();

    /**
     * Constructor for an empty MonthlyData.
     */
    public MonthlyData(int month, int year) {
        this.month = month;
        this.year = year;
        categories = new HashMap<>();
        categoriesArrayList = new ArrayList<>();

        //Update monthly data
        calculateTotalBudget();
        calculateTotalExpensesAsCents();

    }

    /**
     * Constructor for a MonthlyData from a Parcel.
     */
    protected MonthlyData(Parcel in) {
        month = in.readInt();
        year = in.readInt();
        categories = new HashMap<String, Category>();
        in.readMap(categories, Category.class.getClassLoader());
        categoriesArrayList = new ArrayList<>();
        for (Category category : categories.values()) {
            categoriesArrayList.add(category);
        }
    }

    public static final Creator<MonthlyData> CREATOR = new Creator<MonthlyData>() {
        @Override
        public MonthlyData createFromParcel(Parcel in) {
            return new MonthlyData(in);
        }

        @Override
        public MonthlyData[] newArray(int size) {
            return new MonthlyData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(month);
        parcel.writeInt(year);
        parcel.writeMap(categories);
    }

    /**
     * Getter for current year as an int.
     */
    public int getYear() {
        return year;
    }

    /**
     * Getter for Category by its name.
     */
    public Category getCategory(String name) {
        return categories.get(name);
    }

    /**
     * Getter for current month as an int.
     */
    public int getIntMonth(){
        return month;
    }

    /**
     * Getter for current month as a string.
     */
    public String getMonth(){
        switch (month){
            case 0 :
                return "January";

            case 1 :
                return "February";

            case 2:
                return "March";

            case 3:
                return "April";

            case 4:
                return "May";

            case 5:
                return "June";

            case 6:
                return "July";

            case 7:
                return "August";

            case 8:
                return "September";

            case 9:
                return "October";

            case 10:
                return "November";

            case 11:
                return "December";

            default:
                throw new IllegalStateException("Unexpected value: " + month);
        }
    };

    // Check if a "new" name already exists in the current monthly category list
    public boolean checkNameExists(String name) {
        return categories.containsKey(name);
    }

    // Rename a category (remove then old pair and insert a new one with a new key "name")
    public boolean renameCategory(String oldName, String newName) {
        if(checkNameExists(oldName)) {
            categories.put(newName, categories.remove(oldName));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return true if category was successfully made, false otherwise
     */
    public boolean createCategory(String name, int budget) {
        // Check that no category exists with the same name
        if(!categories.containsKey(name)) {
            Category category = new Category(month, year);
            category.setName(name);
            category.setBudget(budget);
            categories.put(name, category);
            categoriesArrayList.add(category);

            // Update the database with the new Category
            this.base.insertCategoryName(name, year, month);
            this.base.insertCategoryBudget(budget, name, year, month);
            this.base.insertCategoryExpenses(totalExpensesAsCents, name, year, month);
            this.base.insertCategoryDate(year, month, name);

            // Update total budget
            this.totalBudget += category.getBudget();
            return true;
        }
        return false;
    }

    /**
     * Creates a Category from existing data and return the new Category.
     */
    public Category createExistCategory(String name, int budget, ArrayList<Expense> expenses, int Month, int Year) {
        Category category = new Category(budget, name, expenses, Month, Year);
        category.setName(name);
        category.setBudget(budget);
        categories.put(name, category);
        categoriesArrayList.add(category);
        this.totalBudget += category.getBudget();
        this.calculateTotalExpensesAsCents();
        return category;
    }

    /**
     * Deletes a Category with some name.
     */
    public void deleteCategory(String name) {
        // Update total budget
        this.totalBudget -= categories.get(name).getBudget();
        categories.remove(name);
        for (int i = 0; i < categoriesArrayList.size(); i++) {
            if (categoriesArrayList.get(i).getName().equals(name)) {
                categoriesArrayList.remove(i);
                break;
            }
        }
        // Delete category from database
        base.delete_cate(name, year, month);
    }

    /**
<<<<<<< HEAD
     * Iterates through all Categories and sums up the total budget.
=======
<<<<<<< HEAD:app/src/wip/java/com/example/cse110/Model/MonthlyData.java
     * Calculates the total amount budgeted for this month, across all categories.
     * Very expensive function so limit use as much as possible.
>>>>>>> version_p
     */
    public void calculateTotalBudget() {
        this.totalBudget = 0;

        // Loop through all categories and add budget
        for(Category category : categoriesArrayList) {
            this.totalBudget += category.getBudget();
        }
    }

    /**
     * Iterates through all Categories and calculates this object's totalExpensesAsCents.
     */
    public void calculateTotalExpensesAsCents(){
        this.totalExpensesAsCents = 0;

        // Loop through all categories and add up expenses
        for (Category category: categoriesArrayList){
            this.totalExpensesAsCents += category.getTotalExpenses();
        }
    }

    /**
     * Getter for total expenses as cents
     */
    public long getTotalExpensesAsCents(){
        calculateTotalExpensesAsCents();
        return this.totalExpensesAsCents;
    }

    /**
     * Getter for all Categories as an ArrayList.
     */
    public ArrayList<Category> getCategoriesAsArray() {
        return categoriesArrayList;
    }

    /**
     * Getter for total budget
     */
    public long getTotalBudget(){
        calculateTotalBudget();
        return totalBudget;
    }
    public void setTotalBudgetDatabase(String budget){
        this.totalBudget = Long.parseLong(budget);

    }

    public void setTotalExpensesDatabase(String expenses){
        this.totalExpensesAsCents = Long.parseLong(expenses);
    }

}
