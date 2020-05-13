package com.example.cse110;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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



    /**
     * Key for pulling an object of monthlyData in the HistoryDetailedActivity
     * @see #onCreate(Bundle)
     */
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";git

    private MonthlyData current_month;

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

        categoryArrayList= current_month.getCategoriesAsArray();
        for (int i=0; i<categoryArrayList.size();i++){
            Category c = categoryArrayList.get(i);
            Log.d("what", c.getName());
            cateArrayList.add(c.getName());
            Log.d("price", Integer.toString(getTotalExpense(c)));
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
                            Intent i = new Intent(getBaseContext(), HistoryActivity.class);
                            setResult(RESULT_OK, i);
                            //i.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, monthlyData);
                            // TODO: grab this from the database

                            if (current_month == null) {
                                Calendar today = Calendar.getInstance();
                                current_month = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
                            }

                            i.putExtra(HISTORY_DATA_INTENT, current_month);
                            startActivityForResult(i, 1);
                            overridePendingTransition(0, 0);
                            return true;

                        case R.id.navigation_graphs:
                            return true;

                    }
                    return false;
                }
            };

}
