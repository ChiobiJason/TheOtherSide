/*
 * This file contains the Chicken class which extends GameObject to create
 * the player-controlled character that moves between lanes at the bottom of
 * the screen. The class handles chicken initialization, movement controls,
 * and lane-based positioning.
 *
 * The class manages:
 * - Chicken positioning in lanes
 * - Left and right movement between lanes
 * - Lane boundary checking
 * - Initial placement at screen bottom
 *
 */

package com.example.theotherside;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * Represents the player-controlled chicken character in the game that moves
 * between lanes at the bottom of the screen. Extends GameObject to inherit
 * basic game object functionality while adding chicken-specific behaviors
 * and properties.
 */
public class Chicken extends GameObject {
    private int currentLane;
    private int laneCount;
    private float laneWidth;
    private float screenHeight;


    /**
     * Creates a new chicken instance with specified parameters.
     * Places the chicken in the middle lane at the bottom of the screen.
     *
     * @param context - The application context used to load chicken resources
     * @param screenWidth - The width of the game screen
     * @param screenHeight - The height of the game screen
     * @param laneCount - The number of lanes available for movement
     */
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

    /**
     * Moves the chicken one lane to the left if not already in the leftmost lane.
     * Updates the chicken's position and hitbox after movement.
     */
    public void moveLeft() {
        if (currentLane > 0) {
            currentLane--;
            posX = currentLane * laneWidth + (laneWidth - width) / 2;
            update();
        }
    }

    /**
     * Moves the chicken one lane to the right if not already in the rightmost lane.
     * Updates the chicken's position and hitbox after movement.
     */
    public void moveRight() {
        if (currentLane < laneCount - 1) {
            currentLane++;
            posX = currentLane * laneWidth + (laneWidth - width) / 2;
            update();
        }
    }
}
