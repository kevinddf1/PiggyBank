package com.example.cse110.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.cse110.Controller.Settings;
import com.example.cse110.Controller.Category;
import com.example.cse110.Controller.Expense;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Model.Database;
import com.example.cse110.R;
import com.example.cse110.View.history.HistoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A class representing the pie chart for PiggyBank.
 * When user presses: See Graph, this page will appear.
 * @author Fan Ding
 * @version April 28
 *
 */
public class PieChartActivity extends AppCompatActivity {

    AnyChartView anyChartView;
    List<String> cateArrayList =new ArrayList<>();
    List<Integer> totalExpenseArrayList = new ArrayList<>();
    // create a Database object
    private Database base = Database.Database();
    /**
     * Key for pulling an object of monthlyData in the HistoryDetailedActivity
     * @see #onCreate(Bundle)
     */
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String SETTINGS_INTENT = "SettingsActivity settings";
    private static final String LIST_OF_MONTHS = "List of Months";
    public static final int NAV_BAR_INDEX = 2;
    private MonthlyData current_month;
    private MonthlyData thisMonthsData;

    private Settings settings;

    private ArrayList<Category> categoryArrayList;

    /**
     * The only constructor for instantiating the pie chart page
     * @see AppCompatActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        anyChartView=findViewById(R.id.any_chart_view);
        //navBar handling
        setUpNavBar();


        //Retrieve passed in MonthlyData object and extract date/categories
        Intent intent = getIntent();
        current_month = intent.getParcelableExtra(PIE_CHART_DATA_INTENT);
        settings = intent.getParcelableExtra(SETTINGS_INTENT);

        categoryArrayList= current_month.getCategoriesAsArray();
        for (int i=0; i<categoryArrayList.size();i++){
            Category c = categoryArrayList.get(i);
            Log.d("what", c.getName());
            cateArrayList.add(c.getName());
            Log.d("price", formatMoneyString(Double.toString(getTotalExpense(c)/100.00)));
            totalExpenseArrayList.add(getTotalExpense(c));
        }

        setupPieChart();
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
     * Gets the total expenses from each expense in the category
     *
     * @param c the category we want the total expense
     * @return
     */
    private int getTotalExpense(Category c) {
        int ret=0;
        ArrayList<Expense> expenseArray= c.getExpenses();
        for (int i=0; i<expenseArray.size(); i++){
            ret+=expenseArray.get(i).getCost();
        }

        return ret;
    }

    /**
     * Sets up the PieChart by filling in the values for each data entry
     */
    public void setupPieChart(){

        Pie pie= AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i=0; i<cateArrayList.size(); i++){
            dataEntries.add(new ValueDataEntry(cateArrayList.get(i), totalExpenseArrayList.get(i)));
        }

        pie.data(dataEntries);
        anyChartView.setChart(pie);
    }

    /**
     * Properly formats the money strings based on the values we get from the database
     *
     * @param valueToFormat the value we want to format to a money string
     * @return
     */
    private String formatMoneyString(String valueToFormat){
        // Add formatting for whole numbers
        if(valueToFormat.indexOf('.') == -1){
            valueToFormat = valueToFormat.concat(".00");
        }else{
            //Ensure only valid input
            int costLength = valueToFormat.length();
            int decimalPlace = valueToFormat.indexOf(".");

            // If the user inputs a number formatted as "<num>.", appends a 00 after the decimal
            if (costLength - decimalPlace == 1) {
                valueToFormat = valueToFormat.substring(0, decimalPlace + 1) +  "00";
            }
            // If the user inputs a number formatted as "<num>.1", where 1 could be any number,
            // appends a 0 to the end
            else if (costLength - decimalPlace == 2) {
                valueToFormat = valueToFormat.substring(0, decimalPlace + 1 + 1) + "0";
            }
            // If the user inputs a number with >= 2 decimal places, only displays up to 2
            else {
                valueToFormat = valueToFormat.substring(0, valueToFormat.indexOf(".") + 2 + 1);
            }
        }

        int hundredthComma = valueToFormat.length() - 6;
        int thousandthComma = valueToFormat.length() - 9;
        if(valueToFormat.length() <= 6){
            return valueToFormat;
        }else if(valueToFormat.length() <= 9){
            return valueToFormat.substring(0, hundredthComma) + "," + valueToFormat.substring(hundredthComma);
        }
        return valueToFormat.substring(0, thousandthComma) + "," + valueToFormat.substring(thousandthComma , hundredthComma) + "," + valueToFormat.substring(hundredthComma );
    }

    /**
     * Helper method to contain the logic for navigation bar to navigate to the home page
     */
    private void homePageHandler() {
        //create the new intent for new activity
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        setResult(RESULT_OK, intent);
        //put extra monthly data intent
        intent.putExtra(MONTHLY_DATA_INTENT, current_month);
        startActivityForResult(intent, 1);
        //avoid shifting
        overridePendingTransition(0, 0);
    }

    /**
     * Helper method to contain the logic for navigation bar to navigate to the lists page
     */
    private void listsPageHandler() {
        Intent in = new Intent(getBaseContext(), CategoriesListActivity.class);
        // put extra data for categories and expenses
        in.putExtra(HISTORY_DATA_INTENT, current_month);
        in.putExtra(MONTHLY_DATA_INTENT, current_month);
        startActivityForResult(in, 1);
        // avoid shifting
        overridePendingTransition(0, 0);
    }

    /**
     * Helper method to contain the logic for navigation bar to navigate to the lists page
     */
    private void historyPageHandler() {
        ValueEventListener Listener = new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified
            // database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent i = new Intent(getBaseContext(), HistoryActivity.class);
                // set the calendar
                Calendar today = Calendar.getInstance();
                int month = today.get(Calendar.MONTH);
                int year = today.get(Calendar.YEAR);
                // Retrieve the current data from data base
                thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                // put extra data for categories and expenses
                i.putExtra(HISTORY_DATA_INTENT, thisMonthsData);
                i.putExtra(LIST_OF_MONTHS, base.getPastMonthSummary(dataSnapshot));
                startActivityForResult(i, 1);
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
     * Helper method to contain the logic for navigation bar to navigate to the settings page
     */
    private void settingsPageHandler() {
        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
        if (settings == null) {
            settings = new Settings();
        }
        // put the extra settings intent data
        intent.putExtra(SettingsActivity.SETTINGS_INTENT, settings);
        startActivityForResult(intent, 1);
        // avoid shifting
        overridePendingTransition(0, 0);
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
                            historyPageHandler();
                            return true;
                        case R.id.navigation_graphs:
                            return true;
                        case R.id.navigation_settings:
                            settingsPageHandler();
                            return true;
                    }
                    return false;
                }
            };
}
