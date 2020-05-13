package com.example.cse110;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button expenseListButton, historyButton, pieChartButton, settingsButton;
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";

    private MonthlyData thisMonthsData;

    private Settings settings;

    private Database base = Database.Database(); // create a Database object

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

        //Instantiate monthlyData only if currently null
       // if(thisMonthsData == null){
         //   base.
        //Bind button to go to expense list
        expenseListButton = findViewById(R.id.ExpensesButton);

        //Bind our month's expenses and budget to proper display
        //totalBudgetDisplay = findViewById(R.id.currentCash);
        //totalBudgetDisplay.setText(totalBudgetDisplay.getText() + " $" + thisMonthsData.getTotalBudget());
        //totalExpenseDisplay = findViewById(R.id.totalExpenses);




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
        base.getMyRef().addListenerForSingleValueEvent(new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent i = new Intent(getBaseContext(), HistoryActivity.class);
                thisMonthsData = base.RetrieveDatafromDatabase(dataSnapshot, thisMonthsData);
                i.putExtra(HISTORY_DATA_INTENT, thisMonthsData);
                startActivityForResult(i, 1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        });
    }

    private void onPieChartClick(View v){
        base.getMyRef().addListenerForSingleValueEvent(new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent i = new Intent(getBaseContext(), PieChartActivity.class);
                thisMonthsData = base.RetrieveDatafromDatabase(dataSnapshot, thisMonthsData);
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
        base.getMyRef().addListenerForSingleValueEvent(new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent = new Intent(getBaseContext(), CategoriesListActivity.class);
                thisMonthsData = base.RetrieveDatafromDatabase(dataSnapshot, thisMonthsData);
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
                Settings settings = data.getParcelableExtra(SettingsActivity.SETTINGS_INTENT);
                if (settings != null) {
                    this.settings = settings;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing on back button press because we don't want the user to be able to go back to login page
    }

    /**
     * Helper method to instantiate current month upon creation
     */
    private void instantiateCurrentMonth(){
        base.getMyRef().addListenerForSingleValueEvent(new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent i = new Intent(getBaseContext(), HistoryActivity.class);
                thisMonthsData = base.RetrieveDatafromDatabase(dataSnapshot, thisMonthsData);
                i.putExtra(HISTORY_DATA_INTENT, thisMonthsData);
                startActivityForResult(i, 1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        });
    }
}

