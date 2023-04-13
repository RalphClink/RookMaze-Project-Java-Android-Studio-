package com.example.rookmazegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class MazeActivity extends AppCompatActivity {

    // Initialize class variables
    RookMaze rookMaze = new RookMaze();
    LinearLayout mainLayout;
    ArrayList<Integer> listOfRowIDs = new ArrayList<>();
    int[][] rookMazeIDs;
    int playerMoveCounter = 0;
    ArrayList<Integer> moves = new ArrayList<>();

    // Set player icon as this will never change


    // Creates a rookJumping Maze
    private void initializeRookMaze() {
        rookMaze.generateMaze();
    }

    // Get Rook Maze Rows and Columns
    private int getRookMazeRows() {
        return rookMaze.mazeRows;
    }

    private int getRookMazeColumns() {
        return rookMaze.mazeColumns;
    }

    private void setMazeDifficulty(int difficulty) {
        rookMaze.setDifficulty(difficulty);
    }

    @SuppressLint({"SetTextI18n", "ResourceType"})
    private void renderMaze() {
        rookMazeIDs = new int[getRookMazeRows()][getRookMazeColumns()];

        for (int y = 0; y < getRookMazeRows(); y++) {
            // make LinearLayout; width match parent, height 36dp, orientation horizontal
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(17);

            // Generating an ID for the row (will work like coordinates later on)
            int id = View.generateViewId();
            rowLayout.setId(id);
            listOfRowIDs.add(id);

            mainLayout.addView(rowLayout);

            // Add individual cells in
            for (int x = 0; x < getRookMazeColumns(); x++) {
                TextView number = new TextView(this);
                number.setText(Integer.toString(rookMaze.rookMaze[y][x]));
                number.setTextSize(25);
                number.setTextColor(ContextCompat.getColor(this, R.color.white));
                number.setLayoutParams(new ViewGroup.LayoutParams(100, ViewGroup.LayoutParams.MATCH_PARENT));

                id = View.generateViewId();
                number.setId(id);
                rookMazeIDs[y][x] = id;

                number.setClickable(true);
                number.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        displayInvalidMove();
                    }
                });

                // gets the row we're currently looping through
                rowLayout.addView(number);
            }
        }
    }

    private void displayInvalidMove() {
        Toast invalidMoveToast = Toast.makeText(this, "Must Move to Highlighted Location", Toast.LENGTH_SHORT);
        invalidMoveToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        invalidMoveToast.show();
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.scifierror);
        mp.start();
    }


    // Set the users starting position
    private void getStartingPoint() {
        int textViewId = rookMazeIDs[0][0];
        TextView startingPoint = findViewById(textViewId);
        startingPoint.setOnClickListener(null);
        SpannableStringBuilder ssb = new SpannableStringBuilder("-");
        ssb.setSpan(new ImageSpan(this, R.drawable.playercharacter), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        startingPoint.setText(ssb, TextView.BufferType.SPANNABLE);
    }


    // Find valid moves on the current maze grid
    private ArrayList<Integer> findValidMoves(int y, int x, int value) {
        ArrayList<Integer> IDs = new ArrayList<>();
        if (x + value < getRookMazeColumns()) {
            IDs.add(rookMazeIDs[y][x + value]);
        }
        if (y + value < getRookMazeRows()) {
            IDs.add(rookMazeIDs[y + value][x]);
        }
        if (x - value >= 0) {
            IDs.add(rookMazeIDs[y][x - value]);
        }
        if (y - value >= 0) {
            IDs.add(rookMazeIDs[y - value][x]);
        }
        return IDs;
    }

    private void setValidMoves() {
        // Get valid moves
        moves = findValidMoves(rookMaze.currentPlayerPositionY,
                rookMaze.currentPlayerPositionX, rookMaze.rookMaze[rookMaze.currentPlayerPositionY][rookMaze.currentPlayerPositionX]);

        for (int i = 0; i < moves.size(); i++) {
            int validPointID = moves.get(i);
            TextView validPoint = findViewById(moves.get(i));
            validPoint.setOnClickListener(null);
            validPoint.setTextColor(ContextCompat.getColor(this, R.color.purple_200));
            validPoint.setOnClickListener(v -> setNewPlayerPosition(validPointID));
        }
    }

    @SuppressLint("SetTextI18n")
    public void setNewPlayerPosition(int id) {
        increasePlayerMoveCounter();
        TextView currentPoint = findViewById(id);

        // Reset the colour of all previous points
        // Also remove on click listeners
        for (int y = 0; y < getRookMazeRows(); y++) {
            for (int x = 0; x < getRookMazeColumns(); x++) {
                TextView point = findViewById(rookMazeIDs[y][x]);
                point.setTextColor(ContextCompat.getColor(this, R.color.white));
                point.setOnClickListener(v -> displayInvalidMove());
                point.setText(Integer.toString(rookMaze.rookMaze[y][x]));

                // Update current position
                if (id == rookMazeIDs[y][x]) {
                    rookMaze.currentPlayerPositionY = y;
                    rookMaze.currentPlayerPositionX = x;
                }
            }
        }

        // Set up the current points colour
        // Also sets the playerCharacter image over the players position
        currentPoint.setTextColor(ContextCompat.getColor(this, R.color.teal_200));
        SpannableStringBuilder ssb = new SpannableStringBuilder("-");
        ssb.setSpan(new ImageSpan(this, R.drawable.playercharacter), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        currentPoint.setText(ssb, TextView.BufferType.SPANNABLE);

        // gets the next set of moves
        setValidMoves();

        // Check if point is the goal
        if (id == rookMazeIDs[rookMaze.getSolutionPositionY()][rookMaze.getSolutionPositionX()]) {
            // You win well done have a cookie
            currentPoint.setTextColor(ContextCompat.getColor(this, R.color.black));
            displayWinnerDialogueBox();
            playWinnerSound();
        } else if (moves.size() == 0) {
            displayLoserDialogueBox();
            playLoserSound();
        }
    }

    private void increasePlayerMoveCounter() {
        ++playerMoveCounter;
        updatePlayerMoveCounterTextView();
    }

    private void resetPlayerMoveCounter() {
        playerMoveCounter = 0;
        updatePlayerMoveCounterTextView();
    }

    private void updatePlayerMoveCounterTextView() {
        TextView playerMoveCounterTextView = findViewById(R.id.moveCounter_textView);
        playerMoveCounterTextView.setText(playerMoveCounterStringBuilder());
    }

    private StringBuilder playerMoveCounterStringBuilder() {
        StringBuilder sb = new StringBuilder();
        sb.append(playerMoveCounter);
        sb.append(" move(s)");
        return sb;
    }

    private void setExpectedMoves() {
        TextView expectedMovesTextView = findViewById(R.id.expectedMoveCounter_textView);
        expectedMovesTextView.setText(expectedMoveCounterStringBuilder());
    }

    private StringBuilder expectedMoveCounterStringBuilder() {
        StringBuilder sb = new StringBuilder();
        sb.append("Par ");
        sb.append(rookMaze.solutionLength);
        return sb;
    }

    @SuppressLint("SetTextI18n")
    private void restartGame() {
        // Reset player move counter
        playerMoveCounter = 0;
        // Reset all colours and on click listeners
        for (int y = 0; y < getRookMazeRows(); y++) {
            for (int x = 0; x < getRookMazeColumns(); x++) {
                TextView point = findViewById(rookMazeIDs[y][x]);
                point.setText(Integer.toString(rookMaze.rookMaze[y][x]));
                point.setTextColor(ContextCompat.getColor(this, R.color.white));
                point.setOnClickListener(null);
            }
        }
        // Set the players position to 0,0
        rookMaze.currentPlayerPositionY = 0;
        rookMaze.currentPlayerPositionX = 0;
        TextView currentPoint = findViewById(rookMazeIDs[0][0]);
        currentPoint.setTextColor(ContextCompat.getColor(this, R.color.teal_200));
        SpannableStringBuilder ssb = new SpannableStringBuilder("-");
        ssb.setSpan(new ImageSpan(this, R.drawable.playercharacter), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        currentPoint.setText(ssb, TextView.BufferType.SPANNABLE);

        resetPlayerMoveCounter();
        // Find the valid moves from the players new position
        setValidMoves();
    }


    private int generateMazeId() {
        int mazeId = 0;
        for (int i = 0; i < getRookMazeRows(); i++) {
            for (int j = 0; j < getRookMazeColumns(); j++) {
                mazeId += rookMaze.rookMaze[i][j];
            }
        }
        return mazeId;
    }

    private StringBuilder buildLevelTitle() {
        Bundle extras = getIntent().getExtras();
        int difficulty = extras.getInt("mazeDifficulty");
        StringBuilder sb = new StringBuilder();
        if (difficulty == 1) {
            sb.append("Easy Level: ");
        } else if (difficulty == 2) {
            sb.append("Medium Level: ");
        } else if (difficulty == 3) {
            sb.append("Hard Level: ");
        }
        sb.append(generateMazeId());

        return sb;
    }

    private void setLevelTitle() {
        TextView levelTitle = findViewById(R.id.levelName_textView);
        levelTitle.setText(buildLevelTitle());
    }

    private void displayWinnerDialogueBox() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Winner Winner Chicken Dinner!");
        alertDialog.setMessage("Return to Main Menu?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void displayLoserDialogueBox() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Dead end, you Lose!");
        alertDialog.setMessage("Restart Level?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartGame();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    private void muteSound() {
        AudioManager aManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        aManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
    }

    private void unmuteSound() {
        AudioManager aManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        aManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
    }

    private void setOnClickSoundButtons() {
        Button muteButton = findViewById(R.id.mute_button);
        muteButton.setOnClickListener(v -> muteSound());

        Button unmuteButton = findViewById(R.id.unmute_button);
        unmuteButton.setOnClickListener(v -> unmuteSound());
    }

    private void playWinnerSound() {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.scifiwinner);
        mp.start();
    }

    private void playLoserSound() {
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.scifiloser);
        mp.start();
    }

    private void setRestartGameButton() {
        Button restartGameButton = findViewById(R.id.restartGame_button);
        restartGameButton.setOnClickListener(v -> restartGame());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maze);
        mainLayout = findViewById(R.id.mainLayout);
        setOnClickSoundButtons();
        Bundle extras = getIntent().getExtras();
        setMazeDifficulty(extras.getInt("mazeDifficulty"));
        setRestartGameButton();
        initializeRookMaze();
        renderMaze();
        setExpectedMoves();
        setLevelTitle();
        getStartingPoint();
        setValidMoves();
    }
}