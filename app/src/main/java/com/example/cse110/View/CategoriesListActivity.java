package com.example.cse110.View;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse110.Controller.Category;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Model.CategoriesListAdapter;
import com.example.cse110.R;
import com.example.cse110.Model.Database;
import com.example.cse110.View.graphs.GraphsActivity;
import com.example.cse110.View.history.HistoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * The app page to display the user's categories and add new ones.
 *
 * @author Peter Gonzalez and Thuycam Nguyen
 * @version 5.24
 */
public class CategoriesListActivity extends AppCompatActivity {

    /**
     * Keys for pulling information into the page and for pushing information to new pages
     */
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    private static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    private static final String Graphs_DATA_INTENT = "GraphsActivity monthlyData";
    private static final String LIST_OF_MONTHS = "List of Months"; //For past months in HistoryActivity.java

    /**
     * A constant (Our max allowable int is 9,999,999 which is 7 place values)
     * that simplifies error handling.
     */
    private static final int MAX_BUDGET = 7;

    /**
     * Retrieves our database singleton
     */
    private final Database base = Database.Database();

    /**
     * The front-end components that are rendered and/or handle user interaction
     */
    private EditText categoryName;
    private EditText categoryBudget;
    private Button btnAdd;
    private CategoriesListAdapter myAdapter;
    private ListView categories;

    /**
     * Backend components that store the user's information
     */

    private MonthlyData monthlyData;
    private MonthlyData thisMonthsData;

