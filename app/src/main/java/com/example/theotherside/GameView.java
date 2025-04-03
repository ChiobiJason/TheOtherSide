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
    private Thread gameThread;
    private SurfaceHolder holder;
    private boolean isPlaying;
    private boolean isGameOver;

    private Paint paint;
    private Canvas canvas;
    private Bitmap backgroundBitmap;

    private Chicken chicken;
    private ArrayList<Car> cars;
    private ArrayList<Coin> coins;

    private int screenWidth, screenHeight;
    private int score;
    private long lastCarTime, lastCoinTime;
    private int carFrequency = 1000; // milliseconds
    private int coinFrequency = 2000; // milliseconds
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
        isGameOver = false;
        lastCarTime = lastCoinTime = System.currentTimeMillis();
    }

    /**
     * Main game loop that updates game state and renders the game.
     */
    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            control();
        }
    }

    /**
     * Updates the game state including object positions,
     * collision detection, and object spawning.
     */
    private void update() {
        if (isGameOver) {
            return;
        }

        // Generate cars
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCarTime > carFrequency) {
            cars.add(new Car(getContext(), screenWidth, screenHeight,
                    laneCount, random.nextInt(8)));
            lastCarTime = currentTime;

            // Increase difficulty over time
            carFrequency = Math.max(500, carFrequency - 5);
        }

        // Generate coins
        if (currentTime - lastCoinTime > coinFrequency) {
            coins.add(new Coin(getContext(), screenWidth, screenHeight, laneCount));
            lastCoinTime = currentTime;
        }

        // Update cars
        Iterator<Car> carIterator = cars.iterator();
        while (carIterator.hasNext()) {
            Car car = carIterator.next();
            car.update();

            // Check for collision with chicken
            if (car.isColliding(chicken)) {
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
            if (coin.isColliding(chicken)) {
                score++;
                coinIterator.remove();
            }

            // Remove off-screen coins
            if (coin.isOffScreen(screenHeight)) {
                coinIterator.remove();
            }
        }
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

            // Draw score
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            canvas.drawText("Score: " + score, 50, 80, paint);

            // Draw game over message if applicable
            if (isGameOver) {
                paint.setColor(Color.RED);
                paint.setTextSize(100);
                String gameOver = "GAME OVER";
                float textWidth = paint.measureText(gameOver);
                canvas.drawText(gameOver, (screenWidth - textWidth) / 2,
                        screenHeight / 2, paint);

                paint.setTextSize(60);
                String tapToRestart = "Tap to restart";
                textWidth = paint.measureText(tapToRestart);
                canvas.drawText(tapToRestart, (screenWidth - textWidth) / 2,
                        screenHeight / 2 + 100, paint);
            }

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
        if (!isGameOver) {
            chicken.moveRight();
        }
    }

    /**
     * Handles left swipe gesture by moving the chicken left.
     */
    public void onSwipeLeft() {
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

                if (isGameOver) {
                    // Restart game on tap if game over
                    resetGame();
                }
                return true;

            case MotionEvent.ACTION_UP:
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
                return true;
        }
        return super.onTouchEvent(event);
    }
}
