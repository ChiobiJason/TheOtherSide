/*
 * This file contains the SwipeGestureDetector class which extends GestureDetector.SimpleOnGestureListener
 * to handle swipe gesture detection for the game interface. The class processes touch
 * events to determine horizontal swipe direction and notifies the GameView of these actions.
 *
 * The class manages:
 * - Touch event processing for swipe detection
 * - Direction determination (left vs right)
 * - Threshold-based qualification of swipe gestures
 * - Communication with GameView for swipe event callbacks
 *
 */

package com.example.theotherside;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
    // Threshold values for swipe detection in pixels
    private static final int SWIPE_DISTANCE_THRESHOLD_PX = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD_PX_PER_SEC = 100;

    // Reference to the game view for callback methods
    private GameView gameViewReference;

    /**
     * Constructs a new SwipeGestureDetector connected to the
     * specified GameView for handling swipe events.
     *
     * @param gameViewReference - the GameView instance that will receive
     *                   swipe event callbacks
     */
    public SwipeGestureDetector(GameView gameViewReference) {
        this.gameViewReference = gameViewReference;
    } //SwipeGestureDetector

    /**
     * Called when the user performs a down motion event.
     * Always returns true to indicate the event was handled.
     *
     * @param touchEvent - the MotionEvent that contains the down action
     * @return - true to indicate event was handled
     */
    @Override
    public boolean onDown(MotionEvent touchEvent) {
        return true;
    }

    /**
     * Detects fling gestures and determines if they qualify as
     * left or right swipes based on distance moved and velocity.
     * Only horizontal swipes that meet the threshold criteria
     * will trigger callbacks to the GameView.
     *
     * @param startTouchEvent - first down motion event that started the fling
     * @param endTouchEvent - move motion event that triggered the fling
     * @param horizontalVelocity - velocity of the fling on the X axis (pixels/second)
     * @param verticalVelocity - velocity of the fling on the Y axis (pixels/second)
     * @return - true if the fling was handled, false otherwise
     */
    @Override
    public boolean onFling(MotionEvent startTouchEvent, MotionEvent endTouchEvent,
                           float horizontalVelocity, float verticalVelocity) {
        boolean swipeDetected = false;
        try {
            float verticalDistanceMoved = endTouchEvent.getY() - startTouchEvent.getY();
            float horizontalDistanceMoved = endTouchEvent.getX() - startTouchEvent.getX();

            // Check if the gesture was more horizontal than vertical
            if (Math.abs(horizontalDistanceMoved) > Math.abs(verticalDistanceMoved)) {
                if (Math.abs(horizontalDistanceMoved) > SWIPE_DISTANCE_THRESHOLD_PX &&
                        Math.abs(horizontalVelocity) > SWIPE_VELOCITY_THRESHOLD_PX_PER_SEC) {
                    if (horizontalDistanceMoved > 0) {
                        // Right swipe
                        gameViewReference.onSwipeRight();
                    } else {
                        // Left swipe
                        gameViewReference.onSwipeLeft();
                    }
                    swipeDetected = true;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return swipeDetected;
    }
}