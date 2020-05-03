package com.example.cse110;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class Database {
    // static variable single_instance of type Database
    private static Database single_instance=null;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String key;
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "MyActivity";
    private ArrayList<Category> categoriesArrayList;

    // static method to create instance of Database class
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
        User = mAuth.getCurrentUser();
        mAuth.signInWithEmailAndPassword("test@ucsd.edu", "q1w2e3r4t5y6").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //key = myRef.push().getKey();
                    //key = User.getUid();
                    System.out.println("login suc");
                } else {
                    System.out.println("login fail: " + task.getException().getLocalizedMessage());
                }
            }
        });
        key = User.getUid();
    }

    public DatabaseReference getMyRef() {
        return myRef;
    }

    public String getkey() {
        return key;
    }

    public void insertCategoryName(String name) {
        myRef.child("User").child(key).child("Category " + name).child("Name").setValue(name);
    }

    public void insertCategoryBudget(int budget, String name) {
        myRef.child("User").child(key).child("Category " + name).child("Budget").setValue(budget);
    }

    public void insertExpense(double cost, String name, String pname, int year, int month, int day, int nextExpenseId) {
        String str_ID = Integer.toString(nextExpenseId);
        myRef.child("User").child(key).child("Category " + pname).child("Expense").child(str_ID).child("Name").setValue(name);
        myRef.child("User").child(key).child("Category " + pname).child("Expense").child(str_ID).child("Cost").setValue(cost);
        myRef.child("User").child(key).child("Category " + pname).child("Expense").child(str_ID).child("Date").setValue(month + "/" + day + "/" + year);
        myRef.child("User").child(key).child("Category " + pname).child("Expense").child(str_ID).child("Year").setValue(year);
        myRef.child("User").child(key).child("Category " + pname).child("Expense").child(str_ID).child("Month").setValue(month);
        myRef.child("User").child(key).child("Category " + pname).child("Expense").child(str_ID).child("Day").setValue(day);
        myRef.child("User").child(key).child("Category " + pname).child("Expense").child(str_ID).child("ID").setValue(nextExpenseId);
    }

    public void delete_cate(String name) {
        myRef.child("User").child(key).child("Category " + name).removeValue();
    }

    public void delete_exp(String pname, int id) {
        String str_ID = Integer.toString(id);
        myRef.child("User").child(key).child("Category " + pname).child("Expense").child(str_ID).removeValue();
    }
}

