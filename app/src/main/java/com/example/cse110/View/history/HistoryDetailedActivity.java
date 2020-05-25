package com.example.cse110.View.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse110.Controller.Category;
import com.example.cse110.Controller.Expense;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Controller.Settings;
import com.example.cse110.Controller.history.HistoryDetailedItem;
import com.example.cse110.Model.Database;
import com.example.cse110.Model.history.HistoryDetailAdapter;
import com.example.cse110.R;
import com.example.cse110.View.CategoriesListActivity;
import com.example.cse110.View.MainActivity;
import com.example.cse110.View.PieChartActivity;
import com.example.cse110.View.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A class representing the HistoryDetailed window, for PiggyBank.
 * When the user presses on the Category item, it will bring up this specific window.
 *
 * @author Peter Gonzalez
 * @version 4.27
 */
public class HistoryDetailedActivity extends AppCompatActivity {

    //Constants to help display formatting
    private static final int DISTANCE_FOR_THOUSANDS_COMMA = 6;
    private static final int DISTANCE_FOR_MILLIONS_COMMA = 9;
    private static final int SIZE_LESS_THAN_THOUSANDS = 6;
    private static final int SIZE_LESS_THAN_MILLIONS = 9;
    private static final int BEGIN_INDEX = 0;
    private static final int DISTANCE_FOR_MILLIONS_COMMA_NO_DECIMALS = 6;
    private static final int DISTANCE_FOR_THOUSANDS_COMMA_NO_DECIMALS = 3;
    private static final int SIZE_LESS_THAN_THOUSANDS_NO_DECIMALS = 3;
    private static final int SIZE_LESS_THAN_MILLIONS_NO_DECIMALS = 6;

    //nav bar need
    private static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    private static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    private static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";
    private MonthlyData current_month;
    private Settings settings; //DEPRECATED
    private MonthlyData thisMonthsData;
    private final Database base = Database.Database(); // create a Database object

    /**
     * The Category that was selected to display details of the Category.
     */
    private Category currentCategory;

    /**
     * The key to retrieve the Category's information.
     */
    private static final String CATEGORY_NAME = "category_name";

    /**
     * The key to retrieve the MonthlyData object from the HistoryActivity.
     *
     * @see HistoryActivity
     */
    private static final String HISTORY_DETAILED_INTENT = "historyDetailedIntent";

    /**
     * The data structure to hold the HistoryDetailedItems.
     */
    private final ArrayList<HistoryDetailedItem> historyDetailedItems = new ArrayList<>();

    /**
     * The data structure to hold the Expense items for this specified category.
     */
    private ArrayList<Expense> myExpenseList;


    /**
     * The method for instantiating the HistoryDetailedIem page.
     * Will pull information to fill all our field variables.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detailed);

        //Render static displays like MONTH YEAR BUDGET EXPENSES
        renderStaticInfo();

        //navBar handling
        setUpNavBar();
        //Convert expenses into HistoryDetailedItems
        convertInfo();

        //Set up ListView and attach custom adapter
        setUpListView();

    }

    /**
     * Converts ExpenseItems into HistoryDetailedItems for rendering purposes
     */
    private void convertInfo() {
        //Retrieve all expenses associated w/ current category
        myExpenseList = currentCategory.getExpenses();

        //Convert into HistoryDetailItems
        fillInHistoryDetailedItemArrayList();
    }

