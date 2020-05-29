package com.example.cse110.View.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse110.Controller.Category;
import com.example.cse110.Controller.history.HistoryCategoryItem;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Model.Database;
import com.example.cse110.Model.history.HistoryCategoryAdapter;
import com.example.cse110.R;
import com.example.cse110.View.CategoriesListActivity;
import com.example.cse110.View.MainActivity;
import com.example.cse110.View.graphs.GraphsActivity;
import com.example.cse110.View.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A class representing the History window for PiggyBank.
 *
 * @author Peter Gonzalez
 * @version April 23
 */
public class HistoryCategoryActivity extends AppCompatActivity {
    /**
     * Key for pulling an object of monthlyData across navbar
     *
     * @see #onCreate(Bundle)
     */

    private static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    private static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    private static final String Graphs_DATA_INTENT = "GraphsActivity monthlyData";
    private static String CATEGORY_NAME = "category_name";
    private static String HISTORY_DETAILED_INTENT = "historyDetailedIntent";
    private static final int NAV_BAR_INDEX = 3;

    //Display the month and year

    /**
     * The monthlyData object to pull data from, including Categories and Expenses
     *
     * @see #onCreate(Bundle)
     */
    private MonthlyData current_month;
    private MonthlyData thisMonthsData;

    /**
     * The display of the list on the History page.
     */
    private ListView pastCategories;

    /**
     * Database singleton to get most up to date information from the user's account
     */
    private final Database base = Database.getInstance(); // create a Database object

    /**
     * The adapter to connect Category data to list display.
     *
     * @see HistoryCategoryAdapter
     */
    private HistoryCategoryAdapter historyCategoryAdapter;

    /**
     * The primary data structure to hold the information to display on History page.
     *
     * @see HistoryCategoryItem
     */
    private ArrayList<HistoryCategoryItem> historyCategoryItemArrayList;

    /**
     * Primary data structure to pull information from, gathered from monthlyData
     *
     * @see MonthlyData
     */
    private ArrayList<Category> categoryArrayList;

    /**
     * The only constructor for instantiating the History page.
     * Will pull information to fill all our field variables.
     *
     * @see AppCompatActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_category);

        //navBar handling
        setUpNavBar();

        //Extract selected month from intent and render on screen
        instantiateMonthAndRender();

        //Set up ListView w/ HistoryCategoryItems and attach custom adapter
        setUpListView();

        //Handle user clicks
        setUpClickHandling();
    }

    /**
     * Retrieve the current month from the intent and render on screen
     */
    private void instantiateMonthAndRender() {
        //Retrieve passed in MonthlyData object and extract date
        Intent i = getIntent();
        current_month = i.getParcelableExtra(HISTORY_DATA_INTENT);

        //Update our local variables to match
        assert current_month != null;

        //Rendering month and year display
        TextView month_year = findViewById(R.id.month_year_display);
        String monthYearRendering = current_month.getMonth() + " " + current_month.getYear(); //Should not concatenate in setTexts
        month_year.setText(monthYearRendering);
    }

    /**
     * Instantiate all categories in the ListView and attach adapter
     */
    private void setUpListView() {
        //Pull all categories associated w/ the current month
        categoryArrayList = current_month.getCategoriesAsArray();

        //Convert to HistoryCategoryItems
        fillInHistoryCategoryItemList();

        //Attach adapter
        historyCategoryAdapter = new HistoryCategoryAdapter(this, historyCategoryItemArrayList);
        pastCategories = (ListView) findViewById(R.id.history_category_expenses);
        pastCategories.setAdapter(historyCategoryAdapter);
    }

    /**
     * Set up an onItemClick for ListViews
     */
    private void setUpClickHandling() {
        //Set Up Clicking Handling
        pastCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * When a certain item is clicked in the list display, the user will be redirected to a detailed view of the chosen category.
             * @param parent The AdapterView for the ListView.
             * @param view The View for the HistoryItem.
             * @param position The position of the item in the list.
             * @param id The particular id of the view.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Determine the category selected
                HistoryCategoryItem currentItem = historyCategoryAdapter.getItem(position);

                //Start new intent to send into HistoryDetailedActivity
                Intent i = new Intent(getBaseContext(), HistoryDetailedActivity.class);

                //Attach necessary info to the intent and start new activity
                assert currentItem != null; // error handling
                i.putExtra(CATEGORY_NAME, currentItem.getName());
                i.putExtra("total_expenses", currentItem.getFormattedTotalExpenses());
                i.putExtra(HISTORY_DETAILED_INTENT, current_month);
                startActivityForResult(i, 1);
            }
        });
    }


    /**
     * Helper method to pull data from the list of Categories and populate historyItemArrayList.
     */
    private void fillInHistoryCategoryItemList() {
        //Initiate HistoryItemArrayList
        historyCategoryItemArrayList = new ArrayList<>();
        //Iterate through categoryArrayList to create a HistoryItem (name, budget, total expenses)
        for (Category currentCategory : categoryArrayList) {
            double totalExpenses = currentCategory.getTotalExpenses() / 100.00; //Divided by 100 because some data may have been changed when re-instantiating catory
            //Create new HistoryItem and Add to List
            historyCategoryItemArrayList.add(new HistoryCategoryItem(currentCategory.getName(), currentCategory.getBudget(), totalExpenses));
        }
    }

    /**
     * The user shall enter any page through clicking the icon in this nav bar
     */
    private void setUpNavBar() {
        // Create the bottom navigation bar
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // set the label to be visible
        navView.setLabelVisibilityMode(1);
        Menu menu = navView.getMenu();
        // Check the icon
        MenuItem menuItem = menu.getItem(NAV_BAR_INDEX);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(navListener);
    }

    /**
     * Helper method to contain the logic for navigation bar to navigate to the home page
     */
    private void homePageHandler() {
        ValueEventListener Listener = new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified
            // database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //create a new intent for home page activity
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                //set up the date for monthly data
                Calendar today = Calendar.getInstance();
                int month = today.get(Calendar.MONTH);
                int year = today.get(Calendar.YEAR);
                base.insertMonthlyData(year, month);
                // Retrieve the current data from data base
                thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                // put extra data for categories and expenses
                intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);
                startActivityForResult(intent, 1);
                // avoid shifting
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
     * Helper method to contain the logic for navigation bar to navigate to the lists page
     */
    private void listsPageHandler() {
         /* Read from the database
        / Read data once: addListenerForSingleValueEvent() method triggers once and then does not
        trigger again.
        / This is useful for data that only needs to be loaded once and isn't expected to change
        frequently or require active listening.
        */
        ValueEventListener Listener = new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified
            // database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent = new Intent(getBaseContext(), CategoriesListActivity.class);
                //set up the date for monthly data
                Calendar today = Calendar.getInstance();
                int month = today.get(Calendar.MONTH);
                int year = today.get(Calendar.YEAR);
                base.insertMonthlyData(year, month);
                //Retrieve the monthly data from the database
                thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                //put extra data into new intent
                intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);
                startActivityForResult(intent, 1);
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
            //The onDataChange() method is called every time data is changed at the specified
            // database reference, including changes to children.
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

    //Helper method to control the functionality of bottom navigation bar
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // switch statement to handle all the icons in the bottom nav bar
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            homePageHandler();
                            return true;
                        case R.id.navigation_lists:
                            listsPageHandler();
                            return true;
                        case R.id.navigation_history:
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
