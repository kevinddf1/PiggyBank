package com.example.cse110;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Category extends AppCompatActivity {

    private int month;
    private EditText expenseName, expenseCost;
    private String name;
    //List Structure
    private CategoryListAdapter categoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent intent = getIntent();
        this.name = Objects.requireNonNull(intent.getExtras()).getString("category_name");
        //Core values for the category
        int budget = intent.getExtras().getInt("category_budget");

        //Toolbar categoryToolBar = findViewById(R.id.categoryBar);
        //setActionBar(categoryToolBar);

        //textViews in the top bar
        TextView categoryName = findViewById(R.id.category_name);
        categoryName.setText( name);

        TextView categoryBudget = findViewById((R.id.budget_display));
        categoryBudget.setText("$" + Integer.toString(budget));

        // Bind element from XML file
        expenseName = findViewById(R.id.ExpenseName);
        expenseCost = findViewById(R.id.ExpenseCost);
        Button btnAdd = findViewById(R.id.AddToList);

        // Initialize List
        ArrayList<CategoryItem> arrayOfItems = CategoryItem.getItems();
        categoryAdapter = new CategoryListAdapter(this, arrayOfItems);
        ListView expensesList = findViewById(R.id.Expenses);
        expensesList.setAdapter(categoryAdapter);

        // Set Event Handler to add items to the list
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Date Object
                Date today = new Date();

                //Convert to calendar Object
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(today);

                month = calendar.get(Calendar.MONTH);

                // Ensure that both fields are filled.
                if(!expenseCost.getText().toString().isEmpty() && !expenseName.getText().toString().isEmpty() ) {

                    // Create new item and update adapter
                    CategoryItem newItem = new CategoryItem(expenseCost.getText().toString(), expenseName.getText().toString(),name, month);
                    categoryAdapter.add(newItem);
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
