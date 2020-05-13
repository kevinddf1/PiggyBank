package com.example.cse110.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cse110.Model.Category;
import com.example.cse110.Model.Expense;
import com.example.cse110.Controller.HistoryItem;
import com.example.cse110.Model.MonthlyData;
import com.example.cse110.R;

import java.util.ArrayList;




/**
 * A class representing the History window for PiggyBank.
 * When user presses: See History, this page will appear.
 * @author Peter Gonzalez
 * @version April 23
 *
 */
public class HistoryActivity extends AppCompatActivity {
    /**
     * Key for pulling an object of monthlyData in the HistoryDetailedActivity
     * @see #onCreate(Bundle) 
     */
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";


    private static String CATEGORY_NAME = "category_name";
    private static String HISTORY_DETAILED_INTENT = "historyDetailedIntent";


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
     * @see HistoryItemAdapter
     */
    private HistoryItemAdapter historyItemAdapter;

    /**
     * The primary data structure to hold the information to display on History page.
     * @see HistoryItem
     */
    private ArrayList<HistoryItem> historyItemArrayList;

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
        setContentView(R.layout.activity_history);

        //Retrieve passed in MonthlyData object and extract date/categories
        Intent i = getIntent();
        current_month = i.getParcelableExtra(HISTORY_DATA_INTENT);

        //Update our local variables to match
        assert current_month != null;
        categoryArrayList = current_month.getCategoriesAsArray();
        month_year = (TextView) findViewById(R.id.month_year_display);
        month_year.setText(current_month.getMonth() + " " + current_month.getYear());

        //Set up our list
        fillInHistoryItemArrayList();
        historyItemAdapter = new HistoryItemAdapter(this, historyItemArrayList);
        pastCategories = (ListView) findViewById(R.id.Categories);
        pastCategories.setAdapter(historyItemAdapter);

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
                HistoryItem currentItem = historyItemAdapter.getItem(position);

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
        historyItemArrayList = new ArrayList<>();
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
            historyItemArrayList.add(new HistoryItem(currentCategory.getName(), currentCategory.getBudget(), totalExpenses));
        }
    }


}
