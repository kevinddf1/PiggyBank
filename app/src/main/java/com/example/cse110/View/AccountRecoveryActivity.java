package com.example.cse110.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cse110.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AccountRecoveryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_recovery);

        mAuth = FirebaseAuth.getInstance();

        final Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameField = findViewById(R.id.email);
                String enteredUsername = usernameField.getText().toString();

                if (enteredUsername.length() == 0) {
                    Toast.makeText(getBaseContext(), "Email is empty.", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(enteredUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Email sent.", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(AccountRecoveryActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getBaseContext(), "Password reset failed. " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}
