package com.example.cse110.Model;

import com.example.cse110.Controller.Expense;
import com.example.cse110.Controller.MonthlyData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Database {
    private static final String TAG = "MyActivity";
    private static Database single_instance = null; // static variable single_instance of type Database
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String key;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    // static method to create single instance of Database class
    public static Database Database() {
        // To ensure only one instance is created
        if (single_instance == null) {
            single_instance = new Database();
        }
        return single_instance;
    }

    public Database() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser User = mAuth.getCurrentUser();
        key = User.getUid();
    }

    public DatabaseReference getMyRef() {
        return myRef;
    }

    public String getUserKey() {
        return key;
    }

    public void insertMonthlydata(int year, int month) {
        DatabaseReference ref = myRef.child("User").child(key).child(this.getMonth(month) + year);
        ref.child("Year").setValue(year);
        ref.child("Month").setValue(month);
    }

    public void insertTotalBudget(int year, int month, long totalBudget) {
        DatabaseReference ref = myRef.child("User").child(key).child(this.getMonth(month) + year);
        ref.child("Total Budget").setValue(totalBudget);
    }

    public void insertTotalExpense(int year, int month, long totalExpense) {
        DatabaseReference ref = myRef.child("User").child(key).child(this.getMonth(month) + year);
        ref.child("Total Expense").setValue(totalExpense);
    }

    public void insertCategoryName(String name, int year, int month) {
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + name).child("Name").setValue(name);
    }

    public void insertCategoryBudget(int budget, String name, int year, int month) {
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + name).child("Budget").setValue(budget);
    }

    public void insertCategoryDate(int year, int month, String name) {
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + name).child("Year").setValue(year);
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + name).child("Month").setValue(month);
    }

    public void insertExpense(double cost, String name, String parent_name, int year, int month, int day, int nextExpenseId) {
        String str_ID = Integer.toString(nextExpenseId);
        DatabaseReference ref = myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + parent_name).child("Expense").child(str_ID);
        ref.child("Name").setValue(name);
        ref.child("Cost").setValue(cost);
        ref.child("Date").setValue(month + "/" + day + "/" + year);
        ref.child("Year").setValue(year);
        ref.child("Month").setValue(month);
        ref.child("Day").setValue(day);
        ref.child("ID").setValue(nextExpenseId);
    }

//    public void insertExpenseId(String name, String parent_name, int nextExpenseId) {
//        String str_ID = Integer.toString(nextExpenseId);
//        myRef.child("User").child(key).child("Category " + parent_name).child("Expense").child(str_ID).child("").setValue(name);
//
//        myRef = myRef.child("User").child(key).child("Category " + parent_name).child("Expense").child();
//        Map newUserData = new HashMap();
//        newUserData.put(YOUR_NEW_DATA);
//        userRef.updateChildren(newUserData);
//    }

    public void delete_cate(String name, int year, int month) {
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + name).removeValue();
    }

    public void delete_exp(String cate_name, int id, int year, int month) {
        String str_ID = Integer.toString(id);
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + cate_name).child("Expense").child(str_ID).removeValue();
    }

    public void delete_account() {
        myRef.child("User").child(key).removeValue();
    }

