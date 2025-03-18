package com.example.theotherside;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Tutorial extends AppCompatActivity {

    // UI elements
    private CheckBox dontShowTut;
    private ImageButton closeTutorialButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_how_to_play);

        dontShowTut = findViewById(R.id.dontShowTut);
        closeTutorialButton = findViewById(R.id.imageButton);

        SharedPreferences sharedPreferences = getSharedPreferences("tutorialPrefs", MODE_PRIVATE);
        boolean dontShowAgain = sharedPreferences.getBoolean("dontShowAgain", false);

        if (dontShowAgain) {
            navigateToScreenHighScore();
        }

        dontShowTut.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dontShowAgain", isChecked);
            editor.apply();
        });

        // The listener for the close (X) button
        closeTutorialButton.setOnClickListener(v -> {
            if (dontShowTut.isChecked()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("dontShowAgain", true);
                editor.apply();
            }

            Toast.makeText(Tutorial.this, "Tutorial skipped.", Toast.LENGTH_SHORT).show();
            navigateToScreenHighScore();
        });
    }

    // navigate to ScreenHighScore
    private void navigateToScreenHighScore() {
        Intent intent = new Intent(Tutorial.this, ScreenHighScore.class);
        startActivity(intent);
        finish(); // Tutorial activity is closed
    }
}