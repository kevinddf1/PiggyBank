package com.example.cse110;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MonthlyData implements Parcelable {
    private int month;
    private int year;
    private Map<String, Category> categories;
    private ArrayList<Category> categoriesArrayList;

    /*
    Constructor for an empty MonthlyData.
    */
    public MonthlyData(int month, int year) {
        this.month = month;
        this.year = year;
        categories = new HashMap<String, Category>();
        categoriesArrayList = new ArrayList<Category>();
    }

    protected MonthlyData(Parcel in) {
        month = in.readInt();
        year = in.readInt();
        categories = new HashMap<String, Category>();
        in.readMap(categories, Category.class.getClassLoader());
        categoriesArrayList = in.readArrayList(Category.class.getClassLoader());
    }

    // TODO: idk what this is
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
        parcel.writeList(categoriesArrayList);
    }

    public void updateFromDatabase() {
        // TODO
    }

    public void updateToDatabase() {
        // TODO
    }

    public Category getCategory(String name) {
        // TODO: error handling?
        if (!categories.containsKey(name)) {
            return new Category();
        }
        return categories.get(name);
    }

    public Category createCategory(String name, int budget) {
        Category category = new Category();
        category.setName(name);
        category.setBudget(budget);
        categories.put(name, category);
        categoriesArrayList.add(category);
        return category;
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
