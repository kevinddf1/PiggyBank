package com.example.cse110.View;

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

import com.example.cse110.Controller.Settings;
import com.example.cse110.Controller.Category;
import com.example.cse110.Controller.Expense;
import com.example.cse110.Controller.HistoryCategoryItem;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Model.HistoryCategoryAdapter;
import com.example.cse110.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;




/**
 * A class representing the History window for PiggyBank.
 * @author Peter Gonzalez
 * @version April 23
 *
 */
public class HistoryCategoryActivity extends AppCompatActivity {
    /**
     * Key for pulling an object of monthlyData in the HistoryDetailedActivity
     * @see #onCreate(Bundle)
     */
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";
    public static final String SETTINGS_INTENT = "SettingsActivity settings";

    private MonthlyData thisMonthsData;




    private static String CATEGORY_NAME = "category_name";
    private static String HISTORY_DETAILED_INTENT = "historyDetailedIntent";

    private Settings settings;
    //Display the month and year
    /**
     * The text display for the current month and year
     * @see #onCreate(Bundle)
     */
    private TextView month_year;

    /**
     * The monthlyData object to pull data from, including Categories and Expenses
     * @see #onCreate(Bundle)
     */
    private MonthlyData current_month;

    //Instantiate the list's objects

    /**
     * The display of the list on the History page.
     */
    private ListView pastCategories;

    /**
     * The adapter to connect Category data to list display.
     * @see HistoryCategoryAdapter
     */
    private HistoryCategoryAdapter historyCategoryAdapter;

    /**
     * The primary data structure to hold the information to display on History page.
     * @see HistoryCategoryItem
     */
    private ArrayList<HistoryCategoryItem> historyCategoryItemArrayList;

    //Instantiate the month's categories

    /**
     * Primary data structure to pull information from, gathered from monthlyData
     * @see MonthlyData
     */
    private ArrayList<Category> categoryArrayList;

    /**
     * The only constructor for instantiating the History page.
     * Will pull information to fill all our field variables.
     * @see AppCompatActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_category);
;

        //Retrieve passed in MonthlyData object and extract date/categories

        Intent i = getIntent();
        current_month = i.getParcelableExtra(HISTORY_DATA_INTENT);

        settings = i.getParcelableExtra(SETTINGS_INTENT);

        //Update our local variables to match
        assert current_month != null;
        categoryArrayList = current_month.getCategoriesAsArray();
        month_year = (TextView) findViewById(R.id.month_year_display);
        month_year.setText(current_month.getMonth() + " " + current_month.getYear());

        //Set up our list
        fillInHistoryItemArrayList();
        historyCategoryAdapter = new HistoryCategoryAdapter(this, historyCategoryItemArrayList);
        pastCategories = (ListView) findViewById(R.id.Categories);
        pastCategories.setAdapter(historyCategoryAdapter);

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
                HistoryCategoryItem currentItem = historyCategoryAdapter.getItem(position);

                Intent i = new Intent(getBaseContext(), HistoryDetailedActivity.class);
                i.putExtra(HISTORY_DETAILED_INTENT, current_month);
                i.putExtra(CATEGORY_NAME, currentItem.getName());
                i.putExtra("total_expenses", currentItem.getFormattedTotalExpenses());
                startActivityForResult(i, 1);
            }
        });




    }

    /**
     * Helper method to pull data from the list of Categories and populate historyItemArrayList.
     */
    private void fillInHistoryItemArrayList(){
        //Initiate HistoryItemArrayList
        historyCategoryItemArrayList = new ArrayList<>();
        //Iterate through categoryArrayList to create a HistoryItem (name, budget, total expenses)
        for(Category currentCategory : categoryArrayList){
            double totalExpenses = 0;

            //Add up all the expenses for the category.
            //CURRENT BUG
            for(Expense currentExpense : currentCategory.getExpenses()){
                totalExpenses = totalExpenses + (double)currentExpense.getCost();
            }


            totalExpenses = totalExpenses/100;
            //Create new HistoryItem and Add to List
            historyCategoryItemArrayList.add(new HistoryCategoryItem(currentCategory.getName(), currentCategory.getBudget(), totalExpenses));
        }
    }


}
