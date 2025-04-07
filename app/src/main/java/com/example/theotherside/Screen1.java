package com.example.theotherside;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;

public class Screen1 extends AppCompatActivity {

    private VideoView videoView;
    private ImageButton muteButton;
    private ImageButton skipIntro;
    //private boolean isMuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_joke_setup);

        //initialise SoundManager
        SoundManager.getInstance(this);

        // mute button
        muteButton = findViewById(R.id.muteButton);
        updateMuteButton(); // this sets inital icon

        muteButton.setOnClickListener(view -> {
            SoundManager.getInstance(this).makeMute();
            updateMuteButton();
        });

        VideoView videoView = findViewById(R.id.videoView);


        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.screen1_video;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // video's done
                // start the next screen (ScreenJokePunchline1)
                Intent intent = new Intent(Screen1.this, ScreenTitle.class);
                startActivity(intent);
                finish(); //then close Screen1
            }
        });

        muteButton.setOnClickListener(view -> {
            SoundManager.getInstance(this).makeMute();
                    if (SoundManager.getInstance(this).isMuted()) {
                        muteButton.setImageResource(R.drawable.ic_volume_off);
                    } else {
                        muteButton.setImageResource(R.drawable.ic_volume_up);
            }
        });

    }

    private void updateMuteButton(){
        if (SoundManager.getInstance(this).isMuted()) {
            muteButton.setImageResource(R.drawable.ic_volume_off);
        } else {
            muteButton.setImageResource(R.drawable.ic_volume_up);
        }
    }
}