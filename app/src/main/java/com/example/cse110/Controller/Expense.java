package com.example.cse110.Controller;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.cse110.Model.FormattingTool;

/**
 * An Expense object that stores information for an expense logged by the user.
 * Author: Peter Gonzalez
 */
public class Expense implements Parcelable {


    public static final int DECEMBER = 11;
    /**
     * Private variables for attrivbutes of an expense object;
     */
    private String name;
    private int cost;
    private int year, month, day;
    private int id;


    // parentCategoryName should not be saved in the database
    private String parentCategoryName;
    private String costString;

    //Formatting tool to avoid redundancy
    private FormattingTool formattingTool = new FormattingTool();

    /**
     * Constructor for the Expense object
     *
     * @param id                 - unique identification for the Expense object
     * @param name               - name of the expense
     * @param cost               - cost of the expense
     * @param year               - year the expense was logged
     * @param month              - month the expense was logged
     * @param day                - day the expense was logged
     * @param parentCategoryName - the category that the expense belongs to
     * @return An expense object
     */
    public Expense(int id, String name, double cost, int year, int month, int day, String parentCategoryName) {
        this.id = id;
        this.name = name;
        costString = Double.toString(cost);

        // Throws a NullPointerException if the input names are null or the month is invalid
        if (name.equals(null) || costString.equals(null) || month <= 0 || month > DECEMBER) {
            throw new NullPointerException("Name and cost must not be null. Month must " +
                    "be greater than or equal to 0.");
        }


        this.costString = formattingTool.formatDecimal(costString);
        this.cost = (int) (100 * Double.parseDouble(costString));
        this.month = month;
        this.year = year;
        this.day = day;
        this.parentCategoryName = parentCategoryName;
    }

    /**
     * Constructor for an Expense object that reads in serialized Expense data
     * and converts it into a usable object
     *
     * @param in - parcel data to convert
     */
    protected Expense(Parcel in) {
        name = in.readString();
        cost = in.readInt();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        id = in.readInt();
        parentCategoryName = in.readString();
        costString = in.readString();
    }


    /**
     * Read information in from the parcel
     */
    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };


    /**
     * Getter for name of the category
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * The cost of the expense, represented as cents.
     *
     * @return The int represent the original expense cost * 100
     */
    public int getCost() {
        return cost;
    }

    /**
     * Getter for the year the expense was logged.
     *
     * @return The year as an int
     */
    public int getYear() {
        return year;
    }

    /**
     * Getter for the month the expense was logged.
     *
     * @return The month as an int, starting at index 0
     */
    public int getMonth() {
        return month;
    }

    /**
     * The getter for the day the expense was logged
     *
     * @return The day as an int, starting at index 1
     */
    public int getDay() {
        return day;
    }

    /**
     * Unique id to this expense
     *
     * @return The unique to id to the expense, help's deletion.
     */
    public int getId() {
        return id;
    }

    /**
     * Return the formatted cost of the expense.
     *
     * @return A string with corrct decimal placement
     */
    public String getCostAsString() {
        return costString;
    }

    /**
     * Set a unique id for the expense.
     *
     * @param id The id to assign to the expense.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The name of the expense, not unique.
     *
     * @param name The name to assign to the expense
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter to assign a cost value to the expense.
     *
     * @param cost The cost of the expense to assign.
     */
    public void setCost(int cost) {
        this.cost = cost;

    }

    /**
     * Reassigns to which this expense belongs to.
     *
     * @param parentCategoryName The category to which this expense belongs to.
     */
    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    /**
     * Neccesary for all parcelable classes
     *
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write to the parcel, the information to serialize.
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(cost);
        parcel.writeInt(year);
        parcel.writeInt(month);
        parcel.writeInt(day);
        parcel.writeInt(id);
        parcel.writeString(parentCategoryName);
        parcel.writeString(costString);
    }
}
