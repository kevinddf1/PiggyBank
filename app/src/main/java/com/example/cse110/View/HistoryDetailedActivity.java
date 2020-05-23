package com.example.cse110.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cse110.Controller.Category;
import com.example.cse110.Controller.Expense;
import com.example.cse110.Controller.HistoryDetailedItem;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Model.HistoryDetailAdapter;
import com.example.cse110.R;

import java.util.ArrayList;

/**
 * A class representing the HistoryDetailed window, for PiggyBank.
 * When the user presses on the Category item, it will bring up this specific window.
 * @author Peter Gonzalez
 * @version 4.27
 */
public class HistoryDetailedActivity extends AppCompatActivity {

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

        Intent i = getIntent();
        current_month = i.getParcelableExtra(HISTORY_DETAILED_INTENT);
        String total = i.getStringExtra("total_expenses");

        currentCategory = current_month.getCategory(i.getStringExtra(CATEGORY_NAME));

        myExpenseList = currentCategory.getExpenses();

        //Display the month we are currently analyzing
        month_year = (TextView) findViewById(R.id.date_display);
        month_year.setText(current_month.getMonth() + " " + current_month.getYear() );
        category_name = (TextView) findViewById(R.id.category_name);
        category_name.setText(currentCategory.getName());
        budget = (TextView) findViewById(R.id.budget_display_history);

        // The details for the budget and total expenditures.
        budget.setText("Budget: $" + formatIntMoneyString(currentCategory.getBudgetAsString()));
        totalExpenses = (TextView) findViewById(R.id.total_expenses);
        totalExpenses.setText("Total Expenditure: $" + formatMoneyString(total));



        //Set up our list
        fillInHistoryDetailedItemArrayList();

        //The adapter to fill in all the necessary list.
        historyDetailAdapter = new HistoryDetailAdapter(this,historyDetailedItems);
        expenseDetails = (ListView) findViewById(R.id.history_expenses);
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
}
