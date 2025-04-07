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

    private SoundManager(Context context) {
        // initialise SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        // koading sounds
        coinSoundId = soundPool.load(context, R.raw.coin_sound, 1);
        jumpSoundId = soundPool.load(context, R.raw.jump_sound, 1);
        crashSoundId = soundPool.load(context, R.raw.crash_sound, 1);
        buttonClickSoundId = soundPool.load(context, R.raw.button_click, 1);

        // initialise background music
        bgMusicPlayer = MediaPlayer.create(context, R.raw.bg_music);
        bgMusicPlayer.setLooping(true);

    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    public void playCoinSound() {
        if (!isMuted) soundPool.play(coinSoundId, volume, volume, 1, 0, 1);
    }

    public void playJumpSound() {
        if (!isMuted) soundPool.play(jumpSoundId, volume, volume, 1, 0, 1);
    }

    public void playCrashSound() {
        if (!isMuted) soundPool.play(crashSoundId, volume, volume, 1, 0, 1);

    }

    public void playButtonClick(){
        if (!isMuted) soundPool.play(buttonClickSoundId, volume, volume, 1, 0, 1);
    }

    public void makeMute(){
        isMuted = !isMuted;
        bgMusicPlayer.setVolume(isMuted ? 0 : volume, isMuted ? 0 : volume);
    }

    public void startBgMusic() {
        if (!bgMusicPlayer.isPlaying()) {
            bgMusicPlayer.start();
        }
    }

    public void pauseBgMusic() {
        if (bgMusicPlayer.isPlaying()) {
            bgMusicPlayer.pause();
        }
    }

    public void release() {
        soundPool.release();
        bgMusicPlayer.release();
    }


    public boolean isMuted() {
        return isMuted;
    }
}

