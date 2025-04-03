package com.example.theotherside;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Coin extends GameObject {
    private static Random random = new Random();

    public Coin(Context context, float screenWidth, float screenHeight, int laneCount) {
        super(0, 0, BitmapFactory.decodeResource(context.getResources(), R.drawable.coin));

        float laneWidth = screenWidth / laneCount;

        // Random lane
        int lane = random.nextInt(laneCount);

        // Position horizontally in lane
        this.x = lane * laneWidth + (laneWidth - width) / 2;

        // Start above screen
        this.y = -height;

        // Fixed speed
        this.speed = 5;

        update();
    }

    public void update() {
        y += speed;
        super.update();
    }

    public boolean isOffScreen(float screenHeight) {
        return y > screenHeight;
    }
}
