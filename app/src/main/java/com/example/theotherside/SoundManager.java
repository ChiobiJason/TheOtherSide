/*
 * This file contains the SoundManager class which is responsible for managing
 * sound effects and background music in the application. It uses SoundPool for
 * short sound effects and MediaPlayer for background music.
 *
 * The class manages:
 * - Loading and playing sound effects
 * - Controlling background music playback
 * - Muting and unmuting all sounds
 * - Singleton pattern to ensure a single instance
 */
package com.example.theotherside;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
public class SoundManager {
    private static SoundManager instance;
    private final SoundPool soundPool;
    private final MediaPlayer bgMusicPlayer;
    private boolean isMuted = false;
    private float volume = 1.0f;

    // Sound IDs
    private int coinSoundId;
    private int jumpSoundId;
    private int crashSoundId;
    private int buttonClickSoundId;

    /**
     * Private constructor to initialize SoundPool and MediaPlayer.
     *
     * @param context - The application context used to load sound resources
     */
    private SoundManager(Context context) {
        // initialise SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        // Loading sounds
        coinSoundId = soundPool.load(context, R.raw.coin_sound, 1);
        jumpSoundId = soundPool.load(context, R.raw.jump_sound, 1);
        crashSoundId = soundPool.load(context, R.raw.crash_sound, 1);
        buttonClickSoundId = soundPool.load(context, R.raw.button_click, 1);

        // initialise background music
        bgMusicPlayer = MediaPlayer.create(context, R.raw.bg_music);
        bgMusicPlayer.setLooping(true);

    }

    /**
     * Returns the singleton instance of SoundManager.
     *
     * @param context - The application context
     * @return The SoundManager instance
     */
    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    /**
     * Plays the coin sound effect if not muted.
     */
    public void playCoinSound() {
        if (!isMuted) soundPool.play(coinSoundId, volume, volume, 1, 0, 1);
    }

    /**
     * Plays the jump sound effect if not muted.
     */
    public void playJumpSound() {
        if (!isMuted) soundPool.play(jumpSoundId, volume, volume, 1, 0, 1);
    }

    /**
     * Plays the crash sound effect if not muted.
     */
    public void playCrashSound() {
        if (!isMuted) soundPool.play(crashSoundId, volume, volume, 1, 0, 1);

    }

    /**
     * Plays the button click sound effect if not muted.
     */
    public void playButtonClick(){
        if (!isMuted) soundPool.play(buttonClickSoundId, volume, volume, 1, 0, 1);
    }

    /**
     * Toggles the mute state and adjusts the background music volume accordingly.
     */
    public void makeMute(){
        isMuted = !isMuted;
        bgMusicPlayer.setVolume(isMuted ? 0 : volume, isMuted ? 0 : volume);
    }

    /**
     * Starts the background music if it's not already playing.
     * Handles IllegalStateException if the player is in an invalid state.
     */
    // TO FIX: music stops playing after playing again
    public void startBgMusic() {
        try {
            if (bgMusicPlayer != null && !bgMusicPlayer.isPlaying()) {
                bgMusicPlayer.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pauses the background music if it's currently playing.
     * Handles IllegalStateException if the player is in an invalid state.
     */
    public void pauseBgMusic() {
        try {
            if (bgMusicPlayer != null && bgMusicPlayer.isPlaying()) {
                bgMusicPlayer.pause();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Releases resources used by SoundPool and MediaPlayer.
     */
    public void release() {
        soundPool.release();
        bgMusicPlayer.release();
    }

    /**
     * Returns the current mute state.
     *
     * @return true if muted, false otherwise
     */
    public boolean isMuted() {
        return isMuted;
    }
}

