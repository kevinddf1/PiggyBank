package com.example.cse110;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Map;

public class MonthlyData implements Parcelable {
    private Date monthAndYear;
    private Map<String, Category> categories;

    /*
    Constructor for an empty MonthlyData.
    */
    public MonthlyData(Date monthAndYear) {
        this.monthAndYear = monthAndYear;
    }

    protected MonthlyData(Parcel in) {
        // TODO
    }

    // TODO
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
        // TODO
    }

    public void updateFromDatabase() {
        // TODO
    }

    public void updateToDatabase() {
        // TODO
    }

    public Category getCategory(String name) {
        // TODO: error handling?
        return categories.get(name);
    }

    public void addCategory(String name) {
        categories.put(name, new Category());
    }
}
