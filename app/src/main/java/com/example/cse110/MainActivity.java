package com.example.cse110;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button expenseListButton;
    Button loginButton;
    private MonthlyData thisMonthsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        //Bind button to go to content main
        loginButton = findViewById(R.id.signInButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToExpenseList(v);
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

    /*private void openExpenseList(){
        Intent intent = new Intent(this, ExpenseList.class);
        startActivity(intent);
    }*/
    public void onExpensesCLick(View v) {
        Intent intent = new Intent(getBaseContext(), CategoriesListActivity.class);

        // TODO: grab this from the database
        if (thisMonthsData == null) {
            Calendar today = Calendar.getInstance();
            thisMonthsData = new MonthlyData(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
        }

        intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);

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
            }
        }
    }
}

