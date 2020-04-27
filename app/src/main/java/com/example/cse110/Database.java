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

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword("test@ucsd.edu", "q1w2e3r4t5y6").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //key = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    //key = myRef.push().getKey();
                    //key = mAuth.getCurrentUser().getUid();
                    System.out.println("login suc");
                } else {
                    System.out.println("login fail: " + task.getException().getLocalizedMessage());
                }
            }
        });
        key = mAuth.getCurrentUser().getUid();
    }

    public void insertCategoryName(String name) {
        myRef.child(key).child("Category " + name).child("Name").setValue(name);
    }

    public void insertCategoryBudget(int budget, String name) {
        myRef.child(key).child("Category " + name).child("Budget").setValue(budget);
    }

    public void insertExpense(int cost, String name, String pname) {
        myRef.child(key).child("Category " + pname).child("Expense " + name).child("Name").setValue(name);
        myRef.child(key).child("Category " + pname).child("Expense " + name).child("Cost").setValue(cost);
    }

//    public void insertExpenseName(String name) {
//        myRef.child(key).child("Category").child("Expense").child("Name").setValue(name);
//    }
//
//    public void insertExpenseCost(int cost) {
//        myRef.child(key).child("Category").child("Expense").child("Cost").setValue(cost);
//    }

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
