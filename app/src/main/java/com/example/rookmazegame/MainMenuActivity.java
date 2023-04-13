package com.example.rookmazegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class MainMenuActivity extends AppCompatActivity {
    int mazeDifficulty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);


        Button startGameButton = findViewById(R.id.startGame_button);
        startGameButton.setEnabled(false);


        Button easyButton = findViewById(R.id.difficulty1Button);
        easyButton.setOnClickListener(v -> setDifficulty(1));


        Button mediumButton = findViewById(R.id.difficulty2Button);
        mediumButton.setOnClickListener(v -> setDifficulty(2));

        Button hardButton = findViewById(R.id.difficulty3Button);
        hardButton.setOnClickListener(v -> setDifficulty(3));

        setVideoPlayer();
    }

    private void setVideoPlayer() {
        final VideoView videoView = findViewById(R.id.instructions_videoView);
        videoView.setVideoURI(Uri.parse("android.resource://" +getPackageName()+ "/"+R.raw.instructionsvideo));
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();

        Button playVideoButton = findViewById(R.id.playVideo_button);
        playVideoButton.setOnClickListener(v -> videoView.start());
    }


    private void setDifficulty(int difficulty) {
        mazeDifficulty = difficulty;
        Button startGameButton = findViewById(R.id.startGame_button);
        startGameButton.setEnabled(true);
    }

    // Sends user from the main menu to the maze game
    public void startMazeGame(View view) {
        Intent intent = new Intent(this, MazeActivity.class);
        intent.putExtra("mazeDifficulty", mazeDifficulty);
        startActivity(intent);
    }

}