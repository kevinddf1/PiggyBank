package com.example.cse110;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ExpensesListActivity extends AppCompatActivity {
    public static final String MONTHLY_DATA_INTENT = "ExpenseListActivity monthlyData";
    public static final String CATEGORY_NAME_INTENT = "ExpenseListActivity categoryName";
    public static final String SETTINGS_INTENT = "ExpenseListActivity settings";
    private static  final int MAX_EXPENSE_VALUE = 9999999;
    //Our max allowable int is 9,999,999 which is 7 place values
    private static final int MAX_BUDGET =  7;
    private EditText expenseName, expenseCost;
    private EditText categoryBudget;
    private TextView totalExpensesDisplay;
    //List Structure
    private ExpenseListAdapter expenseAdapter;

    private MonthlyData monthlyData;
    private Settings settings;
    private Category category;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent intent = getIntent();
        monthlyData = intent.getParcelableExtra(MONTHLY_DATA_INTENT);
        String categoryNameFromParent = intent.getStringExtra(CATEGORY_NAME_INTENT);
        category = monthlyData.getCategory(categoryNameFromParent);
        settings = intent.getParcelableExtra(SETTINGS_INTENT);

        //Toolbar categoryToolBar = findViewById(R.id.categoryBar);
        //setActionBar(categoryToolBar);

        //textViews in the top bar
        TextView categoryName = findViewById(R.id.category_name);
        categoryName.setText(categoryNameFromParent);

        categoryBudget = findViewById((R.id.budget_display));
        categoryBudget.setText("$" + formatIntMoneyString(category.getBudgetAsString()));
        totalExpensesDisplay = (TextView) findViewById(R.id.total_expenses);

        //Account for initial lack of decimal values
        totalExpensesDisplay.setText("$" + formatMoneyString(Long.toString(category.getTotalExpenses()/100)));
        // Bind element from XML file
        expenseName = findViewById(R.id.expense_name);
        expenseCost = findViewById(R.id.expense_cost);
        Button btnAdd = findViewById(R.id.AddToList);

        // Initialize List
        final ArrayList<Expense> arrayOfItems = category.getExpenses();
        expenseAdapter = new ExpenseListAdapter(this, arrayOfItems, category);
        ListView expensesList = findViewById(R.id.Categories);
        expensesList.setAdapter(expenseAdapter);

        //Detect User Changes
        categoryBudget.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && categoryBudget.getText().toString() != null) {
                    if (categoryBudget.getText().toString().length() > MAX_BUDGET) {
                        Toast.makeText(getBaseContext(), "A category cannot have a budget greater than $9,999,999.", Toast.LENGTH_LONG).show();
                    } else if (categoryBudget.getText().toString().isEmpty()) {
                        //In the case of of an empty string
                        categoryBudget.setText("$" + formatIntMoneyString(category.getBudgetAsString()));
                    } else {
                        //Ensure valid input
                        try {
                            // Create new item and update adapter
                            category.setBudget(Integer.parseInt(categoryBudget.getText().toString()));

                            //Update totalBudget
                            monthlyData.setTotalBudget();


                            categoryBudget.setText("$" + formatIntMoneyString(category.getBudgetAsString()));
                            Toast.makeText(getBaseContext(), "Category budget has been successfully updated", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getBaseContext(), "Invalid input", Toast.LENGTH_LONG).show();
                        }
                    }
                }else {
                   categoryBudget.getText().clear();
                }
            }
        });

        // Set Event Handler to add items to the list
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Date Object
                Calendar today = Calendar.getInstance();

                // Ensure that both fields are filled.
                if (!expenseCost.getText().toString().isEmpty() && !expenseName.getText().toString().isEmpty() ) {

                    //Check that we do not go over the max allowed expense
                    try {
                        if (Double.parseDouble(expenseCost.getText().toString()) > MAX_EXPENSE_VALUE)
                            throw new Exception();

                        // Create new item and update adapter
                        category.createExpense(expenseName.getText().toString(), Double.parseDouble(expenseCost.getText().toString()), today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
                       // Update total expenses for this category
                        double currentTotalExpense = category.getTotalExpenses()/100.00;
                        totalExpensesDisplay.setText("$" + formatMoneyString( Double.toString(currentTotalExpense )));

                        expenseName.getText().clear();
                        expenseCost.getText().clear();
                        expenseAdapter.notifyDataSetChanged();
                    } catch (Exception overflow) {
                        if (settings.getEnableNotifications()) {
                            Toast.makeText(getBaseContext(), "Please provide expense cost less than $9,999,999", Toast.LENGTH_LONG).show();
                        }
                    }


                } else {
                    if (settings.getEnableNotifications()) {
                        // Insufficient number of filled fields results in an error warning.
                        Toast missingInformationWarning = Toast.makeText(getBaseContext(), "Please fill in expense name and cost.", Toast.LENGTH_SHORT);
                        missingInformationWarning.show();
                    }
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                monthlyData = data.getParcelableExtra(ExpensesListActivity.MONTHLY_DATA_INTENT);
            }
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(MONTHLY_DATA_INTENT, monthlyData);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
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

    //Handle pressing away from setting category
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    /**
     * Allow formatting for expenses display
     * @param valueToFormat
     * @return
     */

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

    /**
     * Update the display for expenseList
     */
    public void updateTotalExpenseDisplay(String toDisplay){


        totalExpensesDisplay.setText( toDisplay);

    }
}
