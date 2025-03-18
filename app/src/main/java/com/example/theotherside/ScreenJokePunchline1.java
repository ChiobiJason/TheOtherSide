package com.example.theotherside;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ScreenJokePunchline1 extends AppCompatActivity {
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.screen_joke_punchline_1);

        videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.to_get_to));
        videoView.setOnPreparedListener(mp -> mp.setLooping(true)); // Loop video
        videoView.start(); // Play automatically
    }
}