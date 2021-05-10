package edu.moravian.csci299.DungeonDomination;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

/**
 * A screen displaying the app logo and a flashing "Press Anywhere" button
 */
public class MainActivity extends AppCompatActivity {

    /** Access to Shared Preferences */
    private SharedPreferences sharedPreferences;
    public static final String SHARED_PREF_FILE = "SharedPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();
        startAnimation();

        // set background color
        this.getWindow().getDecorView().setBackgroundColor(Color.CYAN);

        // Set up shared preferences and its editor
        sharedPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        boolean isMusicOn = sharedPreferences.getBoolean(getResources().getString(R.string.musicOn), true);
        if (isMusicOn) PlayMusic.playAudio(getApplicationContext());
    }

    /**
     * Start the animation for the "Press Anywhere" TextView.
     */
    public void startAnimation() {
        TextView pressAnywhere = findViewById(R.id.press_anywhere);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(750);
        animation.setStartOffset(20);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        pressAnywhere.startAnimation(animation);
    }

    /**
     * Go to menu activity when screen is touched.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        return super.onTouchEvent(event);
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