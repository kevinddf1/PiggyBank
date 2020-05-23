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
import com.example.cse110.Controller.Settings;
import com.example.cse110.Model.history.HistoryCategoryAdapter;
import com.example.cse110.R;
import com.example.cse110.View.CategoriesListActivity;
import com.example.cse110.View.MainActivity;
import com.example.cse110.View.PieChartActivity;
import com.example.cse110.View.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


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
    private static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";
    private static final String SETTINGS_INTENT = "SettingsActivity settings";
    private static final String CATEGORY_NAME = "category_name";
    private static final String HISTORY_DETAILED_INTENT = "historyDetailedIntent";

    private Settings settings; //DEPRECATED
    //Display the month and year

    /**
     * The monthlyData object to pull data from, including Categories and Expenses
     *
     * @see #onCreate(Bundle)
     */
    private MonthlyData current_month;

    /**
     * The display of the list on the History page.
     */
    private ListView pastCategories;

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
     * Erdong's navbar
     */
    private void setUpNavBar() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(navListener);
    }

    /**
     * Retrieve the current month from the intent and render on screen
     */
    private void instantiateMonthAndRender() {
        //Retrieve passed in MonthlyData object and extract date
        Intent i = getIntent();
        current_month = i.getParcelableExtra(HISTORY_DATA_INTENT);
        settings = i.getParcelableExtra(SETTINGS_INTENT);

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
        pastCategories = findViewById(R.id.Categories);
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
            double totalExpenses = currentCategory.getTotalExpenses() / 100.00;
            //Create new HistoryItem and Add to List
            historyCategoryItemArrayList.add(new HistoryCategoryItem(currentCategory.getName(), currentCategory.getBudget(), totalExpenses));
        }
    }

    //ERDONG'S NAVBAR
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            Intent i = new Intent(getBaseContext(), MainActivity.class);
                            setResult(RESULT_OK, i);
                            i.putExtra(HISTORY_DATA_INTENT, current_month);
                            i.putExtra(MONTHLY_DATA_INTENT, current_month);
                            startActivityForResult(i, 1);
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.navigation_lists:
                            Intent intent = new Intent(getBaseContext(), CategoriesListActivity.class);
/*
                            // TODO: grab this from the database
                            if (thisMonthsData == null) {
                                Calendar today = Calendar.getInstance();
                                thisMonthsData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
                            }
*/
                            //intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);
                            intent.putExtra(HISTORY_DATA_INTENT, current_month);
                            intent.putExtra(MONTHLY_DATA_INTENT, current_month);
                            startActivityForResult(intent, 1);
                            overridePendingTransition(0, 0);
                            return true;

                        case R.id.navigation_history:
                            return true;
                        case R.id.navigation_graphs:
                            Intent inte = new Intent(getBaseContext(), PieChartActivity.class);
                            inte.putExtra(PIE_CHART_DATA_INTENT, current_month);
                            startActivityForResult(inte, 1);
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.navigation_settings:
                            Intent inten = new Intent(getBaseContext(), SettingsActivity.class);
                            setResult(RESULT_OK, inten);
                            if (settings == null) {
                                settings = new Settings();
                            }
                            inten.putExtra(SettingsActivity.SETTINGS_INTENT, settings);

                            startActivityForResult(inten, 1);
                            overridePendingTransition(0, 0);
                            return true;

                    }
                    return false;
                }
            };
}
