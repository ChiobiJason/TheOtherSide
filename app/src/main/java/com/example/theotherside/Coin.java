/*
 * This file contains the Coin class which extends GameObject to create
 * collectible coins that move down the screen in specific lanes. The class handles
 * coin initialization, movement, and positioning within the game environment.
 *
 * The class manages:
 * - Coin positioning in lanes
 * - Coin movement and speed
 * - Screen boundary detection
 * - Random lane selection for coin placement
 *
 */

package com.example.theotherside;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Coin extends GameObject {
    private static Random random = new Random();

    /**
     * Creates a new coin instance with specified parameters.
     *
     * @param context - The application context used to load coin resources
     * @param screenWidth - The width of the game screen
     * @param screenHeight - The height of the game screen
     * @param laneCount - The number of lanes available for coin placement
     */
    public Coin(Context context, float screenWidth, float screenHeight, int laneCount) {
        super(0, 0, BitmapFactory.decodeResource(context.getResources(), R.drawable.coin));

        float laneWidth = screenWidth / laneCount;

        // Random lane
        int lane = random.nextInt(laneCount);

        // Position horizontally in lane
        this.posX = lane * laneWidth + (laneWidth - width) / 2;

        // Start above screen
        this.posY = -height;

        // Fixed speed
        this.speed = 5;

        update();
    }

    /**
     * Updates the coin's position by moving it down the screen at its fixed speed.
     * Calls the parent class's update method to maintain the hitbox position.
     */
    public void update() {
        posY += speed;
        super.update();
    }

    /**
     * Checks if the coin has moved beyond the bottom of the screen.
     *
     * @param screenHeight - The height of the game screen
     * @return true if the coin is below the screen, false otherwise
     */
    public boolean isOffScreen(float screenHeight) {
        return posY > screenHeight;
    }
}
