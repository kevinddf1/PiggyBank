package com.example.cse110;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button expenseListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);


        //Bind button to go to expense list
        expenseListButton = findViewById(R.id.ExpenseListButton);

        expenseListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExpenseList();

            }
        });
    }

    private void openExpenseList(){
        Intent intent = new Intent(this, ExpenseList.class);
        startActivity(intent);
    }
}
