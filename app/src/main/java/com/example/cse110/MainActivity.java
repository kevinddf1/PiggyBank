package com.example.cse110;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import java.util.Calendar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button expenseListButton, historyButton, pieChartButton;
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";

    private MonthlyData thisMonthsData;
    private Database base = Database.Database(); // create a Database object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        //Check if this a month should be re-instantiated
        Intent intent = getIntent();
        thisMonthsData = intent.getParcelableExtra(MONTHLY_DATA_INTENT);
        //Bind button to go to expense list

        expenseListButton = findViewById(R.id.ExpensesButton);

        historyButton = findViewById(R.id.HistoryButton);
        historyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onHistoryClick(v);
            }
        });

        pieChartButton= findViewById(R.id.PieChartButton);
        pieChartButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onPieCHartClick(v);
            }
        });
    }

    /**
     *
     *
     * @param v
     */
    public void goToExpenseList(View v) {
        setContentView(R.layout.content_main);
        //Bind button to go to expense list
        expenseListButton = findViewById(R.id.ExpensesButton);

        expenseListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExpensesCLick(v);

            }
        });
    }

    private void onHistoryClick(View v){
        Intent i = new Intent(getBaseContext(), HistoryActivity.class);
        if (thisMonthsData == null) {
            Calendar today = Calendar.getInstance();
            thisMonthsData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
        }
        i.putExtra(HISTORY_DATA_INTENT, thisMonthsData);
        startActivityForResult(i, 1);

    }

    private void onPieCHartClick(View v){
        Intent i = new Intent(getBaseContext(), PieChartActivity.class);
        if (thisMonthsData == null) {
            Calendar today = Calendar.getInstance();
            thisMonthsData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
        }
        i.putExtra(PIE_CHART_DATA_INTENT, thisMonthsData);
        startActivityForResult(i, 1);

    }


    // TODO: Month Year UPDATE FROM CATEGORY
    public void onExpensesCLick(View v) {
            /* Read from the database
            / Read data once: addListenerForSingleValueEvent() method triggers once and then does not trigger again.
            / This is useful for data that only needs to be loaded once and isn't expected to change frequently or require active listening.
            */
            base.getMyRef().addListenerForSingleValueEvent(new ValueEventListener() {
                //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Intent intent = new Intent(getBaseContext(), CategoriesListActivity.class);
                    if (thisMonthsData == null) { // check if the object is NULL, if NULL initialize it with current Date
                        Calendar today = Calendar.getInstance();
                        thisMonthsData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));

                        // this loop retrieve all the categories from database
                        for (DataSnapshot ds : dataSnapshot.child("User").child(base.getUserKey()).getChildren()) {
                            if (!ds.exists()) { // check if there are any category in user's account
                                break; // if NOT, break the loop
                            }
                            // get the data of current category
                            String cate_name = ds.child("Name").getValue().toString();
                            System.out.println(cate_name);
                            String c_budget = ds.child("Budget").getValue().toString();
                            int cate_budget = Integer.parseInt(c_budget);
                            String c_year = ds.child("Year").getValue().toString();
                            int cate_year = Integer.parseInt(c_year);
                            String c_month = ds.child("Month").getValue().toString();
                            int cate_month = Integer.parseInt(c_month);

                            ArrayList<Expense> expenses = new ArrayList<Expense>();
                            // this loop retrieve all the expenses in current category from database
                            for (DataSnapshot ds2 : ds.child("Expense").getChildren()) {
                                if (!ds2.exists()) { // check if there are any expenses in user's account
                                    break; // if NOT, break the loop
                                }
                                // get the data of current expense
                                String Cost = ds2.child("Cost").getValue().toString();
                                String Year = ds2.child("Year").getValue().toString();
                                String Month = ds2.child("Month").getValue().toString();
                                String Day = ds2.child("Day").getValue().toString();
                                String Name = ds2.child("Name").getValue().toString();
                                String ID = ds2.child("ID").getValue().toString();
                                int iCost = Integer.parseInt(Cost);
                                int iYear = Integer.parseInt(Year);
                                int iMonth = Integer.parseInt(Month);
                                int iDay = Integer.parseInt(Day);
                                int iID = Integer.parseInt(ID);
                                // create expense
                                Expense expense = new Expense(iID, Name, iCost, iYear, iMonth, iDay, cate_name);
                                expenses.add(expense);
                            }
                            // create category
                            thisMonthsData.createExistCategory(cate_name, cate_budget, expenses, cate_month, cate_year);
                        }
                    }
                    intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);
                    startActivityForResult(intent, 1);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Failed to read value
                }
            });
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
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing on back button press because we don't want the user to be able to go back to login page
    }
}

