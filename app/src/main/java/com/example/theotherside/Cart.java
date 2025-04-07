/*
 * This file contains the Cart class which extends GameObject to create
 * vehicles that move down the screen in specific lanes. The class handles
 * cart initialization, movement, and resource management for different cart types.
 *
 * The class manages:
 * - Cart positioning in lanes
 * - Cart movement and speed
 * - Different cart sprite selection
 * - Screen boundary detection
 *
 */

package com.example.theotherside;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.Random;

/**
 * Represents a cart object in the game that moves down the screen in a specific lane.
 * Extends GameObject to inherit basic game object functionality while adding
 * cart-specific behaviors and properties.
 */
public class Cart extends GameObject {
    private static Random random = new Random();
    private static final float FIXED_SPEED = 8;

    /**
     * Creates a new cart instance with specified parameters.
     *
     * @param context - The application context used to load cart resources
     * @param screenWidth - The width of the game screen
     * @param screenHeight - The height of the game screen
     * @param laneCount - The number of lanes available for cart placement
     * @param cartType - The type of cart to create (determines sprite)
     */
    public Cart(Context context, float screenWidth, float screenHeight, int laneCount, int cartType, int lane) {
        super(0, 0, BitmapFactory.decodeResource(context.getResources(), getCartResourceId(cartType)));
        float laneWidth = screenWidth / laneCount;

        // Use the provided lane instead of a random one
        this.posX = lane * laneWidth + (laneWidth - width) / 2;

        // Randomize the starting position slightly within the lane to avoid carts appearing in a line
        this.posX += (random.nextFloat() * 10) - 5; // Shift by -5 to +5 pixels

        // Vary starting position vertically to avoid carts being exactly lined up
        this.posY = -height - (random.nextFloat() * 100);

        // Randomize speed slightly to avoid carts bunching up
        this.speed = FIXED_SPEED + (random.nextFloat() * 3) - 1.5f; // FIXED_SPEED +/- 1.5

        update();
    }
    public Cart(Context context, float screenWidth, float screenHeight, int laneCount, int cartType) {
        this(context, screenWidth, screenHeight, laneCount, cartType, random.nextInt(laneCount));
    }


    /**
     * Determines which cart sprite to use based on the cart type.
     * Maps cart types to specific resource IDs using modulo operation
     * to cycle through available cart sprites.
     *
     * @param cartType - The type of cart to get the resource ID for
     * @return The resource ID for the specified cart type
     */
    private static int getCartResourceId(int cartType) {
        if (cartType % 2 == 0) {
            return R.drawable.cart_nohay;
        } else {
            return R.drawable.cart_hay;
        }
    }

    /**
     * Updates the cart's position by moving it down the screen at its fixed speed.
     * Calls the parent class's update method to maintain the hitbox position.
     */
    public void update() {
        posY += speed;
        super.update();
    }

    /**
     * Checks if the cart has moved beyond the bottom of the screen.
     *
     * @param screenHeight - The height of the game screen
     * @return true if the cart is below the screen, false otherwise
     */
    public boolean isOffScreen(float screenHeight) {
        return posY  > screenHeight;
    }
}