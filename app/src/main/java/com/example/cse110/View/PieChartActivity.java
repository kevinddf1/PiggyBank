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
    private MonthlyData thisMonthsData;



    /**
     * Key for pulling an object of monthlyData in the HistoryDetailedActivity
     * @see #onCreate(Bundle)
     */
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String SETTINGS_INTENT = "SettingsActivity settings";
    private static final String LIST_OF_MONTHS = "List of Months";


    private MonthlyData current_month;
    private Settings settings;
    private Database base = Database.Database(); // create a Database object
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
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(1);
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(navListener);

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

    private int getTotalExpense(Category c) {
        int ret=0;
        ArrayList<Expense> expenseArray= c.getExpenses();
        for (int i=0; i<expenseArray.size(); i++){
            ret+=expenseArray.get(i).getCost();
        }

        return ret;
    }


    public void setupPieChart(){

        Pie pie= AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i=0; i<cateArrayList.size(); i++){
            dataEntries.add(new ValueDataEntry(cateArrayList.get(i), totalExpenseArrayList.get(i)));
        }

        pie.data(dataEntries);
        anyChartView.setChart(pie);
    }

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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:

                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            setResult(RESULT_OK, intent);
                            intent.putExtra(MONTHLY_DATA_INTENT, current_month);
                            startActivityForResult(intent, 1);
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.navigation_lists:
                            Intent in = new Intent(getBaseContext(), CategoriesListActivity.class);
                            in.putExtra(HISTORY_DATA_INTENT, current_month);
                            in.putExtra(MONTHLY_DATA_INTENT, current_month);
                            startActivityForResult(in, 1);
                            overridePendingTransition(0, 0);

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
                                    i.putExtra(LIST_OF_MONTHS, base.getPastMonthSummary(dataSnapshot));
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
                            return true;
                        case R.id.navigation_settings:
                            Intent inten = new Intent(getBaseContext(), SettingsActivity.class);
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
