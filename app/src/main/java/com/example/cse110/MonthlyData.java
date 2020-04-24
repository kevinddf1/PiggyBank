package com.example.cse110;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MonthlyData implements Parcelable {
    // Month and year should never be modified outside the constructor
    private int month;
    private int year;
    private Map<String, Category> categories;
    // This is not serialized but is repopulated from categories so that categories and categoriesArrayList refer to the same Category objects
    private ArrayList<Category> categoriesArrayList;

    /*
    Constructor for an empty MonthlyData.
    */
    public MonthlyData(int month, int year) {
        this.month = month;
        this.year = year;
        categories = new HashMap<>();
        categoriesArrayList = new ArrayList<>();
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

    public void updateFromDatabase() {
        // TODO
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
            return true;
        }
        return false;
    }

    public void deleteCategory(String name) {
        categories.remove(name);
        for (int i = 0; i < categoriesArrayList.size(); i++) {
            if (categoriesArrayList.get(i).getName().equals(name)) {
                categoriesArrayList.remove(i);
                break;
            }
        }
    }


    public ArrayList<Category> getCategoriesAsArray() {
        return categoriesArrayList;
    }
}
