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

import android.content.Context;
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
    private ArrayList<Car> cars;
    private ArrayList<Coin> coins;

    private int screenWidth, screenHeight;
    private int score;
    private long lastCarTime, lastCoinTime;
    private int carFrequency = 1000; // milliseconds
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
        cars = new ArrayList<>();
        coins = new ArrayList<>();
        score = 0;
        hud.setScore(0); // Reset HUD score
        isGameOver = false;
        lastCarTime = lastCoinTime = System.currentTimeMillis();

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

        // Generate cars with guaranteed escape path
        if (currentTime - lastCarTime > carFrequency) {
            // Create a map to track danger zones in each lane
            boolean[] laneDanger = new boolean[laneCount];

            // Track how far down the screen cars have traveled in each lane
            float[] laneCarProgress = new float[laneCount];
            for (int i = 0; i < laneCount; i++) {
                laneCarProgress[i] = screenHeight; // Initialize to screen bottom
            }

            // Check existing cars to determine danger zones
            // A lane is dangerous if a car is in the top 70% of the screen
            for (Car car : cars) {
                if (car.posY < screenHeight * 0.7) {
                    int carLane = getLaneFromX(car.posX, car.width);
                    if (carLane >= 0 && carLane < laneCount) {
                        laneDanger[carLane] = true;
                        laneCarProgress[carLane] = Math.min(laneCarProgress[carLane], car.posY);
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
                if (!laneDanger[i] || laneCarProgress[i] > screenHeight * 0.4) {
                    escapeLanes.add(i);
                }
            }

            // If there's only one escape lane and it's not the chicken's lane, don't spawn a car there
            if (escapeLanes.size() == 1 && escapeLanes.get(0) != chickenLane) {
                int onlyEscapeLane = escapeLanes.get(0);

                // Choose from lanes other than the only escape lane
                ArrayList<Integer> spawnLanes = new ArrayList<>();
                for (int i = 0; i < laneCount; i++) {
                    if (i != onlyEscapeLane && (laneCarProgress[i] > screenHeight * 0.3)) {
                        spawnLanes.add(i);
                    }
                }

                // Only spawn a car if there's a valid lane
                if (!spawnLanes.isEmpty()) {
                    int selectedLane = spawnLanes.get(random.nextInt(spawnLanes.size()));
                    Car newCar = new Car(getContext(), screenWidth, screenHeight, laneCount, random.nextInt(8), selectedLane);
                    cars.add(newCar);
                    lastCarTime = currentTime;
                }
            }
            // If there are multiple escape lanes, we can spawn a car in one
            else if (escapeLanes.size() > 1) {
                // Never spawn a car in the chicken's lane if it's one of several escape lanes
                if (escapeLanes.contains(chickenLane)) {
                    escapeLanes.remove(Integer.valueOf(chickenLane));
                }

                // Select a random lane from the remaining escape lanes
                if (!escapeLanes.isEmpty()) {
                    int selectedLane = escapeLanes.get(random.nextInt(escapeLanes.size()));
                    Car newCar = new Car(getContext(), screenWidth, screenHeight, laneCount, random.nextInt(8), selectedLane);
                    cars.add(newCar);
                    lastCarTime = currentTime;
                }
            }
            // If there are no escape lanes, don't spawn a car at all
            else {
                lastCarTime = currentTime; // Reset timer
            }

            // Gradually increase difficulty by reducing spawn time
            // but keep a minimum threshold to ensure game remains playable
            carFrequency = Math.max(1000 - (score * 3), 600);
        }

        // Generate coins with similar logic to ensure they don't block escape paths
        if (currentTime - lastCoinTime > coinFrequency) {
            // Don't spawn coins in lanes that already have cars near the top
            boolean[] laneBusy = new boolean[laneCount];

            for (Car car : cars) {
                if (car.posY < screenHeight * 0.4) {
                    int carLane = getLaneFromX(car.posX, car.width);
                    if (carLane >= 0 && carLane < laneCount) {
                        laneBusy[carLane] = true;
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
                coins.add(new Coin(getContext(), screenWidth, screenHeight, laneCount, selectedLane));
                lastCoinTime = currentTime;
            } else {
                lastCoinTime = currentTime; // Reset timer
            }
        }

        // Update cars
        Iterator<Car> carIterator = cars.iterator();
        while (carIterator.hasNext()) {
            Car car = carIterator.next();
            car.update();

            // Check for collision with chicken
            if (car.isColliding(chicken)) {
                SoundManager.getInstance(getContext()).playCrashSound();
                isGameOver = true;
            }

            // Remove off-screen cars
            if (car.isOffScreen(screenHeight)) {
                carIterator.remove();
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

    private int getLaneFromX(float posX, float width) {
        float laneWidth = screenWidth / laneCount;
        float objectCenterX = posX + width / 2;
        return (int)(objectCenterX / laneWidth);
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

            // Draw cars
            for (Car car : cars) {
                car.draw(canvas);
            }

            // Draw chicken
            chicken.draw(canvas);

            // Draw game over message if applicable
            if (isGameOver) {
                // Semi-transparent overlay
                paint.setColor(Color.argb(120, 0, 0, 0));
                canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

                paint.setColor(Color.RED);
                paint.setTextSize(100);
                String gameOver = "GAME OVER";
                float textWidth = paint.measureText(gameOver);
                canvas.drawText(gameOver, (screenWidth - textWidth) / 2, screenHeight / 2, paint);

                paint.setColor(Color.WHITE);
                paint.setTextSize(60);
                String tapToRestart = "Tap to restart";
                textWidth = paint.measureText(tapToRestart);
                canvas.drawText(tapToRestart, (screenWidth - textWidth) / 2, screenHeight / 2 + 100, paint);
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
     * Pauses the game loop and stops the game thread.
     */
    public void pause() {
        isPlaying = false;
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
                    // Restart game on tap if game over
                    resetGame();
                    return true;
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