package com.example.cse110.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MonthlyData implements Parcelable {
    // Month and year should never be modified outside the constructor
    private int month;
    private int year;
    private  long totalBudget, totalExpensesAsCents = 0;
    private Map<String, Category> categories;
    // This is not serialized but is repopulated from categories so that categories and categoriesArrayList refer to the same Category objects
    private ArrayList<Category> categoriesArrayList;
    // create a Database object
    private Database base = Database.Database();

    /*
    Constructor for an empty MonthlyData.
    */
    public MonthlyData(int month, int year) {
        this.month = month;
        this.year = year;
        categories = new HashMap<>();
        categoriesArrayList = new ArrayList<>();

        //Update monthly data
        setTotalBudget();
        setTotalExpensesAsCents();
    }

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
        // TODO
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(month);
        parcel.writeInt(year);
        parcel.writeMap(categories);
    }


    public int getYear() {
        return year;
    }

    public Category getCategory(String name) {
        // TODO: error handling?
        return categories.get(name);
    }
    // Getters
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

    /*
    /Return true if category was successfully made, false otherwise
     */
    public boolean createCategory(String name, int budget) {
        //Check that no category exists with the same name
        if(!categories.containsKey(name)) {
            Category category = new Category(month, year);
            category.setName(name);
            category.setBudget(budget);
            categories.put(name, category);
            categoriesArrayList.add(category);
            /****** update the new category info to database ******/
            this.base.insertCategoryName(name);
            this.base.insertCategoryBudget(budget, name);
            this.base.insertCategoryDate(month, year, name);
            /*************************************************/
            //Update total budget
            this.totalBudget += category.getBudget();
            return true;
        }
        return false;
    }

    // This function is used to create a category from database data
    public Category createExistCategory(String name, int budget, ArrayList<Expense> expenses, int Month, int Year) {
        Category category = new Category(budget, name, expenses, Month, Year);
        category.setName(name);
        category.setBudget(budget);
        categories.put(name, category);
        categoriesArrayList.add(category);
        this.totalBudget += category.getBudget();
        this.setTotalExpensesAsCents();
        return category;
    }

    public void deleteCategory(String name) {
        //Update total budget
        this.totalBudget -= categories.get(name).getBudget();
        categories.remove(name);
        for (int i = 0; i < categoriesArrayList.size(); i++) {
            if (categoriesArrayList.get(i).getName().equals(name)) {
                categoriesArrayList.remove(i);
                break;
            }
        }
        base.delete_cate(name); //delete category from database
    }

    /**
     * Calculates the total amount budgeted for this month, across all categories.
     * Very expensive function so limit use as much as possible.
     */
    public void setTotalBudget() {
        this.totalBudget = 0;
        // Possible bug: Exceeding a float's value
        //Loop through all categories and add values
        for(Category category : categoriesArrayList) {
            this.totalBudget += category.getBudget();
        }


    }

    /**
     * Goes through all expenses and adds them to total expenses for the month.
     * Calculates all expenses so avoid using too much
     */
    public void setTotalExpensesAsCents(){
        this.totalExpensesAsCents = 0;

        //Loop through all categories a
        for (Category category: categoriesArrayList){
                this.totalExpensesAsCents += category.getTotalExpenses();

        }
    }

    /**
     * Getter for total expenses as cents
     * @return
     */
    public long getTotalExpensesAsCents(){
        setTotalExpensesAsCents();
        return  this.totalExpensesAsCents;
    }

    public ArrayList<Category> getCategoriesAsArray() {
        return categoriesArrayList;
    }

    /**
     * Getter for total budget
     */
    public long getTotalBudget(){
        setTotalBudget();
        return totalBudget;
    }


}
