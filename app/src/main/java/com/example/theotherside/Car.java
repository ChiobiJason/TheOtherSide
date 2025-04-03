package com.example.theotherside;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Car extends GameObject {
    private static Random random = new Random();
    private static final float FIXED_SPEED = 8;

    public Car(Context context, float screenWidth, float screenHeight, int laneCount, int carType) {
        super(0, 0, BitmapFactory.decodeResource(context.getResources(),
                getCarResourceId(carType)));

        float laneWidth = screenWidth / laneCount;

        // Random lane
        int lane = random.nextInt(laneCount);

        // Position horizontally in lane
        this.x = lane * laneWidth + (laneWidth - width) / 2;

        // Start above screen
        this.y = -height;

        // Random speed between 5 and 15
        this.speed = FIXED_SPEED;

        update();
    }

    private static int getCarResourceId(int carType) {
        switch (carType % 8) {
            case 0: return R.drawable.car1;
            case 1: return R.drawable.car2;
            case 2: return R.drawable.car3;
            case 3: return R.drawable.car4;
            case 4: return R.drawable.car5;
            case 5: return R.drawable.car6;
            case 6: return R.drawable.car7;
            default: return R.drawable.car8;
        }
    }

    public void update() {
        y += speed;
        super.update();
    }

    public boolean isOffScreen(float screenHeight) {
        return y > screenHeight;
    }
}