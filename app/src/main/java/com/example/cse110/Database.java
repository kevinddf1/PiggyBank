package com.example.cse110;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class Database {
    private static final String TAG = "MyActivity";
    private static Database single_instance=null; // static variable single_instance of type Database
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
        mAuth.signInWithEmailAndPassword("test2@ucsd.edu", "password").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser User = mAuth.getCurrentUser();
                    key = User.getUid();
                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    //Toast.makeText(, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            }
        });

    }

    public DatabaseReference getMyRef() {
        return myRef;
    }

    public String getUserKey() {
        return key;
    }

    public void insertCategoryName(String name) {
        myRef.child("User").child(key).child("Category " + name).child("Name").setValue(name);
    }

    public void insertCategoryBudget(int budget, String name) {
        myRef.child("User").child(key).child("Category " + name).child("Budget").setValue(budget);
    }

    public void insertCategoryDate(int year, int month, String name) {
        myRef.child("User").child(key).child("Category " + name).child("Year").setValue(year);
        myRef.child("User").child(key).child("Category " + name).child("Month").setValue(month);
    }

    public void insertExpense(double cost, String name, String parent_name, int year, int month, int day, int nextExpenseId) {
        String str_ID = Integer.toString(nextExpenseId);
        myRef.child("User").child(key).child("Category " + parent_name).child("Expense").child(str_ID).child("Name").setValue(name);
        myRef.child("User").child(key).child("Category " + parent_name).child("Expense").child(str_ID).child("Cost").setValue(cost);
        myRef.child("User").child(key).child("Category " + parent_name).child("Expense").child(str_ID).child("Date").setValue(month + "/" + day + "/" + year);
        myRef.child("User").child(key).child("Category " + parent_name).child("Expense").child(str_ID).child("Year").setValue(year);
        myRef.child("User").child(key).child("Category " + parent_name).child("Expense").child(str_ID).child("Month").setValue(month);
        myRef.child("User").child(key).child("Category " + parent_name).child("Expense").child(str_ID).child("Day").setValue(day);
        myRef.child("User").child(key).child("Category " + parent_name).child("Expense").child(str_ID).child("ID").setValue(nextExpenseId);
    }

    public void delete_cate(String name) {
        myRef.child("User").child(key).child("Category " + name).removeValue();
    }

    public void delete_exp(String parent_name, int id) {
        String str_ID = Integer.toString(id);
        myRef.child("User").child(key).child("Category " + parent_name).child("Expense").child(str_ID).removeValue();
    }

    public MonthlyData RetrieveDatafromDatabase(DataSnapshot dataSnapshot, MonthlyData thisMonthsData) {
        if (thisMonthsData == null) { // check if the object is NULL, if NULL initialize it with current Date
            Calendar today = Calendar.getInstance();
            thisMonthsData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));

            // this loop retrieve all the categories from database
            for (DataSnapshot ds : dataSnapshot.child("User").child(key).getChildren()) {
                if (!ds.exists()) { // check if there are any category in user's account
                    break; // if NOT, break the loop
                }
                // get the data of current category
                String cate_name = ds.child("Name").getValue().toString();
                System.out.println(cate_name);
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
                    int iCost = Integer.parseInt(Cost);
                    int iYear = Integer.parseInt(Year);
                    int iMonth = Integer.parseInt(Month);
                    int iDay = Integer.parseInt(Day);
                    int iID = Integer.parseInt(ID);
                    // create expense
                    Expense expense = new Expense(iID, Name, iCost, iYear, iMonth, iDay, cate_name);
                    expenses.add(expense);
                }
                // create category
                thisMonthsData.createExistCategory(cate_name, cate_budget, expenses, cate_month, cate_year);
            }
        }
        return thisMonthsData;
    }
}


