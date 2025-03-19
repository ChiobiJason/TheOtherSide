package com.example.theotherside;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ScreenTitle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.screen_title);
        Button playNowButton = findViewById(R.id.playNowButton);

        playNowButton.setOnClickListener(v -> {
                // an Intent to start ScreenHighScore
                Intent intent = new Intent(ScreenTitle.this, ScreenHighScore.class);
                startActivity(intent);
            });
    }
}