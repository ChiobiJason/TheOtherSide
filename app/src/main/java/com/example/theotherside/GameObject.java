/*
 * This file contains the base GameObject class that serves as a foundation for all
 * game objects in the application. It provides basic functionality for position,
 * movement, collision detection, and rendering of game objects.
 *
 * The class handles:
 * - Position and size management
 * - Collision detection using hitboxes
 * - Basic rendering of bitmap graphics
 * - Object state management (alive/dead)
 *
 */

package com.example.theotherside;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Base class for all game objects in the application.
 * Provides fundamental functionality for position, movement, collision detection,
 * and rendering of game objects.
 */
public class GameObject {
    protected float posX, posY;
    protected float width, height;
    protected float speed;
    protected Bitmap bitmap;
    protected boolean isAlive = true;
    protected Rect hitBox;

    /**
     * Creates a new game object with the specified position and bitmap.
     *
     * @param posX - The initial X coordinate of the game object
     * @param posY - The initial Y coordinate of the game object
     * @param bitmap - The bitmap image to be used for rendering the game object
     */
    public GameObject(float posX, float posY, Bitmap bitmap) {
        this.posX = posX;
        this.posY = posY;
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.hitBox = new Rect((int)posX, (int)posY,
                (int)(posX + width), (int)(posY + height));
    }

    /**
     * Updates the game object's state.
     * Currently updates the position of the hitbox to match the object's position.
     * This method should be called every frame to maintain accurate collision detection.
     */
    public void update() {
        hitBox.left = (int)posX;
        hitBox.top = (int)posY;
        hitBox.right = (int)(posX + width);
        hitBox.bottom = (int)(posY + height);
    }

    /**
     * Renders the game object on the provided canvas.
     * Only renders if the object is alive.
     *
     * @param canvas - The canvas on which to draw the game object
     */
    public void draw(Canvas canvas) {
        if (isAlive) {
            canvas.drawBitmap(bitmap, posX, posY, null);
        }
    }

    /**
     * Checks if this game object is colliding with another game object.
     * Uses rectangular hitbox intersection for collision detection.
     *
     * @param other - The other game object to check for collision with
     * @return true if the objects are colliding, false otherwise
     */
    public boolean isColliding(GameObject other) {
        return Rect.intersects(hitBox, other.hitBox);
    }
}