    /**
     * Initializes on front-end renderings and handles user interaction
     *
     * @param savedInstanceState
     */
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_list);

        //Set up navBar components
        navBarSetUp();

        //Pull necessary information from incoming intents
        initializeWithAnIntent();

        //Initialize user's inputs and button
        initializeVariableComponents();

        //Set up the ListView and attach a custom adapter
        setUpList();

        //Handle the user clicking on a specific category
        handleListClicks();

        //Handle user clicking the '+' button
        handleAddClicks();
    }

    /**
     * Handle error checking when a user presses '+'.
     * If input is not valid, the user is notified.
     * Otherwise, the user's account is updated to include new category.
     */
    private void handleAddClicks() {
        // Set Event Handler to add items to the list
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ensure that both fields are filled.
                if (!categoryBudget.getText().toString().isEmpty() && !categoryName.getText().toString().isEmpty()) {

                    //Verify that max vale has not be reached.
                    if (categoryBudget.getText().toString().length() > MAX_BUDGET) {
                        Toast.makeText(getBaseContext(), "A category cannot have a budget greater than $9,999,999.", Toast.LENGTH_LONG).show();
                    } else {
                        // Create new item and update adapter
                        boolean creationSuccessful = monthlyData.createCategory(categoryName.getText().toString(), Integer.parseInt(categoryBudget.getText().toString()));
                        base.insertTotalBudget(monthlyData.getYear(), monthlyData.getIntMonth(), monthlyData.getTotalBudget());

                        // Verify that category was made
                        if (!creationSuccessful) {
                            Toast.makeText(getBaseContext(), "A budget with this name already exist", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // Displays a Toast message that lets the user know the category was successfully created
                            Toast.makeText(getBaseContext(), "Category successfully added.", Toast.LENGTH_SHORT).show();
                        }
                        //Clear inputs
                        categoryName.getText().clear();
                        categoryBudget.getText().clear();

                        //Update adapter and render
                        myAdapter.notifyDataSetChanged();
                    }

                } else {
                    // Insufficient number of filled fields results in an error warning.
                    Toast missingInformationWarning = Toast.makeText(getBaseContext(), "Please fill in category name and budget.", Toast.LENGTH_SHORT);
                    missingInformationWarning.show();
                }
            }
        });
    }

    /**
     * Handle the user's clicks on a specific category.
     * This takes the user to a new page where they can add expenses to their category.
     */
    private void handleListClicks() {

        //Set up ListView listener
        categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Identify the category that was clicked on
                Category currentItem = myAdapter.getItem(position);

                //Start new intent with current month, and the name of the category the user selected
                Intent i = new Intent(CategoriesListActivity.this, ExpensesListActivity.class);
                i.putExtra(ExpensesListActivity.MONTHLY_DATA_INTENT, monthlyData);
                assert currentItem != null;
                i.putExtra(ExpensesListActivity.CATEGORY_NAME_INTENT, currentItem.getName());
                startActivityForResult(i, 1);
            }
        });
    }

    /**
     * Set up navBar
     */
    private void navBarSetUp() {
        //create the nav bar view
        BottomNavigationView navView = findViewById(R.id.nav_view);
        //make all page names visible
        navView.setLabelVisibilityMode(1);
        //check the lists icon
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(navListener);
    }

    /**
     * Initialize front-end components like add button and category inputs
     */
    private void initializeVariableComponents() {
        // Bind element from XML file
        categoryName = findViewById(R.id.category_name_category);
        categoryBudget = findViewById(R.id.category_budget);
        btnAdd = findViewById(R.id.AddToList);
    }

    /**
     * Extracts the month that was passed in from MainActivity
     */
    private void initializeWithAnIntent() {
        //Get current month of the user
        Intent intent = getIntent();
        monthlyData = intent.getParcelableExtra(MONTHLY_DATA_INTENT);
    }

    /**
     * Sets up the ListView displaying the user's categories and attaches the adapter.
     */
    private void setUpList() {
        // Initialize List
        ArrayList<Category> arrayOfItems = monthlyData.getCategoriesAsArray();
        // Checklist Structure
        myAdapter = new CategoriesListAdapter(this, arrayOfItems, monthlyData);
        categories = findViewById(R.id.activity_categories_list_history_expenses);
        categories.setAdapter(myAdapter);
    }

    /**
     * Handle backpress from ExpenseListActivity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                //Instantiate the user's new monthly data (potential changes)
                monthlyData = data.getParcelableExtra(ExpensesListActivity.MONTHLY_DATA_INTENT);
                assert monthlyData != null; //error checking

                //Update adapter with the most recent information and attach to front-end rendering
                myAdapter = new CategoriesListAdapter(this, monthlyData.getCategoriesAsArray(), monthlyData);
                categories.setAdapter(myAdapter);
            }
        }
    }

    /**
     * Handles backpress from this page to MainActivity, pass updated MonthlyData.
     */
    @Override
    public void onBackPressed() {

        //Attach most recent MonthlyData object and start new activity to MainActivity
        Intent intent = new Intent();
        intent.putExtra(MONTHLY_DATA_INTENT, monthlyData);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    /**
     * Confirm a category being deleted.
     *
     * @param nameOfCategory The name of the category that was deleted
     */
    public void confirmDeletion(TextView nameOfCategory) {
        Toast.makeText(getBaseContext(), nameOfCategory.getText().toString() + " was deleted.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to contain the logic for navigation bar to navigate to the lists page
     */
    private void homePageHandler() {
        //create new intent
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        setResult(RESULT_OK, intent);
        //put extra monthly data into new intent
        intent.putExtra(MONTHLY_DATA_INTENT, monthlyData);
        startActivityForResult(intent, 1);
        //avoid shifting
        overridePendingTransition(0, 0);
    }

    /**
     * Helper method to contain the logic for navigation bar to navigate to the history page
     */
    private void historyPageHandler() {
        ValueEventListener Listener = new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent i = new Intent(getBaseContext(), HistoryActivity.class);
                //set up the date for monthly data
                Calendar today = Calendar.getInstance();
                int month = today.get(Calendar.MONTH);
                int year = today.get(Calendar.YEAR);
                //Retrieve the monthly data from the database
                thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                //put extra data into new intent
                i.putExtra(HISTORY_DATA_INTENT, thisMonthsData);
                //Add the past month's history (includes current)e
                i.putExtra(LIST_OF_MONTHS, base.getPastMonthSummary(dataSnapshot));
                startActivityForResult(i, 1);
                //avoid shifting
                overridePendingTransition(0, 0);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        };
        base.getMyRef().addListenerForSingleValueEvent(Listener);
    }

    /**
     * Helper method to contain the logic for navigation bar to navigate to the graph page
     */
    private void graphPageHandler() {
        base.getMyRef().addListenerForSingleValueEvent(new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent i = new Intent(getBaseContext(), GraphsActivity.class);
                //set up the date for monthly data
                Calendar today = Calendar.getInstance();
                int month = today.get(Calendar.MONTH);
                int year = today.get(Calendar.YEAR);
                //Retrieve the monthly data from the database
                thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                //Add the past month's history (includes current)e
                i.putExtra(Graphs_DATA_INTENT, thisMonthsData);
                //Add the past month's history (includes current)e
                i.putExtra(LIST_OF_MONTHS, base.getPastMonthSummary(dataSnapshot));
                startActivityForResult(i, 1);
                //avoid shifting
                overridePendingTransition(0, 0);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        });
    }

    // BOTTOM NAVIGATION
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            homePageHandler();
                            return true;
                        case R.id.navigation_lists:
                            return true;
                        case R.id.navigation_history:
                            historyPageHandler();
                            return true;
                        case R.id.navigation_graphs:
                            graphPageHandler();
                            return true;
                        case R.id.navigation_settings:
                            //create new intent for settings activity
                            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            return true;
                    }
                    return false;
                }
            };
}
