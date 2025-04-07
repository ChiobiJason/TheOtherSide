package com.example.theotherside;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ScreenHighScore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_high_score);

        ImageButton shopButton = findViewById(R.id.shopButton);
        ImageButton startButton = findViewById(R.id.startGameButton);

        if (shopButton != null) {  // Null check to prevent crashes
            shopButton.setOnClickListener(v -> {
                SoundManager.getInstance(ScreenHighScore.this).playButtonClick();
                Intent intent = new Intent(ScreenHighScore.this, Shop.class);
                startActivity(intent);
            });
        }
        if (startButton != null) {  // Null check to prevent crashes
            startButton.setOnClickListener(v -> {
                SoundManager.getInstance(ScreenHighScore.this).playButtonClick();
                Intent intent = new Intent(ScreenHighScore.this, GameActivity.class);
                startActivity(intent);
            });
        }
    }
}