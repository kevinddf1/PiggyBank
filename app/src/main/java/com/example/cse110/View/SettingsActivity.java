package com.example.cse110.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cse110.Model.Database;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    public static final String HISTORY_DATA_INTENT = "HistoryActivity monthlyData";
    public static final String MONTHLY_DATA_INTENT = "CategoriesListActivity monthlyData";
    public static final String PIE_CHART_DATA_INTENT = "PieChartActivity monthlyData";
    private MonthlyData monthlyData;

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

        final Button changePasswordButton = findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Start account recovery activity
                Intent i = new Intent(getBaseContext(), AccountRecoveryActivity.class);
                startActivity(i);
            }
        });

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
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:

                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            setResult(RESULT_OK, intent);
                            startActivityForResult(intent, 1);
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.navigation_lists:
                            Intent inte = new Intent(getBaseContext(), CategoriesListActivity.class);
                            setResult(RESULT_OK, inte);
                            startActivityForResult(inte, 1);
                            overridePendingTransition(0, 0);
                            return true;

                        case R.id.navigation_history:
                            Intent i = new Intent(getBaseContext(), HistoryActivity.class);
                            setResult(RESULT_OK, i);
                            //i.putExtra(CategoriesListActivity.MONTHLY_DATA_INTENT, monthlyData);
                            startActivityForResult(i, 1);
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.navigation_graphs:
                            Intent inten = new Intent(getBaseContext(), PieChartActivity.class);
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
        super.onBackPressed();
    }
}
