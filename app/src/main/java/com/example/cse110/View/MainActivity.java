package com.example.cse110.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Model.Database;
import com.example.cse110.Model.FormattingTool;
import com.example.cse110.R;
import com.example.cse110.View.graphs.GraphsActivity;
import com.example.cse110.View.history.HistoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * The  main menu page that allows the user to add expense or track spending.
 * @author The Technical Team
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Icon setup
     */
    private LinearLayout expenseListButton;
    private LinearLayout historyButton;
    private LinearLayout GraphsButton;
    private LinearLayout settingsButton;

    /**
     * Keys for pulling/pushing data between pages
     */
    private static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    private static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    private static final String Graphs_DATA_INTENT = "GraphsActivity monthlyData";
    private static final String LIST_OF_MONTHS = "List of Months"; //For past months in HistoryActivity.java

    /**
     * Core store element for the user's data
     */
    private MonthlyData thisMonthsData;

    /**
     * Allows us to pull most up to date info from the database.
     */
    private final Database base = Database.Database(); // create a Database object

    /**
     * Formatting tool to avoid redundancies.
     */
    private final FormattingTool formattingTool = new FormattingTool();

    /**
     * TextViews to display budget and total expenses
     */
    private TextView totalBudgetDisplay;
    private TextView totalExpenseDisplay;

    /**
     * Display all user's total budget and expenses, as well as gives them options.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        //Check if this a month should be re-instantiated
        Intent intent = getIntent();
        thisMonthsData = intent.getParcelableExtra(MONTHLY_DATA_INTENT);

        //set up nav bar
        setUpNavBar();


        //If we are called internally, display local data
        if (thisMonthsData != null) {

            //Render total budget
            totalBudgetDisplay = findViewById(R.id.totalBudget);
            String budgetRendering = "Total Budget: $" + formattingTool.formatIntMoneyString(Long.toString(thisMonthsData.getTotalBudget()));
            totalBudgetDisplay.setText(budgetRendering);

            //Render total expenses
            totalExpenseDisplay = findViewById(R.id.totalExpenses);
            String expenseRendering = "Total Expenses: $" + formattingTool.formatMoneyString(formattingTool.formatDecimal(Long.toString(thisMonthsData.getTotalExpensesAsCents() / 100)));
            totalExpenseDisplay.setText(expenseRendering);
        }
        //Upon first login, use the bundle set up by the database
        else {

            // Get Bundle object that contain the array
            Bundle b = this.getIntent().getExtras();
            assert b != null;
            String[] list = b.getStringArray("Total Budget and Expense");

            //Render total budget
            totalBudgetDisplay = findViewById(R.id.totalBudget);
            assert list != null;
            String budgetRendering = "Total Budget: $" + formattingTool.formatIntMoneyString(list[0]);
            totalBudgetDisplay.setText(budgetRendering);

            //Render total expenses
            totalExpenseDisplay = findViewById(R.id.totalExpenses);
            String expensesRendering = "Total Expenses: $" + formattingTool.formatMoneyString(formattingTool.formatDecimal(Double.toString(Long.parseLong(list[1]) / 100.00)));
            totalExpenseDisplay.setText(expensesRendering);
        }

        //Bind button to go to expense list
        expenseListButton = findViewById(R.id.ExpensesButton);
        expenseListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExpensesCLick(v);

            }
        });

        //Set up history icon
        historyButton = findViewById(R.id.HistoryButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHistoryClick(v);
            }
        });

        //Set up graphs icon
        GraphsButton = findViewById(R.id.GraphsButton);
        GraphsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGraphsClick(v);
            }
        });


        //Set up settings icon
        settingsButton = findViewById(R.id.SettingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettingsClick(v);
            }
        });
    }

    /**
     * When the expenses icon is clicked, the request is directed here.
     * @param v
     */
    public void onExpensesCLick(View v) {
        listsPageHandler();
    }

    /**
     * When the history icon is clicked, the user is directed here.
     * @param v
     */
    private void onHistoryClick(View v) {
        historyPageHandler();
    }

    /**
     * When the graph icon is clicked, the user is directed here.
     * @param v
     */
    private void onGraphsClick(View v) {
        graphPageHandler();
    }


    /**
     * Settings clock goes straight to settings page
     * @param v
     */
    private void onSettingsClick(View v) {
        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(intent);
    }


    /**
     * Executes upon backpress from CategoryListActivity.
     * @param requestCode
     * @param resultCode
     * @param data The intent containing the most recent monthly data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                //Get most recent data
                thisMonthsData = data.getParcelableExtra(CategoriesListActivity.MONTHLY_DATA_INTENT);

                //Render total budget
                totalBudgetDisplay = findViewById(R.id.totalBudget);
                String budgetRendering = "Total Budget: $" + formattingTool.formatIntMoneyString(Long.toString(thisMonthsData.getTotalBudget()));
                totalBudgetDisplay.setText(budgetRendering);

                //Render total expenses
                totalExpenseDisplay = findViewById(R.id.totalExpenses);
                String expenseRendering = "Total Expenses: $" + formattingTool.formatMoneyString(formattingTool.formatDecimal(Double.toString(thisMonthsData.getTotalExpensesAsCents() / 100.00)));
                totalExpenseDisplay.setText(expenseRendering);
            }
        }
    }

    /**
     * The user shall enter any page through clicking the icon in this nav bar
     */
    private void setUpNavBar() {
        // Create the bottom navigation bar
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // make all icons' names visible
        navView.setLabelVisibilityMode(1);

        // Check the icon
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(navListener);
    }

    /**
     * Helper method to contain the logic for navigation bar to navigate to the lists (expenses) page.
     */
    private void listsPageHandler() {
        /** Read from the database
        / Read data once: addListenerForSingleValueEvent() method triggers once and then does not trigger again.
        / This is useful for data that only needs to be loaded once and isn't expected to change frequently or require active listening.
        */
        ValueEventListener Listener = new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
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

                //Add the past month's history (includes current)
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
     * Helper method to contain the logic for navigation bar to navigate to the graph page.
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
                startActivity(i);

                //avoid shifting
                overridePendingTransition(0, 0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        });
    }

    /**
     * Method controls the navBar and directs user accordingly.
     */
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            return true;
                        case R.id.navigation_lists:
                            listsPageHandler();
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

    /**
     * Do nothing on back button press because we don't want the user to be able to go back to login page
     */
    @Override
    public void onBackPressed() {
    }
}

