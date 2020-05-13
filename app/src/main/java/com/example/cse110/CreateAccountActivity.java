package com.example.cse110;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        final Button createAccountButton = findViewById(R.id.loginButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameField = findViewById(R.id.username);
                String enteredUsername = usernameField.getText().toString();

                EditText passwordField = findViewById(R.id.password);
                String enteredPassword = passwordField.getText().toString();

                EditText confirmPasswordField = findViewById(R.id.confirmPassword);
                String enteredConfirmPassword = confirmPasswordField.getText().toString();

                final TextView userMessage = findViewById(R.id.userMessage);

                if (enteredPassword.equals(enteredConfirmPassword)) {
                    // TODO: check if the email already exists as a user

                    mAuth.createUserWithEmailAndPassword(enteredUsername, enteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Go back to login screen with a user message
                                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                                intent.putExtra(USER_MESSAGE_FIELD, "Account successfully created.");
                                startActivity(intent);
                            } else {
                                userMessage.setText("Authentication failed. " + task.getException().getLocalizedMessage());
                            }
                        }
                    });
                } else {
                    // Confirmed password doesn't match password
                    userMessage.setText("Passwords do not match!");
                }
            }
        });
    }
}
