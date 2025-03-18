package com.example.theotherside;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Tutorial extends AppCompatActivity {

    // Declare UI elements
    private CheckBox dontShowTut;
    private ImageButton closeTutorialButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        dontShowTut = findViewById(R.id.dontShowTut);
        closeTutorialButton = findViewById(R.id.imageButton);

        SharedPreferences sharedPreferences = getSharedPreferences("tutorialPrefs", MODE_PRIVATE);
        boolean dontShowAgain = sharedPreferences.getBoolean("dontShowAgain", false);

        if (dontShowAgain) {
            finish();
        }

        dontShowTut.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dontShowAgain", isChecked);
            editor.apply();
        });

        // Set up the listener for the close (X) button
        closeTutorialButton.setOnClickListener(v -> {
            //proceed to the next screen but currently just close the tutorial since we don't have a next yet
            if (dontShowTut.isChecked()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("dontShowAgain", true);
                editor.apply();
            }

            finish();

            Toast.makeText(Tutorial.this, "Tutorial skipped.", Toast.LENGTH_SHORT).show();
        });
    }
}
