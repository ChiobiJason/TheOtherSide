/*
 * This file contains the Car class which extends GameObject to create
 * vehicles that move down the screen in specific lanes. The class handles
 * car initialization, movement, and resource management for different car types.
 *
 * The class manages:
 * - Car positioning in lanes
 * - Car movement and speed
 * - Different car sprite selection
 * - Screen boundary detection
 *
 */

package com.example.theotherside;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.Random;

/**
 * Represents a car object in the game that moves down the screen in a specific lane.
 * Extends GameObject to inherit basic game object functionality while adding
 * car-specific behaviors and properties.
 */
public class Car extends GameObject {
    private static Random random = new Random();
    private static final float FIXED_SPEED = 8;

    /**
     * Creates a new car instance with specified parameters.
     *
     * @param context - The application context used to load car resources
     * @param screenWidth - The width of the game screen
     * @param screenHeight - The height of the game screen
     * @param laneCount - The number of lanes available for car placement
     * @param carType - The type of car to create (determines sprite)
     */
    public Car(Context context, float screenWidth, float screenHeight, int laneCount, int carType, int lane) {
        super(0, 0, BitmapFactory.decodeResource(context.getResources(), getCarResourceId(carType)));
        float laneWidth = screenWidth / laneCount;

        // Use the provided lane instead of a random one
        this.posX = lane * laneWidth + (laneWidth - width) / 2;

        // Randomize the starting position slightly within the lane to avoid cars appearing in a line
        this.posX += (random.nextFloat() * 10) - 5; // Shift by -5 to +5 pixels

        // Vary starting position vertically to avoid cars being exactly lined up
        this.posY = -height - (random.nextFloat() * 100);

        // Randomize speed slightly to avoid cars bunching up
        this.speed = FIXED_SPEED + (random.nextFloat() * 3) - 1.5f; // FIXED_SPEED +/- 1.5

        update();
    }
    public Car(Context context, float screenWidth, float screenHeight, int laneCount, int carType) {
        this(context, screenWidth, screenHeight, laneCount, carType, random.nextInt(laneCount));
    }


    /**
     * Determines which car sprite to use based on the car type.
     * Maps car types to specific resource IDs using modulo operation
     * to cycle through available car sprites.
     *
     * @param carType - The type of car to get the resource ID for
     * @return The resource ID for the specified car type
     */
    private static int getCarResourceId(int carType) {
        switch (carType % 8) {
            case 0:
                return R.drawable.red_car;
            case 1:
                return R.drawable.blue_car;
            case 2:
                return R.drawable.pink_car;
            case 3:
                return R.drawable.black_car;
            case 4:
                return R.drawable.orange_car;
            case 5:
                return R.drawable.green_car;
            case 6:
                return R.drawable.purple_car;
            default:
                return R.drawable.yellow_car;
        }
    }

    /**
     * Updates the car's position by moving it down the screen at its fixed speed.
     * Calls the parent class's update method to maintain the hitbox position.
     */
    public void update() {
        posY += speed;
        super.update();
    }

    /**
     * Checks if the car has moved beyond the bottom of the screen.
     *
     * @param screenHeight - The height of the game screen
     * @return true if the car is below the screen, false otherwise
     */
    public boolean isOffScreen(float screenHeight) {
        return posY  > screenHeight;
    }
}