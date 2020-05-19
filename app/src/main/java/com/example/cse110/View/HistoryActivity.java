package com.example.cse110.View;

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

import com.example.cse110.Controller.HistoryCategoryItem;
import com.example.cse110.Controller.Settings;
import com.example.cse110.Controller.Category;
import com.example.cse110.Controller.Expense;
import com.example.cse110.Controller.HistoryItem;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Model.Database;
import com.example.cse110.Model.HistoryItemAdapter;
import com.example.cse110.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;

/**
 * This class represents the page to display a user month's across the existence of their account.
 *  * When user presses: See History, this page will appear.
 * @author Peter Gonzalez
 * @version May 15
 */
public class HistoryActivity extends AppCompatActivity{

    private static final String LIST_OF_MONTHS = "List of Months"; //For past months in HistoryActivity.java

    /**
     * Key for pulling an object of monthlyData in the HistoryDetailedActivity
     * @see #onCreate(Bundle)
     */
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";
    public static final String SETTINGS_INTENT = "SettingsActivity settings";
    private MonthlyData thisMonthsData;
    private Database base = Database.Database(); // create a Database object
    private static final String TAG = "HistoryActivity";



    private static String CATEGORY_NAME = "category_name";
    private static String HISTORY_DETAILED_INTENT = "historyDetailedIntent";

    /**
     * Primary data structure to hold a summary of user's past months
     */
    private ArrayList<String> allMonths;

    /**
     * The display of one month in user's history
     */
    private TextView monthYearDisplay;

    /**
     * The display of all budgets for a particular month
     */
    private TextView totalBudgetDisplay;


    /**
     * The display for all expenses for a particular month
     */
    private TextView totalExpensesDisplay;


    /**
     * Primary data structure to construct the correct adapter.
     */
    private ArrayList<HistoryItem> listOfHistoryItems;

    /**
     * Adapter to help front end display the user's data
     */
    private HistoryItemAdapter historyItemAdapter;

    /**
     * ListView contains all the user's month's to display
     */
    private ListView listOfMonths;

    HistoryItem currentItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //Get data from String ArrayList
        Bundle bundle = getIntent().getExtras();
        allMonths = bundle.getStringArrayList(LIST_OF_MONTHS);

        //Parse our data into HistoryItem, which can be display
        fillInHistoryItemList();

        //Set up list
        historyItemAdapter = new HistoryItemAdapter(this, listOfHistoryItems);
        listOfMonths = findViewById(R.id.Months);
        listOfMonths.setAdapter(historyItemAdapter);

        //Set Up Clicking Handling
        listOfMonths.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * When a certain item is clicked in the list display, the user will be redirected to a detailed view of the chosen category.
             *
             * @param parent   The AdapterView for the ListView.
             * @param view     The View for the HistoryItem.
             * @param position The position of the item in the list.
             * @param id       The particular id of the view.
             */
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                currentItem = historyItemAdapter.getItem(position);
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent i = new Intent(getBaseContext(), HistoryCategoryActivity.class);

                        String[] separateMonthYear = currentItem.getMonthYear().split(" ");
                        thisMonthsData = null;
                        thisMonthsData = base.RetrieveDataPast(dataSnapshot, thisMonthsData, separateMonthYear[0], separateMonthYear[1]);
                        //thisMonthsData = base.RetrieveDatafromDatabase(dataSnapshot, thisMonthsData, year, month);
                        i.putExtra(HISTORY_DATA_INTENT, thisMonthsData);

                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        Toast.makeText(HistoryActivity.this, "Failed to load post.",
                                Toast.LENGTH_SHORT).show();
                        // [END_EXCLUDE]

                    }
                };
                base.getMyRef().addValueEventListener(postListener);

            }

        });
    }

    private void fillInHistoryItemList(){
        listOfHistoryItems = new ArrayList<>();

        for(String currentMonth : allMonths){
            String[] brokenDownString = currentMonth.split("-"); //Break String into components: MONTH YEAR BUDGET EXPENSES
            HistoryItem currentMonthItem = new HistoryItem(brokenDownString[0] + " " + brokenDownString[1], brokenDownString[2], brokenDownString[3]);
            listOfHistoryItems.add(currentMonthItem);

        }
    }

}