//    public void insert_notification(boolean enabled) {
//        myRef.child("User").child(key).child("Notification").setValue(enabled);
//    }

    public ArrayList<String> RetrieveT_Budget_Exp(DataSnapshot dataSnapshot, int year, int month) {
        DataSnapshot ds = dataSnapshot.child("User").child(key).child(this.getMonth(month) + year);
        ArrayList<String> list = new ArrayList<String>(2);
        if (ds.child("Total Budget").getValue() == null || ds.child("Total Expense").getValue() == null) {
            list.add("0");
            list.add("0");
        }
        else {
            String T_Budget = ds.child("Total Budget").getValue().toString();
            String T_Expense = ds.child("Total Expense").getValue().toString();
            list.add(T_Budget);
            list.add(T_Expense);
        }
        return list;
    }

    public MonthlyData RetrieveDataCurrent(DataSnapshot dataSnapshot, MonthlyData thisMonthsData, int year, int month) {
        if (thisMonthsData == null) { // check if the object is NULL, if NULL initialize it with current Date
            thisMonthsData = new MonthlyData(month, year);
            DataSnapshot dsMonthlyData = dataSnapshot.child("User").child(key).child(this.getMonth(month) + year);
            if(dsMonthlyData == null || dsMonthlyData.child("Total Budget").getValue() == null || dsMonthlyData.child("Total Expense").getValue() ==  null){
                thisMonthsData.setTotalBudgetDatabase("0");
                thisMonthsData.setTotalExpensesDatabase("0");

            }else {
                thisMonthsData.setTotalBudgetDatabase(dsMonthlyData.child("Total Budget").getValue().toString());
                thisMonthsData.setTotalExpensesDatabase(dsMonthlyData.child("Total Expense").getValue().toString());
            }

            // this loop retrieve all the categories from database
            for (DataSnapshot ds : dataSnapshot.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").getChildren()) {
                if (!ds.exists()) { // check if there are any category in user's account
                    break; // if NOT, break the loop
            }
                // get the data of current category
                String cate_name = ds.child("Name").getValue().toString();
                String c_budget = ds.child("Budget").getValue().toString();
                int cate_budget = Integer.parseInt(c_budget);
                String c_year = ds.child("Year").getValue().toString();
                int cate_year = Integer.parseInt(c_year);
                String c_month = ds.child("Month").getValue().toString();
                int cate_month = Integer.parseInt(c_month);

                ArrayList<Expense> expenses = new ArrayList<Expense>();
                // this loop retrieve all the expenses in current category from database
                for (DataSnapshot ds2 : ds.child("Expense").getChildren()) {
                    if (!ds2.exists()) { // check if there are any expenses in user's account
                        break; // if NOT, break the loop
                    }
                    // get the data of current expense
                    String Cost = ds2.child("Cost").getValue().toString();
                    String Year = ds2.child("Year").getValue().toString();
                    String Month = ds2.child("Month").getValue().toString();
                    String Day = ds2.child("Day").getValue().toString();
                    String Name = ds2.child("Name").getValue().toString();
                    String ID = ds2.child("ID").getValue().toString();
                    double dCost = Double.parseDouble(Cost)/100.00;
                    int iYear = Integer.parseInt(Year);
                    int iMonth = Integer.parseInt(Month);
                    int iDay = Integer.parseInt(Day);
                    int iID = Integer.parseInt(ID);
                    // create expense
                    Expense expense = new Expense(iID, Name, dCost, iYear, iMonth, iDay, cate_name);
                    expenses.add(expense);
                }
                // create category
                thisMonthsData.createExistCategory(cate_name, cate_budget, expenses, cate_month, cate_year).setTotalExpenses();
                thisMonthsData = this.RetrieveCateData(ds, thisMonthsData);
                }
            }
        return thisMonthsData;
        }


    public MonthlyData RetrieveDataPast(DataSnapshot dataSnapshot, MonthlyData pastMonthsData, int year, int month) {
        if (pastMonthsData == null) { // check if the object is NULL, if NULL initialize it with current Date
            pastMonthsData = new MonthlyData(month, year);

            // this loop retrieve all the categories from database
            for (DataSnapshot ds : dataSnapshot.child("User").child(key).getChildren()) {
                if (!ds.exists()) { // check if there are any monthly data in user's account
                    break; // if NOT, break the loop
                }

                String str_year = ds.child("Year").getValue().toString();
                int int_year = Integer.parseInt(str_year);
                String str_month = ds.child("Month").getValue().toString();
                int int_month = Integer.parseInt(str_month);

                if(!(int_year == year && int_month == month)) {
                    for (DataSnapshot ds3 : ds.child("< Categories >").getChildren()) {
                        if (!ds3.exists()) { // check if there are any category in user's account
                            break; // if NOT, break the loop
                        }
                        pastMonthsData = this.RetrieveCateData(ds3, pastMonthsData);
                    }
                }
            }
        }
        return pastMonthsData;
    }

    private MonthlyData RetrieveCateData(DataSnapshot ds, MonthlyData md) {
        // get the data of current category
        String cate_name = ds.child("Name").getValue().toString();
        //System.out.println(cate_name);
        String c_budget = ds.child("Budget").getValue().toString();
        int cate_budget = Integer.parseInt(c_budget);
        String c_year = ds.child("Year").getValue().toString();
        int cate_year = Integer.parseInt(c_year);
        String c_month = ds.child("Month").getValue().toString();
        int cate_month = Integer.parseInt(c_month);

        ArrayList<Expense> expenses = new ArrayList<Expense>();
        // this loop retrieve all the expenses in current category from database
        for (DataSnapshot ds2 : ds.child("Expense").getChildren()) {
            if (!ds2.exists()) { // check if there are any expenses in user's account
                break; // if NOT, break the loop
            }
            // get the data of current expense
            String Cost = ds2.child("Cost").getValue().toString();
            String Year = ds2.child("Year").getValue().toString();
            String Month = ds2.child("Month").getValue().toString();
            String Day = ds2.child("Day").getValue().toString();
            String Name = ds2.child("Name").getValue().toString();
            String ID = ds2.child("ID").getValue().toString();
            double dCost = Double.parseDouble(Cost)/100.00;
            int iYear = Integer.parseInt(Year);
            int iMonth = Integer.parseInt(Month);
            int iDay = Integer.parseInt(Day);
            int iID = Integer.parseInt(ID);
            // create expense
            Expense expense = new Expense(iID, Name, dCost, iYear, iMonth, iDay, cate_name);
            expenses.add(expense);
        }
        // create category
        md.createExistCategory(cate_name, cate_budget, expenses, cate_month, cate_year).setTotalExpenses();
        return md;
    }


    public String getMonth(int month) {
        switch (month) {
            case 0:
                return "January";

            case 1:
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
    }

    /**
     * This method retrieves all children in the database that are monthlyData.
     * @return An ArrayList of Strings. Each one contains Month, Year, TotalBudget, TotalExpenses, dellimetered by '-'
     */
    public ArrayList<String> getPastMonthSummary(DataSnapshot dataSnapshot){
        ArrayList<String> pastMonths = new ArrayList<>();

        // this loop retrieve all the months from the database
        for (DataSnapshot ds : dataSnapshot.child("User").child(key).getChildren()) {
            if (!ds.exists()) { // check if there are any monthly data in user's account
                break; // if NOT, break the loop
            }

            //Go through the entry's information to store in array
            String str_month = ds.child("Month").getValue().toString();
            int int_month = Integer.parseInt(str_month);
            String monthName = getMonth(int_month); // MONTH

            String str_year = ds.child("Year").getValue().toString();
            int int_year = Integer.parseInt(str_year); // YEAR

            String str_budget = ds.child("Total Budget").getValue().toString(); //TOTAL BUDGET

            String str_expenses = ds.child("Total Expense").getValue().toString(); //TOTAL EXPENSES

            //Add the info into one ArrayList entry w/ proper format
            pastMonths.add(str_month + "-" + str_year + "-" + str_budget + "-" + str_expenses);

        }

        return pastMonths;
    }

    /**
     * Another signature for the method that allows the user to input strings instead of integers for month & year
     * @param dataSnapshot
     * @param thisMonthsData
     * @param s
     * @param s1
     * @return
     */
    public MonthlyData RetrieveDataPast(DataSnapshot dataSnapshot, MonthlyData thisMonthsData, String s, String s1) {
        if (thisMonthsData == null) { // check if the object is NULL, if NULL initialize it with current Date

            thisMonthsData = new MonthlyData(Integer.parseInt(s), Integer.parseInt(s1));
            // this loop retrieve all the categories from database
            for (DataSnapshot ds : dataSnapshot.child("User").child(key).getChildren()) {
                if (!ds.exists()) { // check if there are any monthly data in user's account
                    break; // if NOT, break the loop
                }

                String str_year = ds.child("Year").getValue().toString();
                int int_year = Integer.parseInt(str_year);
                String str_month = ds.child("Month").getValue().toString();
                int int_month = Integer.parseInt(str_month);

                if(str_year.equals(s1) && str_month.equals(s)) {
                    for (DataSnapshot ds3 : ds.child("< Categories >").getChildren()) {
                        if (!ds3.exists()) { // check if there are any category in user's account
                            break; // if NOT, break the loop
                        }

                        thisMonthsData = this.RetrieveCateData(ds3, thisMonthsData);
                    }
                }
            }
        }
        return thisMonthsData;
    }


}


