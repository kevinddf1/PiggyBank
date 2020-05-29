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
import com.example.cse110.Controller.history.HistoryDetailedItem;
import com.example.cse110.Model.Database;
import com.example.cse110.Model.history.HistoryDetailAdapter;
import com.example.cse110.R;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.cse110.Model.FormattingTool;
import com.example.cse110.View.CategoriesListActivity;
import com.example.cse110.View.graphs.GraphsActivity;
import com.example.cse110.View.MainActivity;
import com.example.cse110.View.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * A class representing the HistoryDetailed window, for PiggyBank.
 * When the user presses on the Category item, it will bring up this specific window.
 *
 * @author Peter Gonzalez
 * @version 4.27
 */
public class HistoryDetailedActivity extends AppCompatActivity {

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
     * For past months data
     */
    private static final String LIST_OF_MONTHS = "List of Months"; //For past months in HistoryActivity.java

    /**
     * The data structure to hold the HistoryDetailedItems.
     */
    private final ArrayList<HistoryDetailedItem> historyDetailedItems = new ArrayList<>();

    /**
     * The data structure to hold the Expense items for this specified category.
     */
    private ArrayList<Expense> myExpenseList;

    /**
     * Formatting tool to avoid redundancies.
     */
    private FormattingTool formattingTool = new FormattingTool();

    //For nav bar use
    private static final int NAV_BAR_INDEX = 3;
    private static final String Graphs_DATA_INTENT = "GraphsActivity monthlyData";
    private MonthlyData thisMonthsData;

    /**
     * Database singleton to get most up to date information from the user's account
     */
    private final Database base = Database.Database(); // create a Database object

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

        //Convert expenses into HistoryDetailedItems
        convertInfo();

        //Set up ListView and attach custom adapter
        setUpListView();

        //navBar handling
        setUpNavBar();
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
        String budgetRendering = "Budget: $" + formattingTool.formatIntMoneyString(currentCategory.getBudgetAsString()); //Avoid concatenation in setText
        TextView budget = findViewById(R.id.budget_display_history);
        budget.setText(budgetRendering);

        //Render the total expenses
        assert total != null;
        String expensesRendering = "Total Expenses: $" + formattingTool.formatMoneyString(total); //Avoid concatenation in setText
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
                base.insertMonthlydata(year, month);
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
                base.insertMonthlydata(year, month);
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
