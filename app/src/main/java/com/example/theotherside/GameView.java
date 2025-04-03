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


    public GameView(Context context, int screenWidth, int screenHeight) {
        super(context);

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        holder = getHolder();
        paint = new Paint();
        random = new Random();

        // Load background bitmap
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.road);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenWidth, screenHeight, false);

        // Initialize game objects
        resetGame();
    }

    private void resetGame() {
        chicken = new Chicken(getContext(), screenWidth, screenHeight, laneCount);
        cars = new ArrayList<>();
        coins = new ArrayList<>();
        score = 0;
        isGameOver = false;
        lastCarTime = lastCoinTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        if (isGameOver) {
            return;
        }

        // Generate cars
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCarTime > carFrequency) {
            cars.add(new Car(getContext(), screenWidth, screenHeight, laneCount, random.nextInt(8)));
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
                canvas.drawText(gameOver, (screenWidth - textWidth) / 2, screenHeight / 2, paint);

                paint.setTextSize(60);
                String tapToRestart = "Tap to restart";
                textWidth = paint.measureText(tapToRestart);
                canvas.drawText(tapToRestart, (screenWidth - textWidth) / 2, screenHeight / 2 + 100, paint);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            Thread.sleep(17); // ~60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void onSwipeRight() {
        if (!isGameOver) {
            chicken.moveRight();
        }
    }

    public void onSwipeLeft() {
        if (!isGameOver) {
            chicken.moveLeft();
        }
    }

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
