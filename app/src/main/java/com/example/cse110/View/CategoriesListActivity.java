package com.example.cse110.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cse110.Model.Category;
import com.example.cse110.Model.MonthlyData;
import com.example.cse110.R;
import com.example.cse110.Controller.Settings;

import java.util.ArrayList;

public class CategoriesListActivity extends AppCompatActivity {
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String SETTINGS_INTENT = "CategoriesListActivity settings";

    //Our max allowable int is 9,999,999 which is 7 place values
    private static final int MAX_BUDGET =  7;

    EditText categoryName, categoryBudget;
    Button btnAdd;
    CategoriesListAdapter myAdapter;
    ListView categories;



    private MonthlyData monthlyData;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_list);

        // Bind element from XML file
        // Core elements of the activity
        categoryName = findViewById(R.id.category_name);
        categoryBudget = findViewById(R.id.category_budget);
        btnAdd = findViewById(R.id.AddToList);

        Intent intent = getIntent();
        monthlyData = intent.getParcelableExtra(MONTHLY_DATA_INTENT);
        settings = intent.getParcelableExtra(SETTINGS_INTENT);

        // Initialize List
        ArrayList<Category> arrayOfItems = monthlyData.getCategoriesAsArray();
        // Checklist Structure
        myAdapter = new CategoriesListAdapter(this, arrayOfItems, monthlyData);
        categories = (ListView) findViewById(R.id.Categories);
        categories.setAdapter(myAdapter);

        categories.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Category currentItem = myAdapter.getItem(position);

                Intent i = new Intent(CategoriesListActivity.this, ExpensesListActivity.class);
                i.putExtra(ExpensesListActivity.MONTHLY_DATA_INTENT, monthlyData);
                i.putExtra(ExpensesListActivity.SETTINGS_INTENT, settings);
                i.putExtra(ExpensesListActivity.CATEGORY_NAME_INTENT, currentItem.getName());
                startActivityForResult(i, 1);
            }
        });

        // Set Event Handler to add items to the list
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ensure that both fields are filled.
                if(!categoryBudget.getText().toString().isEmpty() && !categoryName.getText().toString().isEmpty() ) {

                    //Verify that max vale has not be reached.
                    if (categoryBudget.getText().toString().length() > MAX_BUDGET) {
                        if (settings.getEnableNotifications()) {
                            Toast.makeText(getBaseContext(), "A category cannot have a budget greater than $9,999,999.", Toast.LENGTH_LONG).show();
                        }
                    } else {

                        // Create new item and update adapter
                        boolean creationSuccessful = monthlyData.createCategory(categoryName.getText().toString(), Integer.parseInt(categoryBudget.getText().toString()));

                        // Verify that category was made
                        if (!creationSuccessful) {
                            if (settings.getEnableNotifications()) {
                                Toast.makeText(getBaseContext(), "A budget with this name already exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            if (settings.getEnableNotifications()) {
                                // Displays a Toast message that lets the user know the category was successfully created
                                Toast.makeText(getBaseContext(), "Category successfully added.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        //Clear inputs
                        categoryName.getText().clear();
                        categoryBudget.getText().clear();
                        myAdapter.notifyDataSetChanged();
                    }


                } else {
                    if (settings.getEnableNotifications()) {
                        // Insufficient number of filled fields results in an error warning.
                        Toast missingInformationWarning = Toast.makeText(getBaseContext(), "Please fill in category name and budget.", Toast.LENGTH_SHORT);
                        missingInformationWarning.show();
                    }
                }
            }
        });
    }

   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                monthlyData = data.getParcelableExtra(ExpensesListActivity.MONTHLY_DATA_INTENT);

                myAdapter = new CategoriesListAdapter(this, monthlyData.getCategoriesAsArray(), monthlyData);
                categories.setAdapter(myAdapter);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        intent.putExtra(MONTHLY_DATA_INTENT, monthlyData);
        super.onBackPressed();
    }

    public void confirmDeletion(TextView nameOfCategory) {
        Toast.makeText(getBaseContext(),  nameOfCategory.getText().toString() + " was deleted.", Toast.LENGTH_SHORT).show();
    }
}
