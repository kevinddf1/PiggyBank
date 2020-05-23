package com.example.cse110.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cse110.Controller.Category;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Model.CategoriesListAdapter;
import com.example.cse110.R;
import com.example.cse110.Controller.Settings;
import com.example.cse110.Model.Database;
import com.example.cse110.View.history.HistoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;

public class CategoriesListActivity extends AppCompatActivity {
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String SETTINGS_INTENT = "CategoriesListActivity settings";
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";

    //Our max allowable int is 9,999,999 which is 7 place values
    private static final int MAX_BUDGET =  7;

    // create a Database object
    private Database base = Database.Database();

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
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(1);
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(navListener);
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
                        base.insertTotalBudget(monthlyData.getYear(), monthlyData.getIntMonth(), monthlyData.getTotalBudget());

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
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:

                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            setResult(RESULT_OK, intent);
                            intent.putExtra(MONTHLY_DATA_INTENT, monthlyData);
                            startActivityForResult(intent, 1);
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.navigation_lists:
                            return true;

                        case R.id.navigation_history:
                            Intent i = new Intent(getBaseContext(), HistoryActivity.class);
                            setResult(RESULT_OK, i);
                            //i.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, monthlyData);
                            // TODO: grab this from the database

                            if (monthlyData == null) {
                                Calendar today = Calendar.getInstance();
                                monthlyData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
                            }

                            i.putExtra(HISTORY_DATA_INTENT, monthlyData);
                            startActivityForResult(i, 1);
                            overridePendingTransition(0, 0);
                            return true;
                            case R.id.navigation_graphs:
                                    Intent inte = new Intent(getBaseContext(), PieChartActivity.class);
                                    inte.putExtra(PIE_CHART_DATA_INTENT, monthlyData);
                                    startActivityForResult(inte, 1);
                                    overridePendingTransition(0, 0);
                            return true;
                        case R.id.navigation_settings:
                            Intent inten = new Intent(getBaseContext(), SettingsActivity.class);
                            if (settings == null) {
                                settings = new Settings();
                            }
                            inten.putExtra(SettingsActivity.SETTINGS_INTENT, settings);
                            inten.putExtra(PIE_CHART_DATA_INTENT, monthlyData);
                            startActivityForResult(inten, 1);
                            overridePendingTransition(0, 0);
                            return true;



                    }
                    return false;
                }
            };
}
