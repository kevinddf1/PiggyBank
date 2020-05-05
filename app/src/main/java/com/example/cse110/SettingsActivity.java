package com.example.cse110;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    public static final String SETTINGS_INTENT = "SettingsActivity settings";

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        intent.putExtra(SETTINGS_INTENT, settings);
        super.onBackPressed();
    }
}
