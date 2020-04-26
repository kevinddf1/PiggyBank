package com.example.cse110;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Database {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String key;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "MyActivity";
    private ArrayList<Category> categoriesArrayList;

    public Database() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        key = myRef.push().getKey();
    }

//    public String getkey() {
//        return key;
//    }

    public DatabaseReference getmyRef() {
        return myRef;
    }

//    public Database(Database a)
//    {
//        this.mFirebaseDatabase = a.mFirebaseDatabase;
//        this.myRef = a.myRef;
//        this.key = a.key;
//    }

    public void insertCategoryName(String name) {
        myRef.child(key).child("Category").child("Name").setValue(name);
    }

    public void insertCategoryBudget(int budget) {
        myRef.child(key).child("Category").child("Budget").setValue(budget);
    }

    public void insertExpenseName(String name) {
        myRef.child(key).child("Category").child("Expense").child("Name").setValue(name);
    }

    public void insertExpenseCost(int cost) {
        myRef.child(key).child("Category").child("Expense").child("Cost").setValue(cost);
    }

    //        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });
}
