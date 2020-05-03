package com.example.cse110;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import java.util.Calendar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button expenseListButton, historyButton, pieChartButton;
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";

    private MonthlyData thisMonthsData;
    private Database base = Database.Database(); // create a Database object
    private ArrayList<Expense> expenses;
    private int iYear;
    private int iMonth;

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
        // TODO: grab this from the database
        if (thisMonthsData == null) {
            Calendar today = Calendar.getInstance();
            thisMonthsData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
        }
        i.putExtra(HISTORY_DATA_INTENT, thisMonthsData);
        startActivityForResult(i, 1);

    }

    private void onPieCHartClick(View v){
        Intent i = new Intent(getBaseContext(), PieChartActivity.class);
        // TODO: grab this from the database
        if (thisMonthsData == null) {
            Calendar today = Calendar.getInstance();
            thisMonthsData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
        }
        i.putExtra(PIE_CHART_DATA_INTENT, thisMonthsData);
        startActivityForResult(i, 1);

    }


    public void onExpensesCLick(View v) {
//        Intent intent = new Intent(getBaseContext(), CategoriesListActivity.class);
        if (thisMonthsData == null) {
            Calendar today = Calendar.getInstance();
            thisMonthsData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
        }
//        intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);
//        startActivityForResult(intent, 1);

//        // Read from the database
//        base.getMyRef().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                Intent intent = new Intent(getBaseContext(), CategoriesListActivity.class);
//
//                //if (dataSnapshot.child(base.getkey()).exists()) {
//                    for (DataSnapshot ds : dataSnapshot.child("User").child(base.getkey()).getChildren()) {
//                        if (!ds.exists()) {
//                            break;
//                        }
//
//                        //String cate = ds.getKey();
//                        String cate_name = ds.child("Name").getValue().toString();
//                        String c_budget = ds.child("Budget").getValue().toString();
//                        int cate_budget = Integer.parseInt(c_budget);
//
//                        //Toast.makeText(getApplicationContext(), cate_name + " " + cate_budget, Toast.LENGTH_LONG).show();
//
//                        expenses = new ArrayList<Expense>();
//
//                        for (DataSnapshot ds2 : ds.child("Expense").getChildren()) {
//                            //String exp = ds2.getKey();
//                            String Cost = ds2.child("Cost").getValue().toString();
//                            String Year = ds2.child("Year").getValue().toString();
//                            String Month = ds2.child("Month").getValue().toString();
//                            String Day = ds2.child("Day").getValue().toString();
//                            String Name = ds2.child("Name").getValue().toString();
//                            String ID = ds2.child("ID").getValue().toString();
//
//                            int iCost = Integer.parseInt(Cost);
//                            iYear = Integer.parseInt(Year);
//                            iMonth = Integer.parseInt(Month);
//                            int iDay = Integer.parseInt(Day);
//                            int iID = Integer.parseInt(ID);
//
//                            Expense expense = new Expense(iID, Name, iCost, iYear, iMonth, iDay, cate_name);
//                            expenses.add(expense);
//                        }
//                        thisMonthsData.createExistCategory(cate_name, cate_budget, expenses, iMonth, iYear);
//                    }
//                //}
//                intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);
//                startActivityForResult(intent, 1);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Failed to read value
//            }
//        });
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

