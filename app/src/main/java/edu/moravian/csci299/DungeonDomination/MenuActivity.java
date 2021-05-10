package edu.moravian.csci299.DungeonDomination;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.view.WindowManager;
import android.widget.ImageView;

/**
 * The main menu where user can access the game, settings, and the store
 */
public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    /** Image to animate */
    private ImageView stickMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        hideSystemUI();

        // set background color
        this.getWindow().getDecorView().setBackgroundColor(Color.CYAN);
        stickMan = findViewById(R.id.stickman); // image for animation
        
        // set listener for buttons
        findViewById(R.id.adventure_button).setOnClickListener(this);
        findViewById(R.id.settings_button).setOnClickListener(this);
        findViewById(R.id.store_button).setOnClickListener(this);

        startAnimation();
    }

    /**
     * Begins the infinite animate of the app logo moving back and forth
     */
    private void startAnimation() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(stickMan, "translationX", -500f, 500f);
        animation.setDuration(3000);
        animation.setRepeatMode(ObjectAnimator.REVERSE);
        animation.setRepeatCount(ObjectAnimator.INFINITE);
        animation.start();
    }

    /**
     * When the adventure button is clicked to start the GameActivity
     * @param v The play game button
     */
    @Override
    public void onClick(View v) {
        int buttonId = v.getId();
        Class<? extends AppCompatActivity> nextActivity = StoreActivity.class;
        if (buttonId == R.id.adventure_button) {
            nextActivity = GameActivity.class;
        } else if (buttonId == R.id.settings_button) {
            nextActivity = SettingsActivity.class;
        }
        startActivity(new Intent(this, nextActivity));
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