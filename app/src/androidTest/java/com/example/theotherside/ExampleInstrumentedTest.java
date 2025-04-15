package com.example.theotherside;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.theotherside", appContext.getPackageName());
    }

    //==============================================================================================
    //         GameObject Tests
    //==============================================================================================
    @Test
    public void testGameObjectCreation() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin);
        GameObject newObject = new GameObject(10, 10, bitmap);
        assertNotNull(newObject);
    }

    @Test
    public void testGameObjectHeight() {
        Bitmap sampleBitmap = Bitmap.createBitmap(2, 30, Bitmap.Config.ARGB_8888);

        GameObject sampleObject = new GameObject(10, 11, sampleBitmap);
        assertEquals(30, sampleObject.height, 0.01f);
    }

    @Test
    public void testGameObjectIsAlive() {
        Bitmap sampleBitmap = Bitmap.createBitmap(2, 9, Bitmap.Config.ARGB_8888);

        GameObject sampleObject = new GameObject(5, 1, sampleBitmap);
        assertEquals(true, sampleObject.isAlive);
    }

    @Test
    public void testGameObjectPosition() {
        Bitmap sampleBitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
        float testX = 15.5f;
        float testY = 25.7f;

        GameObject sampleObject = new GameObject(testX, testY, sampleBitmap);
        assertEquals(testX, sampleObject.posX, 0.01f);
        assertEquals(testY, sampleObject.posY, 0.01f);
    }

    @Test
    public void testHitBoxInitialization() {
        Bitmap sampleBitmap = Bitmap.createBitmap(20, 30, Bitmap.Config.ARGB_8888);
        float posX = 10f;
        float posY = 15f;

        GameObject sampleObject = new GameObject(posX, posY, sampleBitmap);
        assertEquals((int)posX, sampleObject.hitBox.left);
        assertEquals((int)posY, sampleObject.hitBox.top);
        assertEquals((int)(posX + sampleBitmap.getWidth()), sampleObject.hitBox.right);
        assertEquals((int)(posY + sampleBitmap.getHeight()), sampleObject.hitBox.bottom);
    }

    @Test
    public void testUpdate() {
        Bitmap sampleBitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
        GameObject sampleObject = new GameObject(10f, 10f, sampleBitmap);

        // Change the position
        sampleObject.posX = 30f;
        sampleObject.posY = 40f;

        // Call update to update the hitbox
        sampleObject.update();

        // Verify hitbox is updated
        assertEquals(30, sampleObject.hitBox.left);
        assertEquals(40, sampleObject.hitBox.top);
        assertEquals(30 + sampleBitmap.getWidth(), sampleObject.hitBox.right);
        assertEquals(40 + sampleBitmap.getHeight(), sampleObject.hitBox.bottom);
    }

    @Test
    public void testDraw() {
        // Use a real Canvas with a Bitmap for drawing
        Bitmap canvasBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBitmap);

        // Create a colored bitmap for our GameObject so we can detect if it was drawn
        Bitmap redBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        redBitmap.eraseColor(Color.RED);

        // Create GameObject and draw it
        GameObject sampleObject = new GameObject(5f, 5f, redBitmap);
        sampleObject.draw(canvas);

        // Verify something was drawn by checking pixel color
        assertEquals(Color.RED, canvasBitmap.getPixel(5, 5));

        // Test the negative case - set object to not alive
        Bitmap canvasBitmap2 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(canvasBitmap2);
        canvasBitmap2.eraseColor(Color.WHITE);

        sampleObject.isAlive = false;
        sampleObject.draw(canvas2);

        // Pixel should remain white as nothing should have been drawn
        assertEquals(Color.WHITE, canvasBitmap2.getPixel(5, 5));
    }

    @Test
    public void testCollisionTrue() {
        Bitmap bitmap1 = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        Bitmap bitmap2 = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);

        GameObject object1 = new GameObject(0, 0, bitmap1);
        GameObject object2 = new GameObject(5, 5, bitmap2);

        // Objects should collide
        assertTrue(object1.isColliding(object2));
    }

    @Test
    public void testCollisionFalse() {
        Bitmap bitmap1 = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
        Bitmap bitmap2 = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);

        GameObject object1 = new GameObject(0, 0, bitmap1);
        GameObject object2 = new GameObject(20, 20, bitmap2);

        // Objects should not collide
        assertFalse(object1.isColliding(object2));
    }
}