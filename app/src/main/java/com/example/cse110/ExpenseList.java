package com.example.cse110;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ExpenseList extends AppCompatActivity {
    // Core elements of the activity
    private EditText expenseName;
    private EditText expenseCost;

    // Checklist Structure
    private Adapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        // Bind element from XML file
        expenseName = findViewById(R.id.ExpenseName);
        expenseCost = findViewById(R.id.ExpenseCost);
        Button btnAdd = findViewById(R.id.AddToList);

        // Initialize List
        ArrayList<ExpenseItem> arrayOfItems = ExpenseItem.getItems();
        myAdapter = new Adapter(this, arrayOfItems);
        ListView expensesList = findViewById(R.id.Expenses);
        expensesList.setAdapter(myAdapter);

        // Set Event Handler to add items to the list
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Date Object
                Date today = new Date();

                //Convert to calendar Object
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(today);

                int month = calendar.get(Calendar.MONTH);

                // Ensure that both fields are filled.
                if(!expenseCost.getText().toString().isEmpty() && !expenseName.getText().toString().isEmpty() ) {

                    // Create new item and update adapter
                    ExpenseItem newItem = new ExpenseItem(expenseCost.getText().toString(), expenseName.getText().toString(), month);
                    myAdapter.add(newItem);
                    expenseName.getText().clear();
                    expenseCost.getText().clear();
                }else{

                    // Insufficient number of filled fields results in an error warning.
                    Toast missingInformationWarning = Toast.makeText(getBaseContext(), "Missing Information", Toast.LENGTH_SHORT);
                    missingInformationWarning.show();
                }


            }
        });
    }
}
