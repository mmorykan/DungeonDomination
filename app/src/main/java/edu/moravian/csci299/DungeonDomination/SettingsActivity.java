package edu.moravian.csci299.DungeonDomination;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;

/**
 * Activity for setting the difficulty, color of player, and music
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    /** Shared preferences */
    private SharedPreferences sharedPreferences;
    private int currentDifficulty, currentColor;

    /** Music toggle */
    private SwitchCompat music;

    /** Easy, medium, and hard mode text views */
    private TextView easy, medium, hard;

    /** Color TextViews */
    private TextView white, orange, purple;

    /** Colors for changing selected/unselected difficulty TextViews */
    private final int selectedDifficulty = Color.rgb(83, 83, 83);
    private final int unselected = Color.rgb(188, 188, 188);

    /** Colors for changing the color TextViews */
    private final int selectedWhite = Color.WHITE, selectedOrange = Color.rgb(255,140,0),
        selectedPurple = Color.rgb(148,0,211);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        hideSystemUI();

        // set background color
        this.getWindow().getDecorView().setBackgroundColor(Color.CYAN);

        // setup music switch listener
        music = findViewById(R.id.music);
        music.setOnCheckedChangeListener(this);

        // setup shared preferences
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_FILE, Context.MODE_PRIVATE);
        currentDifficulty = sharedPreferences.getInt(getResources().getString(R.string.difficulty), 1);
        currentColor = sharedPreferences.getInt(getResources().getString(R.string.color), selectedWhite);
        boolean isPlaying = sharedPreferences.getBoolean(getResources().getString(R.string.musicOn), true);
        if (!isPlaying) { music.setChecked(false); }

        // back button to return to menu activity
        findViewById(R.id.settings_back_button).setOnClickListener(v -> finish());

        // setup difficulty TextView listeners
        easy = findViewById(R.id.easy);
        medium = findViewById(R.id.medium);
        hard = findViewById(R.id.hard);
        easy.setOnClickListener(this);
        medium.setOnClickListener(this);
        hard.setOnClickListener(this);

        // setup color TextView listeners
        white = findViewById(R.id.white);
        orange = findViewById(R.id.orange);
        purple = findViewById(R.id.purple);
        white.setOnClickListener(this);
        orange.setOnClickListener(this);
        purple.setOnClickListener(this);

        // highlight selected difficulty/color
        updateColorTextView(currentColor, currentColor);
        updateDifficultyTextView(currentDifficulty, selectedDifficulty);
    }

    /**
     * Choosing difficulties and colors
     * @param v the View clicked on
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.white || id == R.id.orange || id == R.id.purple) updateColor(id);
        else updateDifficulty(id);
    }

    /**
     * Updates the current color
     * @param id The id of the color chosen
     */
    public void updateColor(int id) {
        int color = selectedWhite;
        if (id == R.id.orange) {
            color = selectedOrange;
        } else if (id == R.id.purple) {
            color = selectedPurple;
        }
        if (color != currentColor) {
            updateColorTextView(color, color);
            updateColorTextView(currentColor, unselected);
            currentColor = color;
            sharedPreferences.edit().putInt(getResources().getString(R.string.color), currentColor).apply();
        }
    }

    /**
     * Updates the current difficulty
     * @param id the id of the difficulty text view
     */
    public void updateDifficulty(int id) {
        int difficulty = 1;
        if (id == R.id.medium) {
            difficulty = 2;
        } else if (id == R.id.hard) {
            difficulty = 3;
        }
        if (difficulty != currentDifficulty) {
            updateDifficultyTextView(difficulty, selectedDifficulty);
            updateDifficultyTextView(currentDifficulty, unselected);
            currentDifficulty = difficulty;
            sharedPreferences.edit().putInt(getResources().getString(R.string.difficulty), currentDifficulty).apply();
        }
    }

    /**
     * Highlights of unhighlights the color TextViews with the appropriate color
     * @param color The Color chosen to get the correct text view id from
     * @param newColor color to set
     */
    public void updateColorTextView(int color, int newColor) {
        int colorId = R.id.white;
        if (color == selectedOrange) {
            colorId = R.id.orange;
        } else if (color == selectedPurple) {
            colorId = R.id.purple;
        }
        ((TextView)findViewById(colorId)).setTextColor(newColor);
    }

    /**
     * Highlights or unhightlights the difficulty TextViews
     */
    public void updateDifficultyTextView(int difficulty, int difficultyColor) {
        int difficultyId = R.id.easy;
        if (difficulty == 2) {
            difficultyId = R.id.medium;
        } else if (difficulty == 3) {
            difficultyId = R.id.hard;
        }
        ((TextView)findViewById(difficultyId)).setTextColor(difficultyColor);
    }

    /**
     * Save the state of the music and start or stop the music from playing
     * @param buttonView The music switch
     * @param isChecked Whether or not the music switch is set
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sharedPreferences.edit().putBoolean(getResources().getString(R.string.musicOn), isChecked).apply();
        if (isChecked) PlayMusic.playAudio(getApplicationContext());
        else PlayMusic.stopAudio();
    }

    /** Timeout handler to re-hide the system UI after a delay */
    private final Handler timeoutHandler = new Handler();
    /** The Runnable version of the hideSystemUI() function */
    private final Runnable hideUIRunnable = this::hideSystemUI;

    /** Hides the system UI elements for the app, making the app full-screen. */
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        // Keep the screen on as well
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /** When the focus of the app changes, possibly hide the system UI elements */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) { hideSystemUI(); }
    }

    /**
     * When the user interacts, the timer is reset for re-hiding the system UI.
     */
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        timeoutHandler.removeCallbacks(hideUIRunnable);
        timeoutHandler.postDelayed(hideUIRunnable, 2000);
    }

}