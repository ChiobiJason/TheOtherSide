/*
 * This file contains the GameView class which serves as the main game engine
 * and rendering surface for the game. It handles game logic, object management,
 * collision detection, score tracking, and user input processing.
 *
 * The class manages:
 * - Game loop and timing
 * - Object spawning and updates
 * - Collision detection
 * - Score tracking
 * - Touch input and swipe detection
 * - Game state management
 * - Rendering of all game elements
 *
 */

package com.example.theotherside;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Main game view class that handles the game loop, rendering, and game logic.
 * Implements Runnable to run the game loop in a separate thread and extends
 * SurfaceView for efficient rendering.
 */
public class GameView extends SurfaceView implements Runnable {
    private long gameStartTime;
    private float distanceTraveled;
    private int currentScore;
    private static final float BASE_SPEED = 0.2f;

    private Thread gameThread;
    private SurfaceHolder holder;
    private boolean isPlaying;
    private boolean isGameOver;

    private Paint paint;
    private Canvas canvas;
    private Bitmap backgroundBitmap;
    private HUD hud;


    private Chicken chicken;
    private ArrayList<Cart> carts;
    private ArrayList<Coin> coins;

    private int screenWidth, screenHeight;
    private int score;
    private long lastCartTime, lastCoinTime;
    private int cartFrequency = 1000; // milliseconds
    private int coinFrequency = 2000; // milliseconds
    private int coinsCollected;
    private int laneCount = 4;
    private Random random;
    private float touchStartX;
    private float touchStartY;
    private static final int MIN_SWIPE_DISTANCE = 100;

