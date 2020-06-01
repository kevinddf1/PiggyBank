package com.example.cse110.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.Model.Database;
import com.example.cse110.R;
import com.example.cse110.View.graphs.GraphsActivity;
import com.example.cse110.View.history.HistoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;

public class SettingsActivity extends AppCompatActivity {
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String Graphs_DATA_INTENT = "GraphsActivity monthlyData";
    private static final String LIST_OF_MONTHS = "List of Months"; //For past months data
    private static final int NAV_BAR_INDEX = 4;

    private MonthlyData monthlyData;
    private MonthlyData thisMonthsData;
    private Database base = Database.Database(); // create a Database object

    /**
     *
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        //navBar handling
        setUpNavBar();

        // On-click listener for sign out button
        final Button signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();

                // Start login activity
                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        // On-click listener for change passsword button
        final Button changePasswordButton = findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Start account recovery activity
                Intent i = new Intent(getBaseContext(), AccountRecoveryActivity.class);
                startActivity(i);
            }
        });

        // On-click listener for delete account button
        final Button deleteAccountButton = findViewById(R.id.delete_account_button);
        deleteAccountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Show confirmation prompt
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setCancelable(true);
                builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                base.delete_account(); //delete data in this account
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getBaseContext(), "Account deletion failed. " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                // Start login activity
                                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                                startActivity(i);
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // On-click listener for meet the team button
        final Button meetTeamButton = findViewById(R.id.meet_the_team_button);
        meetTeamButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();

                // Start login activity
                Intent i = new Intent(getBaseContext(), MeetTheTeamActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * The user shall enter any page through clicking the icon in this nav bar
     */
    private void setUpNavBar() {
        // Create the bottom navigation bar
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // set the label to be visible
        navView.setLabelVisibilityMode(1);
        Menu menu = navView.getMenu();
        // Check the icon
        MenuItem menuItem = menu.getItem(NAV_BAR_INDEX);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(navListener);
    }

    /**
     * Helper method to contain the logic for navigation bar to navigate to the home page
     */
    private void homePageHandler() {
        ValueEventListener Listener = new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified
            // database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //create a new intent for home page activity
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                //set up the date for monthly data
                Calendar today = Calendar.getInstance();
                int month = today.get(Calendar.MONTH);
                int year = today.get(Calendar.YEAR);
                base.insertMonthlydata(year, month);
                // Retrieve the current data from data base
                thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                // put extra data for categories and expenses
                intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);
                startActivityForResult(intent, 1);
                // avoid shifting
                overridePendingTransition(0, 0);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        };
        base.getMyRef().addListenerForSingleValueEvent(Listener);
    }

    /**
     * Helper method to contain the logic for navigation bar to navigate to the lists page
     */
    private void listsPageHandler() {
         /* Read from the database
        / Read data once: addListenerForSingleValueEvent() method triggers once and then does not
        trigger again.
        / This is useful for data that only needs to be loaded once and isn't expected to change
        frequently or require active listening.
        */
        ValueEventListener Listener = new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified
            // database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent = new Intent(getBaseContext(), CategoriesListActivity.class);
                //set up the date for monthly data
                Calendar today = Calendar.getInstance();
                int month = today.get(Calendar.MONTH);
                int year = today.get(Calendar.YEAR);
                base.insertMonthlydata(year, month);
                //Retrieve the monthly data from the database
                thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                //put extra data into new intent
                intent.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, thisMonthsData);
                startActivityForResult(intent, 1);
                //avoid shifting
                overridePendingTransition(0, 0);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        };
        base.getMyRef().addListenerForSingleValueEvent(Listener);
    }

    /**
     * Helper method to contain the logic for navigation bar to navigate to the graph page
     */
    private void graphPageHandler() {
        base.getMyRef().addListenerForSingleValueEvent(new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified
            // database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent i = new Intent(getBaseContext(), GraphsActivity.class);
                //set up the date for monthly data
                Calendar today = Calendar.getInstance();
                int month = today.get(Calendar.MONTH);
                int year = today.get(Calendar.YEAR);
                //Retrieve the monthly data from the database
                thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                //Add the past month's history (includes current)e
                i.putExtra(Graphs_DATA_INTENT, thisMonthsData);
                //Add the past month's history (includes current)e
                i.putExtra(LIST_OF_MONTHS, base.getPastMonthSummary(dataSnapshot));
                startActivityForResult(i, 1);
                //avoid shifting
                overridePendingTransition(0, 0);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        });
    }

    /**
     * Helper method to contain the logic for navigation bar to navigate to the history page
     */
    private void historyPageHandler() {
        ValueEventListener Listener = new ValueEventListener() {
            //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent i = new Intent(getBaseContext(), HistoryActivity.class);
                //set up the date for monthly data
                Calendar today = Calendar.getInstance();
                int month = today.get(Calendar.MONTH);
                int year = today.get(Calendar.YEAR);
                //Retrieve the monthly data from the database
                thisMonthsData = base.RetrieveDataCurrent(dataSnapshot, thisMonthsData, year, month);
                //put extra data into new intent
                i.putExtra(HISTORY_DATA_INTENT, thisMonthsData);
                //Add the past month's history (includes current)e
                i.putExtra(LIST_OF_MONTHS, base.getPastMonthSummary(dataSnapshot));
                startActivityForResult(i, 1);
                //avoid shifting
                overridePendingTransition(0, 0);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
            }
        };
        base.getMyRef().addListenerForSingleValueEvent(Listener);
    }

    //Helper method to control the functionality of bottom navigation bar
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // switch statement to handle all the icons in the bottom nav bar
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            homePageHandler();
                            return true;
                        case R.id.navigation_lists:
                            listsPageHandler();
                            return true;
                        case R.id.navigation_history:
                            historyPageHandler();
                            return true;
                        case R.id.navigation_graphs:
                            graphPageHandler();
                            return true;
                        case R.id.navigation_settings:
                            return true;
                    }
                    return false;
                }
            };

    /**
     *
     *
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
