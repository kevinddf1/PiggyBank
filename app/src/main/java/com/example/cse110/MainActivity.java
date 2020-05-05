package com.example.cse110;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button expenseListButton, historyButton, pieChartButton, settingsButton;
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";

    private MonthlyData thisMonthsData;
    private Settings settings;

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
        Intent i = new Intent(getBaseContext(), HistoryActivity.class);
        // TODO: grab this from the database
        if (thisMonthsData == null) {
            Calendar today = Calendar.getInstance();
            thisMonthsData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
        }
        // TODO: grab this from the database
        if (settings == null) {
            settings = new Settings();
        }
        i.putExtra(HISTORY_DATA_INTENT, thisMonthsData);
        startActivityForResult(i, 1);

    }

    private void onPieChartClick(View v){
        Intent i = new Intent(getBaseContext(), PieChartActivity.class);
        // TODO: grab this from the database
        if (thisMonthsData == null) {
            Calendar today = Calendar.getInstance();
            thisMonthsData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
        }
        // TODO: grab this from the database
        if (settings == null) {
            settings = new Settings();
        }
        i.putExtra(PIE_CHART_DATA_INTENT, thisMonthsData);
        startActivityForResult(i, 1);

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

    public void onExpensesCLick(View v) {
        Intent intent = new Intent(getBaseContext(), CategoriesListActivity.class);

        // TODO: grab this from the database
        if (thisMonthsData == null) {
            Calendar today = Calendar.getInstance();
            thisMonthsData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
        }
        // TODO: grab this from the database
        if (settings == null) {
            settings = new Settings();
        }
        intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);
        intent.putExtra(CategoriesListActivity.SETTINGS_INTENT, settings);

        startActivityForResult(intent, 1);
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
                settings = data.getParcelableExtra(SettingsActivity.SETTINGS_INTENT);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing on back button press because we don't want the user to be able to go back to login page
    }
}

