package com.example.cse110;

import android.content.Intent;
import android.graphics.Color;
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

public class Login extends AppCompatActivity {
    public static final String USERNAME_FIELD = "com.example.test.USERNAME_FIELD";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        final TextView userMessage = findViewById(R.id.userMessage);
        userMessage.setTextColor(Color.GREEN);
        userMessage.setText(intent.getStringExtra(CreateAccount.USER_MESSAGE_FIELD));

        final Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameField = findViewById(R.id.username);
                String enteredUsername = usernameField.getText().toString();

                EditText passwordField = findViewById(R.id.password);
                String enteredPassword = passwordField.getText().toString();

                mAuth.signInWithEmailAndPassword(enteredUsername, enteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userMessage.setTextColor(Color.GREEN);
                            userMessage.setText("Logging in...");

                            // TODO: open home page; do other stuff on login
                        } else {
                            userMessage.setTextColor(Color.RED);
                            userMessage.setText("Login failed. " + task.getException().getLocalizedMessage());
                        }
                    }
                });
            }
        });

        final Button createAccountButton = findViewById(R.id.signup);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText usernameField = findViewById(R.id.username);
                String enteredUsername = usernameField.getText().toString();

                Intent intent = new Intent(Login.this, CreateAccount.class);
                intent.putExtra(USERNAME_FIELD, enteredUsername);
                startActivity(intent);
            }
        });
    }
}
