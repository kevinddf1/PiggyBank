package com.example.cse110.View.history;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cse110.Controller.Category;
import com.example.cse110.Controller.Expense;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Controller.history.HistoryDetailedItem;
import com.example.cse110.Model.history.HistoryDetailAdapter;
import com.example.cse110.R;

import java.util.ArrayList;
import com.example.cse110.Model.FormattingTool;

/**
 * A class representing the HistoryDetailed window, for PiggyBank.
 * When the user presses on the Category item, it will bring up this specific window.
 *
 * @author Peter Erdong
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


}
