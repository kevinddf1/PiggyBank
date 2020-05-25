package com.example.cse110.View.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse110.Controller.Settings;
import com.example.cse110.Controller.history.HistoryItem;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Model.Database;
import com.example.cse110.Model.history.HistoryItemAdapter;
import com.example.cse110.R;
import com.example.cse110.View.CategoriesListActivity;
import com.example.cse110.View.MainActivity;
import com.example.cse110.View.PieChartActivity;
import com.example.cse110.View.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class represents the page to display a user month's across the existence of their account.
 * * When user presses: See History, this page will appear.
 *
 * @author Peter Gonzalez
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

    //For nav bar
    private static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    private static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";
    private Settings settings; //DEPRECATED
    private MonthlyData monthlyData;
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

        //navBar handling
        setUpNavBar();

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
    /**
     * Erdong's navbar
     * The user shall enter any page through clicking the icon in this nav bar
     */
    private void setUpNavBar() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(1);
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(navListener);
        Intent intent = getIntent();
        monthlyData = intent.getParcelableExtra(MONTHLY_DATA_INTENT);
    }

    //ERDONG'S NAVBAR
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            ValueEventListener Listener1 = new ValueEventListener() {
                                //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);

                                    Calendar today = Calendar.getInstance();
                                    int month = today.get(Calendar.MONTH);
                                    int year = today.get(Calendar.YEAR);
                                    base.insertMonthlydata(year, month);

                                    //pastMonthsData = base.RetrieveDataforPast(dataSnapshot, pastMonthsData, year, month);
                                    monthlyData = base.RetrieveDataCurrent(dataSnapshot, monthlyData, year, month);

                                    intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, monthlyData);
                                    if (settings == null) {
                                        settings = new Settings();
                                    }
                                    intent.putExtra(CategoriesListActivity.SETTINGS_INTENT, settings);
                                    startActivityForResult(intent, 1);
                                    overridePendingTransition(0, 0);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Failed to read value
                                }
                            };
                            base.getMyRef().addListenerForSingleValueEvent(Listener1);
                            return true;
                        case R.id.navigation_lists:
                            /* Read from the database
                               Read data once: addListenerForSingleValueEvent() method triggers once and then does not trigger again.
                               This is useful for data that only needs to be loaded once and isn't expected to change frequently or require active listening.
                            */
                            ValueEventListener Listener = new ValueEventListener() {
                                //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Intent intent = new Intent(getBaseContext(), CategoriesListActivity.class);
                                    Calendar today = Calendar.getInstance();
                                    int month = today.get(Calendar.MONTH);
                                    int year = today.get(Calendar.YEAR);
                                    base.insertMonthlydata(year, month);

                                    //pastMonthsData = base.RetrieveDataforPast(dataSnapshot, pastMonthsData, year, month);
                                    thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);

                                    intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);
                                    if (settings == null) {
                                        settings = new Settings();
                                    }
                                    intent.putExtra(CategoriesListActivity.SETTINGS_INTENT, settings);
                                    startActivityForResult(intent, 1);
                                    overridePendingTransition(0, 0);

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Failed to read value
                                }
                            };
                            base.getMyRef().addListenerForSingleValueEvent(Listener);
                            return true;

                        case R.id.navigation_history:
                            return true;

                        case R.id.navigation_graphs:
                            base.getMyRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Intent i = new Intent(getBaseContext(), PieChartActivity.class);

                                    Calendar today = Calendar.getInstance();
                                    int month = today.get(Calendar.MONTH);
                                    int year = today.get(Calendar.YEAR);

                                    thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                                    i.putExtra(PIE_CHART_DATA_INTENT, thisMonthsData);
                                    startActivityForResult(i, 1);
                                    overridePendingTransition(0, 0);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Failed to read value
                                }
                            });
                            return true;
                        case R.id.navigation_settings:
                            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                            if (settings == null) {
                                settings = new Settings();
                            }
                            intent.putExtra(SettingsActivity.SETTINGS_INTENT, settings);

                            startActivityForResult(intent, 1);
                            overridePendingTransition(0, 0);
                            return true;
                    }


                    return false;
                }
            };

}