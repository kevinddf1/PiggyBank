package com.example.cse110;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

/**
 * An Expense should be instantiated only by Category.
 */
public class Expense implements Parcelable {
    private String name;
    private int cost;
    private int year, month, day;
    private int id;
    // parentCategoryName should not be saved in the database
    private String parentCategoryName;

    private String costString;

    public Expense(int id, String name, double cost, int year, int month, int day, String parentCategoryName) {
        this.id = id;
        this.name = name;
        costString = Double.toString(cost);
        // Throws a NullPointerException if the input names are null or the month is invalid
        if (name.equals(null) || costString.equals(null) || month <= 0 || month > 12) {
            throw new NullPointerException("Name and cost must not be null. Month must " +
                    "be greater than or equal to 0.");
        }

        // Add formatting for whole numbers
        if(costString.indexOf('.') == -1){
            costString = costString.concat(".00");
        }else{
            //Ensure only valid input
            int costLength = costString.length();
            int decimalPlace = costString.indexOf(".");

            // If the user inputs a number formatted as "<num>.", appends a 00 after the decimal
            if (costLength - decimalPlace == 1) {
                costString = costString.substring(0, decimalPlace + 1) +  "00";
            }
            // If the user inputs a number formatted as "<num>.1", where 1 could be any number,
            // appends a 0 to the end
            else if (costLength - decimalPlace == 2) {
                costString = costString.substring(0, decimalPlace + 1 + 1) + "0";
            }
            // If the user inputs a number with >= 2 decimal places, only displays up to 2
            else {
                costString = costString.substring(0, costString.indexOf(".") + 2 + 1);
            }
        }
        this.costString = costString;
        this.cost = (int) (100 * Double.parseDouble(costString));
        this.month = month;
        this.year = year;
        this.day = day;
        this.parentCategoryName = parentCategoryName;
    }

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
     * Update the database to reflect changes in Expense's fields.
     * This is called every time any field in Expense is modified.
     */
    public void updateToDatabase() {
        // String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // TODO: uid uniquely identifies the user; use it to update the database; Expense.id uniquely identifies the Expense within the Category
        // use uid, id, and parentCategoryName
    }

    public String getName() {
        return name;
    }
    public int getCost() {
        return cost;
    }
    public int getYear() {
        return year;
    }
    public int getMonth() {
        return month;
    }
    public int getDay() {
        return day;
    }
    public int getId() {
        return id;
    }
    public String getCostAsString() {
        // TODO
        return "$" + costString;
    }

    public void setName(String name) {
        this.name = name;
        updateToDatabase();
    }
    public void setCost(int cost) {
        this.cost = cost;
        updateToDatabase();
    }
    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        updateToDatabase();
    }
    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
