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

        VideoView videoView = findViewById(R.id.videoView);
        //ImageButton muteButton = findViewById(R.id.muteButton);


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


//        muteButton.setOnClickListener(v -> {
//            if (isMuted) {
//                // unmute
//                videoView.setVolume(1.0f, 1.0f); // Set volume to max
//                muteButton.setImageResource(R.drawable.ic_volume_up);
//            } else {
//                // Mute
//                videoView.setVolume(0.0f, 0.0f); // Set volume to 0
//                muteButton.setImageResource(R.drawable.ic_volume_off);
//            }
//            isMuted = !isMuted;
//        });

    }
}