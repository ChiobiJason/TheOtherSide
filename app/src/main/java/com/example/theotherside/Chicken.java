package com.example.theotherside;

import android.content.Context;
import android.graphics.BitmapFactory;

public class Chicken extends GameObject {
    private int currentLane;
    private int laneCount;
    private float laneWidth;
    private float screenHeight;

    public Chicken(Context context, float screenWidth, float screenHeight, int laneCount) {
        super(0, 0, BitmapFactory.decodeResource(context.getResources(), R.drawable.chicken));

        this.laneCount = laneCount;
        this.laneWidth = screenWidth / laneCount;
        this.screenHeight = screenHeight;

        // Start in middle lane
        this.currentLane = laneCount / 2;

        // Position chicken at bottom of screen
        this.posX = currentLane * laneWidth + (laneWidth - width) / 2;
        this.posY = screenHeight - height - 50; // Small gap from bottom

        update();
    }

    public void moveLeft() {
        if (currentLane > 0) {
            currentLane--;
            posX = currentLane * laneWidth + (laneWidth - width) / 2;
            update();
        }
    }

    public void moveRight() {
        if (currentLane < laneCount - 1) {
            currentLane++;
            posX = currentLane * laneWidth + (laneWidth - width) / 2;
            update();
        }
    }
}
