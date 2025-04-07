package com.example.theotherside;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

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
//load highscore
        SharedPreferences prefs = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        int highScore = prefs.getInt("highScore", 0);

        TextView highScoreText = findViewById(R.id.highScore);
        highScoreText.setText(""+highScore);
    }
}