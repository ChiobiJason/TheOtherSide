package com.example.theotherside;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

public class HUD {
    private Paint paint, shadowPaint;
    private int screenWidth, screenHeight;
    private Bitmap playBitmap, pauseBitmap, coinBitmap;
    private boolean isPaused;
    private RectF hudBox;
    private int score;

    // Countdown variables
    private boolean isCountingDown;
    private int countdownValue; // 3, 2, 1, Go!
    private long lastCountdownTime;

    public HUD(Context context, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        paint = new Paint();
        shadowPaint = new Paint();
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setTextSize(50);
        shadowPaint.setAlpha(120);

        // Load button images
        playBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play);
        pauseBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pause);
        coinBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin);

        // Size and scale the button bitmaps
        int buttonSize = 60;
        playBitmap = Bitmap.createScaledBitmap(playBitmap, buttonSize, buttonSize, true);
        pauseBitmap = Bitmap.createScaledBitmap(pauseBitmap, buttonSize, buttonSize, true);
        coinBitmap = Bitmap.createScaledBitmap(coinBitmap, buttonSize, buttonSize, true);

        // Create unified HUD box (dynamic island style)
        int boxWidth = screenWidth / 2;
        int boxHeight = 110;
        int boxX = (screenWidth - boxWidth) / 2;
        int boxY = 80;

        hudBox = new RectF(boxX, boxY, boxX + boxWidth, boxY + boxHeight);

        // Init state
        isPaused = false;
        score = 0;

        // Countdown init
        isCountingDown = false;
        countdownValue = 3;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isCountingDown() {
        return isCountingDown;
    }

    public void startCountdown() {
        isCountingDown = true;
        countdownValue = 3;
        lastCountdownTime = System.currentTimeMillis();
    }

    public void updateCountdown() {
        if (!isCountingDown) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCountdownTime > 1000) { // 1 second intervals
            countdownValue--;
            lastCountdownTime = currentTime;

            if (countdownValue < 0) { // "Go!" is over
                isCountingDown = false;
                isPaused = false; // Resume game after countdown
            }
        }
    }

    public void togglePause() {
        isPaused = !isPaused;
        if (!isPaused) {
            // Start countdown when unpausing
            startCountdown();
        }
    }

    public boolean checkButtonPress(float touchX, float touchY) {
        // Check if touch is within the pause/play button area (right side of HUD)
        float buttonX = hudBox.right - 80;
        float buttonY = hudBox.centerY() - 30;
        RectF buttonArea = new RectF(buttonX, buttonY, buttonX + 60, buttonY + 60);
        return buttonArea.contains(touchX, touchY);
    }

    public void draw(Canvas canvas) {
        // Draw beautiful semi-transparent background for HUD
        paint.setColor(Color.argb(200, 30, 30, 30)); // Dark semi-transparent
        canvas.drawRoundRect(hudBox, 40, 40, paint);

        // Add subtle border glow
        paint.setColor(Color.argb(60, 255, 255, 255));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRoundRect(hudBox, 40, 40, paint);
        paint.setStyle(Paint.Style.FILL);

        // Draw coin icon on left side
        float coinX = hudBox.left + 20;
        float coinY = hudBox.centerY() - coinBitmap.getHeight() / 2;
        canvas.drawBitmap(coinBitmap, coinX, coinY, paint);

        // Draw score text with subtle shadow for depth
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        String scoreText = String.valueOf(score);
        float textX = coinX + coinBitmap.getWidth() + 15;
        float textY = hudBox.centerY() + 15;

        // Draw shadow text first
        canvas.drawText(scoreText, textX + 2, textY + 2, shadowPaint);

        // Draw actual text
        canvas.drawText(scoreText, textX, textY, paint);

        // Draw play/pause button on right side
        Bitmap buttonBitmap = isPaused ? playBitmap : pauseBitmap;
        float buttonX = hudBox.right - buttonBitmap.getWidth() - 20;
        float buttonY = hudBox.centerY() - buttonBitmap.getHeight() / 2;
        canvas.drawBitmap(buttonBitmap, buttonX, buttonY, paint);

        // Draw countdown if active
        if (isCountingDown) {
            // Transparent overlay
            paint.setColor(Color.argb(120, 0, 0, 0));
            canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

            // Draw countdown with style
            paint.setColor(Color.WHITE);
            paint.setTextSize(150);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setShadowLayer(15, 0, 0, Color.argb(180, 255, 165, 0)); // Orange glow

            String countdownText = countdownValue > 0 ? String.valueOf(countdownValue) : "GO!";
            canvas.drawText(countdownText, screenWidth / 2f, screenHeight / 2f, paint);
            paint.setShadowLayer(0, 0, 0, 0); // Reset shadow
            paint.setTextAlign(Paint.Align.LEFT);
        }
    }
}