    /**
     * Creates a new game view with the specified dimensions.
     *
     * @param context - The application context
     * @param screenWidth - The width of the game screen
     * @param screenHeight - The height of the game screen
     */
    public GameView(Context context, int screenWidth, int screenHeight) {
        super(context);

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        holder = getHolder();
        paint = new Paint();
        random = new Random();

        // Load background bitmap
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.road);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenWidth,
                screenHeight, false);
        // Initialize HUD
        hud = new HUD(context, screenWidth, screenHeight);

        // Initialize game objects
        resetGame();
    }

    /**
     * Resets the game state to initial values.
     * Creates new game objects and resets score and timers.
     */
    private void resetGame() {
        chicken = new Chicken(getContext(), screenWidth, screenHeight, laneCount);
        carts = new ArrayList<>();
        coins = new ArrayList<>();
        score = 0;
        hud.setScore(0); // Reset HUD score
        isGameOver = false;
        lastCartTime = lastCoinTime = System.currentTimeMillis();

        gameStartTime = System.currentTimeMillis();
        distanceTraveled = 0f;

        // Start countdown when game is reset
        hud.startCountdown();
    }

    /**
     * Main game loop that updates game state and renders the game.
     */
    @Override
    public void run() {
        while (isPlaying) {
            // Only update if not paused and not counting down
            if (!hud.isPaused() && !hud.isCountingDown()) {
                update();
            }

            // Always update the countdown if it's active
            hud.updateCountdown();

            // Always draw, even when paused
            draw();
            control();
        }
    }

    /**
     * Updates the game state including object positions,
     * collision detection, and object spawning.
     */
    private void update() {
        if (!isGameOver && !hud.isPaused() && !hud.isCountingDown()) {
            long currentTime = System.currentTimeMillis();
            distanceTraveled = ((currentTime - gameStartTime) * BASE_SPEED);
            hud.setDistance(distanceTraveled); // update HUD
        }

        if (isGameOver) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Generate carts with guaranteed escape path
        if (currentTime - lastCartTime > cartFrequency) {
            // Create a map to track danger zones in each lane
            boolean[] laneDanger = new boolean[laneCount];

            // Track how far down the screen carts have traveled in each lane
            float[] laneCartProgress = new float[laneCount];
            for (int i = 0; i < laneCount; i++) {
                laneCartProgress[i] = screenHeight; // Initialize to screen bottom
            }

            // Check existing carts to determine danger zones
            // A lane is dangerous if a cart is in the top 70% of the screen
            for (Cart cart : carts) {
                if (cart.posY < screenHeight * 0.7) {
                    int cartLane = getLaneFromX(cart.posX, cart.width);
                    if (cartLane >= 0 && cartLane < laneCount) {
                        laneDanger[cartLane] = true;
                        laneCartProgress[cartLane] = Math.min(laneCartProgress[cartLane], cart.posY);
                    }
                }
            }

            // Get the lane the chicken is currently in
            int chickenLane = getLaneFromX(chicken.posX, chicken.width);

            // Identify possible escape lanes
            ArrayList<Integer> escapeLanes = new ArrayList<>();
            for (int i = 0; i < laneCount; i++) {
                // A lane is an escape lane if:
                // 1. It's not dangerous, OR
                // 2. The danger is far enough away to escape to another lane
                if (!laneDanger[i] || laneCartProgress[i] > screenHeight * 0.4) {
                    escapeLanes.add(i);
                }
            }

            // If there's only one escape lane and it's not the chicken's lane, don't spawn a cart there
            if (escapeLanes.size() == 1 && escapeLanes.get(0) != chickenLane) {
                int onlyEscapeLane = escapeLanes.get(0);

                // Choose from lanes other than the only escape lane
                ArrayList<Integer> spawnLanes = new ArrayList<>();
                for (int i = 0; i < laneCount; i++) {
                    if (i != onlyEscapeLane && (laneCartProgress[i] > screenHeight * 0.3)) {
                        spawnLanes.add(i);
                    }
                }

                // Only spawn a cart if there's a valid lane
                if (!spawnLanes.isEmpty()) {
                    int selectedLane = spawnLanes.get(random.nextInt(spawnLanes.size()));
                    Cart newCart = new Cart(getContext(), screenWidth, screenHeight,
                            laneCount, random.nextInt(10), selectedLane);
                    carts.add(newCart);
                    lastCartTime = currentTime;
                }
            }
            // If there are multiple escape lanes, we can spawn a cart in one
            else if (escapeLanes.size() > 1) {
                // Never spawn a cart in the chicken's lane if it's one of several escape lanes
                if (escapeLanes.contains(chickenLane)) {
                    escapeLanes.remove(Integer.valueOf(chickenLane));
                }

                // Select a random lane from the remaining escape lanes
                if (!escapeLanes.isEmpty()) {
                    int selectedLane = escapeLanes.get(random.nextInt(escapeLanes.size()));
                    Cart newCart = new Cart(getContext(), screenWidth, screenHeight,
                            laneCount, random.nextInt(10), selectedLane);
                    carts.add(newCart);
                    lastCartTime = currentTime;
                }
            }
            // If there are no escape lanes, don't spawn a cart at all
            else {
                lastCartTime = currentTime; // Reset timer
            }

            // Gradually increase difficulty by reducing spawn time
            // but keep a minimum threshold to ensure game remains playable
            cartFrequency = Math.max(1000 - (score * 3), 600);
        }

        // Generate coins with similar logic to ensure they don't block escape paths
        if (currentTime - lastCoinTime > coinFrequency) {
            // Don't spawn coins in lanes that already have carts near the top
            boolean[] laneBusy = new boolean[laneCount];

            for (Cart cart : carts) {
                if (cart.posY < screenHeight * 0.4) {
                    int cartLane = getLaneFromX(cart.posX, cart.width);
                    if (cartLane >= 0 && cartLane < laneCount) {
                        laneBusy[cartLane] = true;
                    }
                }
            }

            // Also check for existing coins
            for (Coin coin : coins) {
                if (coin.posY < screenHeight * 0.3) {
                    int coinLane = getLaneFromX(coin.posX, coin.width);
                    if (coinLane >= 0 && coinLane < laneCount) {
                        laneBusy[coinLane] = true;
                    }
                }
            }

            // Get chicken's lane
            int chickenLane = getLaneFromX(chicken.posX, chicken.width);

            // Find all available lanes for coins
            ArrayList<Integer> availableLanes = new ArrayList<>();
            for (int i = 0; i < laneCount; i++) {
                if (!laneBusy[i]) {
                    availableLanes.add(i);
                }
            }

            // Spawn coin if there's at least one available lane
            if (!availableLanes.isEmpty()) {
                int selectedLane = availableLanes.get(random.nextInt(availableLanes.size()));
                coins.add(new Coin(getContext(), screenWidth, screenHeight,
                        laneCount, selectedLane));
                lastCoinTime = currentTime;
            } else {
                lastCoinTime = currentTime; // Reset timer
            }
        }

        // Update carts
        Iterator<Cart> cartIterator = carts.iterator();
        while (cartIterator.hasNext()) {
            Cart cart = cartIterator.next();
            cart.update();

            // Check for collision with chicken
            if (cart.isColliding(chicken)) {
                SoundManager.getInstance(getContext()).playCrashSound();
                isGameOver = true;

                currentScore = Math.round(distanceTraveled/100);
                saveHighScore(currentScore);
                saveCoins(coinsCollected);
            }

            // Remove off-screen carts
            if (cart.isOffScreen(screenHeight)) {
                cartIterator.remove();
            }
        }

        // Update coins
        Iterator<Coin> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            Coin coin = coinIterator.next();
            coin.update();
            // Check for collision with chicken
            // when collecting coins:
            if (coin.isColliding(chicken)) {
                SoundManager.getInstance(getContext()).playCoinSound();
                coinsCollected++;
                hud.setCoins(coinsCollected); // Update HUD
                coinIterator.remove();
            }
            // Remove off-screen coins
            if (coin.isOffScreen(screenHeight)) {
                coinIterator.remove();
            }
        }
        // Update HUD score
        hud.setScore(distanceTraveled);;
    }

    /**
     * Saves the high score if the new score is greater than the stored high score.
     *
     * @param newScore - The new score to compare with the stored high score
     */
    private void saveHighScore(int newScore) {
        SharedPreferences prefs = getContext().getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        int storedHighScore = prefs.getInt("highScore", 0);

        if (newScore > storedHighScore) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highScore", newScore);
            editor.apply();
        }
    }

    /**
     * Determines the lane index based on the x position and width of an object.
     *
     * @param posX - The x position of the object
     * @param width - The width of the object
     * @return The lane index where the object is located
     */
    private int getLaneFromX(float posX, float width) {
        float laneWidth = screenWidth / laneCount;
        float objectCenterX = posX + width / 2;
        return (int)(objectCenterX / laneWidth);
    }

    /**
     * Saves the total number of coins collected by adding to the stored coin count.
     *
     * @param numOfCoinsCollected - The number of coins collected in the current session
     */
    private void saveCoins(int numOfCoinsCollected) {
        SharedPreferences prefs2 = getContext().getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        int storedCoinCount = prefs2.getInt("coinCount", 0);

        SharedPreferences.Editor editor = prefs2.edit();
        editor.putInt("coinCount", numOfCoinsCollected + storedCoinCount);
        editor.apply();
    }


    /**
     * Renders all game elements to the screen.
     * Includes background, game objects, score, and game over message.
     */
    private void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            // Draw background
            canvas.drawBitmap(backgroundBitmap, 0, 0, paint);

            // Draw coins
            for (Coin coin : coins) {
                coin.draw(canvas);
            }

            // Draw carts
            for (Cart cart : carts) {
                cart.draw(canvas);
            }

            // Draw chicken
            chicken.draw(canvas);

            // Draw game over message if applicable
            if (isGameOver) {
                // Semi-transparent overlay
                paint.setColor(Color.argb(120, 0, 0, 0));
                canvas.drawRect(0, screenHeight/2 - 150, screenWidth, screenHeight/2 + 250, paint);

                // Game Over text
                paint.setColor(Color.RED);
                paint.setTextSize(100);
                String gameOver = "GAME OVER";
                float textWidth = paint.measureText(gameOver);
                canvas.drawText(gameOver, (screenWidth - textWidth) / 2, screenHeight / 2, paint);

                // Restart text
                paint.setColor(Color.WHITE);
                paint.setTextSize(60);
                String tapToRestart = "Tap to restart";
                textWidth = paint.measureText(tapToRestart);
                float restartY = screenHeight / 2 + 100;
                canvas.drawText(tapToRestart, (screenWidth - textWidth) / 2, restartY, paint);

                // Back to Home text
                String backToHome = "Back to Home";
                textWidth = paint.measureText(backToHome);
                float homeY = restartY + 100;
                canvas.drawText(backToHome, (screenWidth - textWidth) / 2, homeY, paint);
            }

            // Draw HUD on top of everything (after game over overlay if present)
            hud.draw(canvas);

            holder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Controls the game loop timing to maintain approximately 60 FPS.
     */
    private void control() {
        try {
            Thread.sleep(17); // ~60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pauses the game loop, saves game state and stops the game thread.
     */
    public void pause() {
        isPlaying = false;
        saveCoins(coinsCollected);
        saveHighScore(currentScore);

        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resumes the game loop and starts the game thread.
     */
    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Handles right swipe gesture by moving the chicken right.
     */
    public void onSwipeRight() {
        SoundManager.getInstance(getContext()).playJumpSound();
        if (!isGameOver) {
            chicken.moveRight();
        }
    }

    /**
     * Handles left swipe gesture by moving the chicken left.
     */
    public void onSwipeLeft() {
        SoundManager.getInstance(getContext()).playJumpSound();
        if (!isGameOver) {
            chicken.moveLeft();
        }
    }

    /**
     * Processes touch events for game control.
     * Handles swipe gestures and game restart on game over.
     *
     * @param event - The motion event to process
     * @return true if the event was handled, false otherwise
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStartX = event.getX();
                touchStartY = event.getY();

                // Check if the pause/play button was pressed
                if (hud.checkButtonPress(touchStartX, touchStartY)) {
                    if (!isGameOver) {
                        hud.togglePause();
                    }
                    return true;
                }

                if (isGameOver) {
                    float tapY = event.getY();
                    float centerY = screenHeight / 2;

                    // Check if tap is in restart area (100px around restart text)
                    if (tapY > centerY + 50 && tapY < centerY + 150) {
                        resetGame();
                        return true;
                    }
                    // Check if tap is in home area (100px around home text)
                    else if (tapY > centerY + 150 && tapY < centerY + 250) {
                        // Return to main activity
                        getContext().startActivity(new Intent(getContext(), ScreenHighScore.class));
                        ((Activity) getContext()).finish();
                        return true;
                    }
                }
                return true;

            case MotionEvent.ACTION_UP:
                // Don't process swipes if the game is paused, counting down, or game over
                if (!hud.isPaused() && !hud.isCountingDown() && !isGameOver) {
                    float touchEndX = event.getX();
                    float touchEndY = event.getY();

                    // Calculate the difference
                    float diffX = touchEndX - touchStartX;
                    float diffY = touchEndY - touchStartY;

                    // Check if the gesture was a horizontal swipe
                    if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > MIN_SWIPE_DISTANCE) {
                        if (diffX > 0) {
                            // Swipe right
                            chicken.moveRight();
                        } else {
                            // Swipe left
                            chicken.moveLeft();
                        }
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }
}