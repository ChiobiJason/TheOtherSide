package com.example.theotherside;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class GameObject {
    protected float x, y;
    protected float width, height;
    protected float speed;
    protected Bitmap bitmap;
    protected boolean isAlive = true;
    protected Rect hitBox;

    public GameObject(float x, float y, Bitmap bitmap) {
        this.x = x;
        this.y = y;
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.hitBox = new Rect((int)x, (int)y, (int)(x + width), (int)(y + height));
    }

    public void update() {
        // Update the position of the hitbox
        hitBox.left = (int)x;
        hitBox.top = (int)y;
        hitBox.right = (int)(x + width);
        hitBox.bottom = (int)(y + height);
    }

    public void draw(Canvas canvas) {
        if (isAlive) {
            canvas.drawBitmap(bitmap, x, y, null);
        }
    }

    public boolean isColliding(GameObject other) {
        return Rect.intersects(hitBox, other.hitBox);
    }
}

