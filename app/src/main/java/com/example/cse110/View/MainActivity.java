package com.example.cse110.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cse110.Model.Database;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.R;
import com.example.cse110.Controller.Settings;
import com.example.cse110.Model.FormattingTool;
import java.util.Calendar;

import com.example.cse110.View.history.HistoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    LinearLayout expenseListButton, historyButton, pieChartButton, settingsButton;
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String SETTINGS_INTENT = "CategoriesListActivity settings";
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";
    private static final String LIST_OF_MONTHS = "List of Months"; //For past months in HistoryActivity.java

    private static final int DISTANCE_FROM_MILLIONS_COMMA = 9;
    private static final int DISTANCE_FROM_THOUSANDS_COMMA = 6;
    private static final int LENGTH_LESS_THAN_THOUSANDS = 6;
    private static final int LENGTH_LESS_THAN_MILLIONS = 9;
    private static final int BEGIN_INDEX = 0;
    private static final int CORRECT_DECIMAL = 2;
    private static final int TOO_SHORT_DECIMAL = 1;
    private static final int MISSING_DECIMAL = -1;
    private static final int DISTANCE_FROM_MILLIONS_COMMA_NO_DECIMAL = 6;
    private static final int DISTANCE_FROM_THOUSANDS_COMMA_NO_DECIMAL = 3;
    private static final int LENGTH_LESS_THAN_THOUSANDS_NO_DECIMAL = 3;
    private static final int LENGTH_LESS_THAN_MILLIONS_NO_DECIMALS = 6;


    private MonthlyData thisMonthsData;
    private MonthlyData pastMonthsData;

    private Settings settings;

    private Database base = Database.Database(); // create a Database object

    /**
     * Formatting tool to avoid redundancies.
     */
    private FormattingTool formattingTool = new FormattingTool();
    /**
     * TextViews to display budget and total expenses
     */
    TextView totalBudgetDisplay, totalExpenseDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        //Check if this a month should be re-instantiated
        Intent intent = getIntent();
        thisMonthsData = intent.getParcelableExtra(MONTHLY_DATA_INTENT);

        BottomNavigationView navView = findViewById(R.id.nav_view);
       // navView.setLabelVisibilityMode(1);
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(navListener);
        //Bind button to go to expense list

        //Instantiate monthlyData only if currently null
       // if(thisMonthsData == null){
         //   base.
        //Bind button to go to expense list
        expenseListButton = findViewById(R.id.ExpensesButton);
        expenseListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExpensesCLick(v);

            }
        });


        if(thisMonthsData != null) {
            totalBudgetDisplay = findViewById(R.id.currentCash);
            String budgetRendering = "Total Budget: " + Long.toString(thisMonthsData.getTotalBudget());
            totalBudgetDisplay.setText(budgetRendering);

            totalExpenseDisplay = findViewById(R.id.totalExpenses);
            String expenseRendering = "Total Expenses: " + Long.toString(thisMonthsData.getTotalExpensesAsCents()/100);

            totalExpenseDisplay.setText(expenseRendering);
        } else {
            // Get Bundle object that contain the array
            Bundle b = this.getIntent().getExtras();
            String[] list = b.getStringArray("Total Budget and Expense");

            //Bind our month's expenses and budget to proper display
            totalBudgetDisplay = findViewById(R.id.currentCash);
            String budgetRendering = "Total Budget: " + formattingTool.formatIntMoneyString( list[0]);
            totalBudgetDisplay.setText(budgetRendering);

            totalExpenseDisplay = findViewById(R.id.totalExpenses);
            String expensesRendering = "Total Expenses: " + formattingTool.formatMoneyString(formattingTool.formatDecimal(Double.toString(Long.parseLong(list[1])/100.00)));
            totalExpenseDisplay.setText(expensesRendering);
        }


        historyButton = findViewById(R.id.HistoryButton);
        historyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onHistoryClick(v);
            }
        });

        pieChartButton = findViewById(R.id.PieChartButton);
        pieChartButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onPieChartClick(v);
            }
        });


        settingsButton = findViewById(R.id.SettingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onSettingsClick(v);
            }
        });


    }

    private void onHistoryClick(View v){
        ValueEventListener Listener = new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent i = new Intent(getBaseContext(), HistoryActivity.class);

                Calendar today = Calendar.getInstance();
                int month = today.get(Calendar.MONTH);
                int year = today.get(Calendar.YEAR);

                thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                //thisMonthsData = base.RetrieveDatafromDatabase(dataSnapshot, thisMonthsData, year, month);

                i.putExtra(HISTORY_DATA_INTENT, thisMonthsData);

                //Add the past month's history (includes current)
                i.putExtra(LIST_OF_MONTHS, base.getPastMonthSummary(dataSnapshot));
                startActivityForResult(i, 1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        };
        base.getMyRef().addListenerForSingleValueEvent(Listener);
    }

    private void onPieChartClick(View v){
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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        });
    }



    public void onSettingsClick(View v) {
        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);

        // TODO: grab this from the database
        if (settings == null) {
            settings = new Settings();
        }
        intent.putExtra(SettingsActivity.SETTINGS_INTENT, settings);

        startActivityForResult(intent, 1);
    }

    // TODO: Month Year UPDATE FROM CATEGORY
    public void onExpensesCLick(View v) {
        /* Read from the database
        / Read data once: addListenerForSingleValueEvent() method triggers once and then does not trigger again.
        / This is useful for data that only needs to be loaded once and isn't expected to change frequently or require active listening.
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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        };
        base.getMyRef().addListenerForSingleValueEvent(Listener);
    }

    /*
    public void onHistoryCLick(View v) {
        Intent intent = new Intent(getBaseContext(), MessagingPage.class);
        startActivity(intent);
    }
    public void onLogoutCLick(View v) {
        Intent intent = new Intent(getBaseContext(), MapPage.class);
        startActivity(intent);
    }
    public void onSettingsCLick(View v) {
        Intent intent = new Intent(getBaseContext(), FAQPage.class);
        startActivity(intent);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                thisMonthsData = data.getParcelableExtra(CategoriesListActivity.MONTHLY_DATA_INTENT);

                totalBudgetDisplay = findViewById(R.id.currentCash);
                String budgetRendering = "Total Budget: " + formattingTool.formatIntMoneyString(Long.toString(thisMonthsData.getTotalBudget()));
                totalBudgetDisplay.setText(budgetRendering);

                totalExpenseDisplay = findViewById(R.id.totalExpenses);
                String expenseRendering = "Total Expenses: " + formattingTool.formatMoneyString(formattingTool.formatDecimal(Long.toString(thisMonthsData.getTotalExpensesAsCents()/100)));

                totalExpenseDisplay.setText(expenseRendering);
                Settings settings = data.getParcelableExtra(SettingsActivity.SETTINGS_INTENT);
                if (settings != null) {
                    this.settings = settings;
                }
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            return true;
                        case R.id.navigation_lists:
                                     /* Read from the database
        / Read data once: addListenerForSingleValueEvent() method triggers once and then does not trigger again.
        / This is useful for data that only needs to be loaded once and isn't expected to change frequently or require active listening.
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
                            ValueEventListener Listener2 = new ValueEventListener() {
                                //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Intent i = new Intent(getBaseContext(), HistoryActivity.class);

                                    Calendar today = Calendar.getInstance();
                                    int month = today.get(Calendar.MONTH);
                                    int year = today.get(Calendar.YEAR);

                                    thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                                    //thisMonthsData = base.RetrieveDatafromDatabase(dataSnapshot, thisMonthsData, year, month);

                                    i.putExtra(HISTORY_DATA_INTENT, thisMonthsData);
                                    startActivityForResult(i, 1);
                                    overridePendingTransition(0, 0);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Failed to read value
                                }
                            };
                            base.getMyRef().addListenerForSingleValueEvent(Listener2);
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

                            // TODO: grab this from the database
                            if (settings == null) {
                                settings = new Settings();
                            }
                            intent.putExtra(SettingsActivity.SETTINGS_INTENT, settings);

                            startActivityForResult(intent, 1);
                            overridePendingTransition(0, 0);

                    }
                    return false;
                }
            };

    @Override
    public void onBackPressed() {
        // Do nothing on back button press because we don't want the user to be able to go back to login page
    }







}

