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

        if (shopButton != null) {  // Null check to prevent crashes
            shopButton.setOnClickListener(v -> {
                Intent intent = new Intent(ScreenHighScore.this, Shop.class);
                startActivity(intent);
            });
        }
    }
}