package com.example.cse110.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cse110.Model.Database;
import com.example.cse110.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {
    public static final String USERNAME_FIELD = "com.example.test.USERNAME_FIELD";

    private FirebaseAuth mAuth;
    private Database base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        mAuth = FirebaseAuth.getInstance();

        // On-click listener for login button
        final Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameField = findViewById(R.id.username);
                String enteredUsername = usernameField.getText().toString();

                EditText passwordField = findViewById(R.id.password);
                String enteredPassword = passwordField.getText().toString();

                if (enteredUsername.length() == 0 || enteredPassword.length() == 0) {
                    Toast.makeText(getBaseContext(), "One or more fields are empty.", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(enteredUsername, enteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Logged in.", Toast.LENGTH_LONG).show();
                            
                            base =  Database.Database(); // create a Database object
                            base.setKey();
                            ValueEventListener Listener = new ValueEventListener() {
                                //The onDataChange() method is called every time data is changed at the specified database reference, including changes to children.
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Intent i = new Intent(getBaseContext(), HistoryActivity.class);

                                    Calendar today = Calendar.getInstance();
                                    int month = today.get(Calendar.MONTH);
                                    int year = today.get(Calendar.YEAR);
                                    ArrayList<String> list =  base.RetrieveT_Budget_Exp(dataSnapshot, year, month);

                                    Bundle b = new Bundle();
                                    b.putStringArray("Total Budget and Expense", new String[]{list.get(0), list.get(1)});

                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    intent.putExtras(b);

                                    startActivity(intent);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Failed to read value
                                }
                            };
                            base.getMyRef().addListenerForSingleValueEvent(Listener);
                        } else {
                            Toast.makeText(getBaseContext(), "Login failed. " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        // On-click listener for account creation button
        final Button createAccountButton = findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameField = findViewById(R.id.username);
                String enteredUsername = usernameField.getText().toString();

                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                intent.putExtra(USERNAME_FIELD, enteredUsername);
                startActivity(intent);
            }
        });

        // On-click listener for account recovery button
        final Button accountRecoveryButton = findViewById(R.id.accountRecoveryButton);
        accountRecoveryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AccountRecoveryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Do nothing on back button press because we don't want the user to be able to go back to wherever they were
    }
}
