/*
 * This file includes implementation of functions to
 * - upload and retrieve data
 * - delete account or data
 * from database.
 */
package com.example.cse110.Model;
// import Expense and MonthlyData class Controller
import com.example.cse110.Controller.Expense;
import com.example.cse110.Controller.MonthlyData;
// import essential firebase class
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
// import ArrayList class
import java.util.ArrayList;

public class Database {
    private static Database single_instance = null;         // static variable single_instance of type Database
    private FirebaseAuth mAuth;                             // firebase class used for user login and get user info
    private FirebaseDatabase mFirebaseDatabase;             // The entry point for accessing a FirebaseDatabase.
    private DatabaseReference myRef;                        /* A Firebase reference represents a particular location in the FirebaseDatabase
                                                            and can be used for reading or writing data to that FirebaseDatabase location. */
    private String key;                                     // String variable to store the UiD of a user
    private CloudData data;                                 // an class to store received data from database

    ////////////////////////////////////////////////// CONSTRUCTORS, DESTRUCTOR AND GETTER FUNCTION //////////////////////////////////////////////////

    // static method to create single instance of Database class
    public static Database getInstance() {
        // This If statement ensures only one instance is created
        if (single_instance == null) {
            single_instance = new Database();
        }
        return single_instance;
    }
    // constructor of Database class
    public Database() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        key = mAuth.getCurrentUser().getUid();
    }
    // This getter function allows Database object in other Activity classes be able to call listener functions
    public DatabaseReference getMyRef() {
        return myRef;
    }
    // reset the database object when user log out
    public void clear()
    {
        single_instance = null;
    }
    // set uid
    public void setKey(){
        FirebaseUser User = mAuth.getCurrentUser();
        key = User.getUid();
    }

    ////////////////////////////////////////////////// INSERTION FUNCTIONS //////////////////////////////////////////////////

    // insert monthly data, year and month to database
    public void insertMonthlyData(int year, int month) {
        DatabaseReference ref = myRef.child("User").child(key).child(this.getMonth(month) + year);
        ref.child("Year").setValue(year);
        ref.child("Month").setValue(month);
    }
    // insert total budget
    public void insertTotalBudget(int year, int month, long totalBudget) {
        DatabaseReference ref = myRef.child("User").child(key).child(this.getMonth(month) + year);
        ref.child("Total Budget").setValue(totalBudget);
    }
    // insert total expense
    public void insertTotalExpense(int year, int month, long totalExpense) {
        DatabaseReference ref = myRef.child("User").child(key).child(this.getMonth(month) + year);
        ref.child("Total Expense").setValue(totalExpense);
    }
    // insert Category Name
    public void insertCategoryName(String name, int year, int month) {
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + name).child("Name").setValue(name);
    }
    // insert Category Budget
    public void insertCategoryBudget(int budget, String name, int year, int month) {
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + name).child("Budget").setValue(budget);
    }
    // insert Category Date, Month and year
    public void insertCategoryDate(int year, int month, String name) {
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + name).child("Year").setValue(year);
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + name).child("Month").setValue(month);
    }
    // insert single expense
    public void insertCategoryExpenses(long expenses, String name, int year, int month) {
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + name).child("Expenses").setValue(expenses);
    }
    // insert expense info
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

    ////////////////////////////////////////////////// DELETION FUNCTIONS //////////////////////////////////////////////////

    // delete a category with total budget and expense
    public void delete_cate(String name, final int year, final int month, final int categoryTotal, final long categoryExpense) {
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + name).removeValue();
    }
    // delete a category
    public void delete_cate(String name, int year, int month) {
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + name).removeValue();
    }
    // delete an expense
    public void delete_exp(String cate_name, int id, int year, int month) {
        String str_ID = Integer.toString(id);
        myRef.child("User").child(key).child(this.getMonth(month) + year).child("< Categories >").child("Category " + cate_name).child("Expense").child(str_ID).removeValue();
    }
    // delete a user account, all data of current user will LOST
    public void delete_account() {
        myRef.child("User").child(key).removeValue();
    }

    ////////////////////////////////////////////////// RETRIEVE DATA FUNCTIONS //////////////////////////////////////////////////

    static int SIZE = 2; // constant to initialize the size of an array

    // retrieve Total Budget and Total Expense of current month after user login
    public ArrayList<String> RetrieveT_Budget_Exp(DataSnapshot dataSnapshot, int year, int month) {
        DataSnapshot ds = dataSnapshot.child("User").child(key).child(this.getMonth(month) + year);
        ArrayList<String> list = new ArrayList<String>(SIZE);
        // this If statement checks whether the user has Total Budget or Total Expense or not
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
    // retrieve current month data
    public MonthlyData RetrieveDataCurrent(DataSnapshot dataSnapshot, MonthlyData thisMonthsData, int year, int month) {
        if (thisMonthsData == null) { // check if the object is NULL, if NULL initialize it with current Date
            thisMonthsData = new MonthlyData(month, year);
            DataSnapshot dsMonthlyData = dataSnapshot.child("User").child(key).child(this.getMonth(month) + year);
            // this If statement checks whether the user has Total Budget or Total Expense or not
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
                // ******************** Retrieve Data Twice? ******************** //
                data = RetrieveCateData(ds);
                // create category
                thisMonthsData.createExistCategory(data.cate_name, data.cate_budget, data.expenses, data.cate_month, data.cate_year).setTotalExpenses();

                data = RetrieveCateData(ds);
                // create category
                thisMonthsData.createExistCategory(data.cate_name, data.cate_budget, data.expenses, data.cate_month, data.cate_year).setTotalExpenses();
            }
        }
        return thisMonthsData;
    }
    // retrieve past month data
    public MonthlyData RetrieveDataPast(DataSnapshot dataSnapshot, MonthlyData thisMonthsData, String s, String s1) {
        if (thisMonthsData == null) { // check if the object is NULL, if NULL initialize it with current Date
            thisMonthsData = new MonthlyData(Integer.parseInt(s), Integer.parseInt(s1));
            // this loop retrieve all the categories from database
            for (DataSnapshot ds : dataSnapshot.child("User").child(key).getChildren()) {
                if (!ds.exists()) { // check if there are any monthly data in user's account
                    break; // if NOT, break the loop
                }

                String str_year = ds.child("Year").getValue().toString();
                String str_month = ds.child("Month").getValue().toString();

                if(str_year.equals(s1) && str_month.equals(s)) {
                    for (DataSnapshot ds3 : ds.child("< Categories >").getChildren()) {
                        if (!ds3.exists()) { // check if there are any category in user's account
                            break; // if NOT, break the loop
                        }
                        data = RetrieveCateData(ds3);
                        // create category
                        thisMonthsData.createExistCategory(data.cate_name, data.cate_budget, data.expenses, data.cate_month, data.cate_year).setTotalExpenses();
                    }
                }
            }
        }
        return thisMonthsData;
    }
    // This method retrieves all children in the database that are monthlyData.
    public ArrayList<String> getPastMonthSummary(DataSnapshot dataSnapshot){
        ArrayList<String> pastMonths = new ArrayList<>();
        // this loop retrieve all the months from the database
        for (DataSnapshot ds : dataSnapshot.child("User").child(key).getChildren()) {
            if (!ds.exists()) { // check if there are any monthly data in user's account
                break; // if NOT, break the loop
            }
            //Go through the entry's information to store in array
            String str_month = ds.child("Month").getValue().toString();
            String str_year = ds.child("Year").getValue().toString();
            String str_budget = ds.child("Total Budget").getValue().toString(); //TOTAL BUDGET
            String str_expenses = ds.child("Total Expense").getValue().toString(); //TOTAL EXPENSES
            //Add the info into one ArrayList entry w/ proper format
            pastMonths.add(str_month + "-" + str_year + "-" + str_budget + "-" + str_expenses);
        }
        return pastMonths;
    }

    ////////////////////////////////////////////////// HELPER FUNCTIONS //////////////////////////////////////////////////

    static double HD = 100.00; // constant to convert expense cost

    // helper function to store data retrieved from database, so it can be reused
    static class CloudData {
        String cate_name;                   // category name
        int cate_budget;                    // category budget
        int cate_year;                      // category year
        int cate_month;                     // category month
        ArrayList<Expense> expenses;        // expenses in category
        // constructor for CloudData
        CloudData(String name, int budget, ArrayList<Expense> exp, int month, int year)
        {
            cate_name = name;
            cate_budget = budget;
            cate_year = year;
            cate_month = month;
            expenses = exp;
        }
    }
    // helper function to store the data into CloudData class
    static CloudData getCloudData(String cate_name, int cate_budget, ArrayList<Expense> expenses, int cate_month, int cate_year) {
        // Returning multiple values of different types by returning an object
        return new CloudData(cate_name, cate_budget, expenses, cate_month, cate_year);
    }
    // helper function takes a Database snapshot and loops to retrieve data
    private CloudData RetrieveCateData(DataSnapshot ds) {
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
            double dCost = Double.parseDouble(Cost)/HD;
            int iYear = Integer.parseInt(Year);
            int iMonth = Integer.parseInt(Month);
            int iDay = Integer.parseInt(Day);
            int iID = Integer.parseInt(ID);
            // create expense
            Expense expense = new Expense(iID, Name, dCost, iYear, iMonth, iDay, cate_name);
            expenses.add(expense);
        }
        CloudData ans = getCloudData(cate_name, cate_budget, expenses, cate_month, cate_year);
        return ans;
    }
    // helper function to convert int month to monthly calender name
    private String getMonth(int month) {
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
}