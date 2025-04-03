/*
 * This file contains the GameActivity class which serves as the main activity
 * for the game. It handles the initialization of the game view, screen dimensions,
 * and lifecycle management of the game.
 *
 * The class manages:
 * - Game view initialization
 * - Screen dimension retrieval
 * - Activity lifecycle events
 * - Game pause and resume functionality
 *
 */

package com.example.theotherside;

import android.graphics.Point;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Main activity class that hosts the game view and manages the game's lifecycle.
 * Extends AppCompatActivity to provide basic Android activity functionality while
 * adding game-specific initialization and lifecycle management.
 */
public class GameActivity extends AppCompatActivity {
    private GameView gameView;

    /**
     * Initializes the game activity and sets up the game view.
     * Retrieves screen dimensions and creates a new game view instance.
     *
     * @param savedInstanceState - Bundle containing the activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get screen dimensions
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        // Create game view with screen dimensions
        gameView = new GameView(this, size.x, size.y);
        setContentView(gameView);
    }

    /**
     * Handles the pause event of the activity.
     * Pauses the game view to stop game updates and rendering.
     */
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    /**
     * Handles the resume event of the activity.
     * Resumes the game view to restart game updates and rendering.
     */
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}
