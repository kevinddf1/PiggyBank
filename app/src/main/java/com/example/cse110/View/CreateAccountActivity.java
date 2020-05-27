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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity {
    public static final String USER_MESSAGE_FIELD = "com.example.test.USER_MESSAGE_FIELD";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_layout);

        mAuth = FirebaseAuth.getInstance();

        // Extract username from previous screen
        Intent intent = getIntent();
        ((EditText)findViewById(R.id.username)).setText(intent.getStringExtra(LoginActivity.USERNAME_FIELD));

        // On-click listener for create account button
        final Button createAccountButton = findViewById(R.id.loginButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameField = findViewById(R.id.username);
                String enteredUsername = usernameField.getText().toString();

                EditText passwordField = findViewById(R.id.password);
                String enteredPassword = passwordField.getText().toString();

                EditText confirmPasswordField = findViewById(R.id.confirmPassword);
                String enteredConfirmPassword = confirmPasswordField.getText().toString();

                if (enteredUsername.length() == 0 || enteredPassword.length() == 0 || enteredConfirmPassword.length() == 0) {
                    Toast.makeText(getBaseContext(), "One or more fields are empty.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (enteredPassword.equals(enteredConfirmPassword)) {
                    mAuth.createUserWithEmailAndPassword(enteredUsername, enteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getBaseContext(), "Account created.", Toast.LENGTH_LONG).show();

                                // Go back to login screen with a user message
                                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                                intent.putExtra(USER_MESSAGE_FIELD, "Account successfully created.");
                                startActivity(intent);
                            } else {
                                Toast.makeText(getBaseContext(), "Authentication failed. " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    // Confirmed password doesn't match password
                    Toast.makeText(getBaseContext(), "Passwords do not match.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
