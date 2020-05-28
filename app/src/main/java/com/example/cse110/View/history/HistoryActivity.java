package com.example.cse110.View.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cse110.Controller.history.HistoryItem;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Model.Database;
import com.example.cse110.Model.history.HistoryItemAdapter;
import com.example.cse110.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * This class represents the page to display a user month's across the existence of their account.
 * * When user presses: See History, this page will appear.
 *
 * @author Peter, Erdong
 * @version May 15
 */
public class HistoryActivity extends AppCompatActivity {

    /**
     * Key for pull past month's summary from MainActivity
     *
     * @see #onCreate(Bundle)
     */
    private static final String LIST_OF_MONTHS = "List of Months"; //

    /**
     * Key for pulling an object of monthlyData into HistoryCategoryActivity
     *
     * @see #onCreate(Bundle)
     */
    private static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";

    /**
     * Indices for pulling MONTH YEAR BUDGET EXPENSES from entries in allMonths
     */
    private static final int MONTH_INDEX = 0;
    private static final int YEAR_INDEX = 1;
    private static final int BUDGET_INDEX = 2;
    private static final int EXPENSES_INDEX = 3;

    /**
     * Instantiated with selected month and put as an extra for HistoryCategoryActivity
     */
    private MonthlyData thisMonthsData;

    /**
     * Database singleton to get most up to date information from the user's account
     */
    private final Database base = Database.Database(); // create a Database object


    /**
     * Primary data structure to hold a summary of user's past months
     */
    private ArrayList<String> allMonths;


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

    /**
     * To be instantiated with the month the user selects on screen
     */
    private HistoryItem currentItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Match to rendering in activity_history.xml
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //Extract list of months from an incoming Intent
        instantiateListOfMonths();

        //Set up ListView w/ a custom adapter
        setUpList();

        //Handle user selection of a particular month
        setUpClickHandler();
    }

    /**
     * Handle incoming intents and extract pastMonthSummary's output
     */
    private void instantiateListOfMonths() {
        //Get data from incoming Intent
        Bundle bundle = getIntent().getExtras();

        //Null error handling
        assert bundle != null;

        //Extract months summary
        allMonths = bundle.getStringArrayList(LIST_OF_MONTHS);
    }

    /**
     * Set up HistoryItem conversion and attach the custom adapter to the ListView
     */
    private void setUpList() {
        //Parse our data into HistoryItem, which can be displayed
        fillInHistoryItemList();

        //Attach Adapter
        historyItemAdapter = new HistoryItemAdapter(this, listOfHistoryItems);
        listOfMonths = findViewById(R.id.Months);
        listOfMonths.setAdapter(historyItemAdapter);
    }

    /**
     * Parse months summary and convert into HistoryItems
     */
    private void fillInHistoryItemList() {
        listOfHistoryItems = new ArrayList<>();

        //Go through all Strings representing (MONTH YEAR BUDGET EXPENSES(not as cents) and convert to HistoryItem
        for (String currentMonth : allMonths) {
            String[] brokenDownString = currentMonth.split("-"); //Break String into components: MONTH YEAR BUDGET EXPENSES

            //Convert to HistoryItems for rendering purposes
            HistoryItem currentMonthItem = new HistoryItem(brokenDownString[MONTH_INDEX] + " " + brokenDownString[YEAR_INDEX], brokenDownString[BUDGET_INDEX], brokenDownString[EXPENSES_INDEX]);

            //Add to listOfHistoryItems for Adapter updates
            listOfHistoryItems.add(currentMonthItem);

        }
    }

    /**
     *  Set up clicking handling with onItemClick for ListViews
     */
    private void setUpClickHandler() {
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
                //Identify the month the user selected on screen
                currentItem = historyItemAdapter.getItem(position);

                ValueEventListener postListener = new ValueEventListener() {

                    /**
                     * Pulls most recent database info and starts nextActivity
                     * @param dataSnapshot A "snapshot" of the user's database info @ the current point in time
                     */
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //Set up new intent to send to HistoryCategoryActivity
                        Intent i = new Intent(getBaseContext(), HistoryCategoryActivity.class);

                        //Parse the month and year selected to pull from database
                        String[] separateMonthYear = currentItem.getMonthYear().split(" ");
                        thisMonthsData = null; //Ensure to pull from database on every call
                        thisMonthsData = base.RetrieveDataPast(dataSnapshot, null, separateMonthYear[0], separateMonthYear[1]);

                        //Attach the monthlyData associated w/ the chosen month and start next activity
                        i.putExtra(HISTORY_DATA_INTENT, thisMonthsData);
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };
                base.getMyRef().addValueEventListener(postListener);

            }

        });
    }

}