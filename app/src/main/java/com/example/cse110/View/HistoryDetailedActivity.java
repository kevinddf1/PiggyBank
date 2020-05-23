package com.example.cse110.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cse110.Controller.Category;
import com.example.cse110.Controller.Expense;
import com.example.cse110.Controller.HistoryDetailedItem;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Controller.Settings;
import com.example.cse110.Model.Database;
import com.example.cse110.Model.HistoryDetailAdapter;
import com.example.cse110.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A class representing the HistoryDetailed window, for PiggyBank.
 * When the user presses on the Category item, it will bring up this specific window.
 * @author Peter Gonzalez
 * @version 4.27
 */
public class HistoryDetailedActivity extends AppCompatActivity {


    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String SETTINGS_INTENT = "SettingsActivity settings";

    private MonthlyData monthlyData;
    private Database base = Database.Database(); // create a Database object
    private Settings settings;
    private MonthlyData thisMonthsData;
    /**
     * All the display data to instantiate.
     */
    private TextView category_name,month_year, budget, totalExpenses;

    /**
     * The main object that will hold the month's information, including the expenses for the Category selected.
     * @see MonthlyData
     */
    private MonthlyData current_month;

    /**
     * The Category that was selected to display details of the Category.
     */
    private Category currentCategory;

    /**
     * The key to retrieve the Category's information.
     */
    private static String CATEGORY_NAME = "category_name";

    /**
     * The key to retrieve the MonthlyData object from the HistoryActivity.
     * @see HistoryActivity
     */
    private static String HISTORY_DETAILED_INTENT = "historyDetailedIntent";

    /**
     * The ListView to hold the data to display.
     */
    private ListView expenseDetails;

    /**
     * The data structure to hold the HistoryDetailedItems.
     */
    private ArrayList<HistoryDetailedItem> historyDetailedItems = new ArrayList<>();

    /**
     * The data structure to hold the Expense items for this specified category.
     */
    private ArrayList<Expense> myExpenseList;

    /**
     * The adapter to connect the ArrayList of HistoryDetailedItem  to the intended ListView.
     */
    private HistoryDetailAdapter historyDetailAdapter;


    /**
     * The method for instantiating the HistoryDetailedIem page.
     * Will pull information to fill all our field variables.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detailed);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(1);
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(navListener);
        Intent i = getIntent();
        settings = i.getParcelableExtra(SETTINGS_INTENT);
        monthlyData = i.getParcelableExtra(MONTHLY_DATA_INTENT);
        current_month = i.getParcelableExtra(HISTORY_DETAILED_INTENT);
        String total = i.getStringExtra("total_expenses");

        currentCategory = current_month.getCategory(i.getStringExtra(CATEGORY_NAME));

        myExpenseList = currentCategory.getExpenses();

        //Display the month we are currently analyzing
        month_year = (TextView) findViewById(R.id.date_display);
        month_year.setText(current_month.getMonth() + " " + current_month.getYear() );
        category_name = (TextView) findViewById(R.id.category_name);
        category_name.setText(currentCategory.getName());
        budget = (TextView) findViewById(R.id.budget_display);

        // The details for the budget and total expenditures.
        budget.setText("Budget: $" + formatIntMoneyString(currentCategory.getBudgetAsString()));
        totalExpenses = (TextView) findViewById(R.id.total_expenses);
        totalExpenses.setText("Total Expenditure: $" + formatMoneyString(total));



        //Set up our list
        fillInHistoryDetailedItemArrayList();

        //The adapter to fill in all the necessary list.
        historyDetailAdapter = new HistoryDetailAdapter(this,historyDetailedItems);
        expenseDetails = (ListView) findViewById(R.id.expenses);
        expenseDetails.setAdapter(historyDetailAdapter);






    }

    /**
     * Helper method to pull data from the Expenses list to populate historyDetailedItems
     */
    public void fillInHistoryDetailedItemArrayList(){

        for (Expense currentExpense : myExpenseList){
            historyDetailedItems.add(new HistoryDetailedItem(currentExpense.getName(), currentExpense.getCostAsString()));
        }
    }

    private String formatMoneyString(String valueToFormat){
        int hundredthComma = valueToFormat.length() - 6;
        int thousandthComma = valueToFormat.length() - 9;
        if(valueToFormat.length() <= 6){
            return valueToFormat;
        }else if(valueToFormat.length() <= 9){
            return valueToFormat.substring(0, hundredthComma) + "," + valueToFormat.substring(hundredthComma);
        }
        return valueToFormat.substring(0, thousandthComma) + "," + valueToFormat.substring(thousandthComma , hundredthComma) + "," + valueToFormat.substring(hundredthComma );
    }

    private String formatIntMoneyString(String valueToFormat){
        int hundredthComma = valueToFormat.length() - 3;
        int thousandthComma = valueToFormat.length() - 6;

        if (valueToFormat.length() <= 3){
            return  valueToFormat;
        }else if (valueToFormat.length() <= 6){
            return valueToFormat.substring(0, hundredthComma) + "," + valueToFormat.substring(hundredthComma);
        }
        return valueToFormat.substring(0, thousandthComma) + "," + valueToFormat.substring(thousandthComma , hundredthComma) + "," + valueToFormat.substring(hundredthComma );
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
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
                                    monthlyData = base.RetrieveDataCurrent(dataSnapshot, monthlyData, year, month);

                                    intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, monthlyData);
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
                                    monthlyData = base.RetrieveDataCurrent(dataSnapshot, monthlyData, year, month);

                                    intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, monthlyData);
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