    /**
     * Erdong's navbar
     * The user shall enter any page through clicking the icon in this nav bar
     */
    private void setUpNavBar() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(1);
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(navListener);
    }

    /**
     * Attaches adapter to ListView w/ all data instantiated beforehand
     */
    private void setUpListView() {
        //Attach the adapter to fill in all the necessary expenses
        HistoryDetailAdapter historyDetailAdapter = new HistoryDetailAdapter(this, historyDetailedItems);
        ListView expenseDetails = findViewById(R.id.history_expenses);
        expenseDetails.setAdapter(historyDetailAdapter);
    }

    /**
     * Handles rendering all info not contained in the ListView
     */
    private void renderStaticInfo() {
        //Retrieve all information passed in
        Intent i = getIntent();
        MonthlyData current_month = i.getParcelableExtra(HISTORY_DETAILED_INTENT);
        String total = i.getStringExtra("total_expenses");
        assert current_month != null;
        currentCategory = current_month.getCategory(i.getStringExtra(CATEGORY_NAME));

        // Render the budget
        String budgetRendering = "Budget: $" + formatIntMoneyString(currentCategory.getBudgetAsString()); //Avoid concatenation in setText
        TextView budget = findViewById(R.id.budget_display_history);
        budget.setText(budgetRendering);

        //Render the total expenses
        assert total != null;
        String expensesRendering = "Total Expenses: $" + formatMoneyString(total); //Avoid concatenation in setText
        TextView totalExpenses = findViewById(R.id.total_expenses);
        totalExpenses.setText(expensesRendering);


        //Render the month and year
        TextView month_year = findViewById(R.id.date_display);
        String monthAndYearRendering = current_month.getMonth() + " " + current_month.getYear();//Avoid concatenation in setText
        month_year.setText(monthAndYearRendering);

        //Render the category name
        TextView category_name = findViewById(R.id.category_name);
        category_name.setText(currentCategory.getName());
    }

    /**
     * Helper method to pull data from the Expenses list to populate historyDetailedItems
     */
    private void fillInHistoryDetailedItemArrayList() {

        for (Expense currentExpense : myExpenseList) {
            String dividedExpenseString = Double.toString(currentExpense.getCost()/100.00); //Divided by 100 to adjust to perhaps Database manipulation
            historyDetailedItems.add(new HistoryDetailedItem(currentExpense.getName(), dividedExpenseString));
        }
    }

    /**
     * Handle formatting for numbers containing decimals
     *
     * @param valueToFormat The comma free string to format
     * @return A newly formatted string w/ commas in the correct place
     */
    private String formatMoneyString(String valueToFormat) {
        //Set up local constant for where commas should be
        int thousandsComma = valueToFormat.length() - DISTANCE_FOR_THOUSANDS_COMMA;
        int millionsComma = valueToFormat.length() - DISTANCE_FOR_MILLIONS_COMMA;

        //Return unchanged if necessary or add commas in designated indices
        if (valueToFormat.length() <= SIZE_LESS_THAN_THOUSANDS) {
            return valueToFormat;
        } else if (valueToFormat.length() <= SIZE_LESS_THAN_MILLIONS) {
            return valueToFormat.substring(BEGIN_INDEX, thousandsComma) + "," + valueToFormat.substring(thousandsComma);
        }
        return valueToFormat.substring(BEGIN_INDEX, millionsComma) + "," + valueToFormat.substring(millionsComma, thousandsComma) + "," + valueToFormat.substring(thousandsComma);
    }

    /**
     * Handle formatting for numbers w/o decimals
     *
     * @param valueToFormat The comma free string to format
     * @return A newly formatted string w/ commas in the correct place
     */
    private String formatIntMoneyString(String valueToFormat) {
        //Set up local constant for where commas should be
        int thousandsComma = valueToFormat.length() - DISTANCE_FOR_THOUSANDS_COMMA_NO_DECIMALS;
        int millionsComma = valueToFormat.length() - DISTANCE_FOR_MILLIONS_COMMA_NO_DECIMALS;

        //Return unchanged if necessary or add commas in designated indices
        if (valueToFormat.length() <= SIZE_LESS_THAN_THOUSANDS_NO_DECIMALS) {
            return valueToFormat;
        } else if (valueToFormat.length() <= SIZE_LESS_THAN_MILLIONS_NO_DECIMALS) {
            return valueToFormat.substring(BEGIN_INDEX, thousandsComma) + "," + valueToFormat.substring(thousandsComma);
        }
        return valueToFormat.substring(BEGIN_INDEX, millionsComma) + "," + valueToFormat.substring(millionsComma, thousandsComma) + "," + valueToFormat.substring(thousandsComma);
    }

    //ERDONG'S NAVBAR
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            ValueEventListener Listener1 = new ValueEventListener() {
                                //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);

                                    Calendar today = Calendar.getInstance();
                                    int month = today.get(Calendar.MONTH);
                                    int year = today.get(Calendar.YEAR);
                                    base.insertMonthlydata(year, month);

                                    //pastMonthsData = base.RetrieveDataforPast(dataSnapshot, pastMonthsData, year, month);
                                    thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);

                                    intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);
                                    if (settings == null) {
                                        settings = new Settings();
                                    }
                                    intent.putExtra(CategoriesListActivity.SETTINGS_INTENT, settings);
                                    startActivityForResult(intent, 1);
                                    overridePendingTransition(0, 0);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Failed to read value
                                }
                            };
                            base.getMyRef().addListenerForSingleValueEvent(Listener1);
                            return true;
                        case R.id.navigation_lists:
                         /* Read from the database
                               Read data once: addListenerForSingleValueEvent() method triggers once and then does not trigger again.
                               This is useful for data that only needs to be loaded once and isn't expected to change frequently or require active listening.
                            */
                            ValueEventListener Listener = new ValueEventListener() {
                                //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Intent intent = new Intent(getBaseContext(), CategoriesListActivity.class);
                                    Calendar today = Calendar.getInstance();
                                    int month = today.get(Calendar.MONTH);
                                    int year = today.get(Calendar.YEAR);
                                    base.insertMonthlydata(year, month);

                                    //pastMonthsData = base.RetrieveDataforPast(dataSnapshot, pastMonthsData, year, month);
                                    thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);

                                    intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);
                                    if (settings == null) {
                                        settings = new Settings();
                                    }
                                    intent.putExtra(CategoriesListActivity.SETTINGS_INTENT, settings);
                                    startActivityForResult(intent, 1);
                                    overridePendingTransition(0, 0);

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Failed to read value
                                }
                            };
                            base.getMyRef().addListenerForSingleValueEvent(Listener);
                            return true;

                        case R.id.navigation_history:
                            return true;

                        case R.id.navigation_graphs:
                            base.getMyRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Intent i = new Intent(getBaseContext(), PieChartActivity.class);

                                    Calendar today = Calendar.getInstance();
                                    int month = today.get(Calendar.MONTH);
                                    int year = today.get(Calendar.YEAR);

                                    thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                                    i.putExtra(PIE_CHART_DATA_INTENT, thisMonthsData);
                                    startActivityForResult(i, 1);
                                    overridePendingTransition(0, 0);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Failed to read value
                                }
                            });
                            return true;
                        case R.id.navigation_settings:
                            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                            if (settings == null) {
                                settings = new Settings();
                            }
                            intent.putExtra(SettingsActivity.SETTINGS_INTENT, settings);

                            startActivityForResult(intent, 1);
                            overridePendingTransition(0, 0);
                            return true;
                    }
                    return false;
                }
            };
}
