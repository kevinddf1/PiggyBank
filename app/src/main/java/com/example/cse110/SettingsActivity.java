package com.example.cse110;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    public static final String SETTINGS_INTENT = "SettingsActivity settings";
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";
    private MonthlyData monthlyData;

    private Settings settings;
    private Database base = Database.Database(); // create a Database object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(1);
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
        navView.setOnNavigationItemSelectedListener(navListener);
        Intent intent = getIntent();
        settings = intent.getParcelableExtra(SETTINGS_INTENT);

        final Switch notificationsSwitch = findViewById(R.id.notifications_switch);
        // Initialize value
        notificationsSwitch.setChecked(settings.getEnableNotifications());
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setEnableNotifications(isChecked);
            }
        });

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

        final Button deleteAccountButton = findViewById(R.id.delete_account_button);
        deleteAccountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:

                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            setResult(RESULT_OK, intent);
                            if (settings == null) {
                                settings = new Settings();
                            }
                            intent.putExtra(SETTINGS_INTENT, settings);
                            startActivityForResult(intent, 1);
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.navigation_lists:
                            Intent inte = new Intent(getBaseContext(), CategoriesListActivity.class);
                            setResult(RESULT_OK, inte);
                            if (settings == null) {
                                settings = new Settings();
                            }
                            inte.putExtra(SETTINGS_INTENT, settings);
                            startActivityForResult(inte, 1);
                            overridePendingTransition(0, 0);
                            return true;

                        case R.id.navigation_history:
                            Intent i = new Intent(getBaseContext(), HistoryActivity.class);
                            setResult(RESULT_OK, i);
                            if (settings == null) {
                                settings = new Settings();
                            }
                            //i.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, monthlyData);
                            // TODO: grab this from the database
                            i.putExtra(SETTINGS_INTENT, settings);
                            startActivityForResult(i, 1);
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.navigation_graphs:
                            Intent inten = new Intent(getBaseContext(), PieChartActivity.class);
                            if (settings == null) {
                                settings = new Settings();
                            }
                            inten.putExtra(SETTINGS_INTENT, settings);
                            startActivityForResult(inten, 1);
                            overridePendingTransition(0, 0);

                            return true;
                        case R.id.navigation_settings:
                            return true;
                    }
                    return false;
                }
            };
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        intent.putExtra(SETTINGS_INTENT, settings);
        super.onBackPressed();
    }
}
