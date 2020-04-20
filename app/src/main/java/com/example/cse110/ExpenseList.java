package com.example.cse110;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ExpenseList extends AppCompatActivity {
    EditText expenseName, expenseCost;
    Button btnAdd;
    ExpenseListAdapter myAdapter;
    ListView expensesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        // Bind element from XML file
        // Core elements of the activity
         expenseName = findViewById(R.id.ExpenseName);
         expenseCost = findViewById(R.id.ExpenseCost);
         btnAdd = findViewById(R.id.AddToList);

        // Initialize List
        ArrayList<ExpenseItem> arrayOfItems = new ArrayList<>();
        // Checklist Structure
         myAdapter = new ExpenseListAdapter(this, arrayOfItems);
         expensesList = (ListView) findViewById(R.id.Expenses);
        expensesList.setAdapter(myAdapter);

        expensesList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExpenseItem currentItem = myAdapter.getItem(position);

                Intent i = new Intent(ExpenseList.this, Category.class);
                i.putExtra("category_budget", currentItem.getBudget());
                i.putExtra("category_name",  currentItem.getCategory());
                startActivity(i);


            }
        });

        // Set Event Handler to add items to the list
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ensure that both fields are filled.
                if(!expenseCost.getText().toString().isEmpty() && !expenseName.getText().toString().isEmpty() ) {

                    // Create new item and update adapter
                    ExpenseItem newItem = new ExpenseItem(expenseName.getText().toString(), Integer.parseInt(expenseCost.getText().toString() ));
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
