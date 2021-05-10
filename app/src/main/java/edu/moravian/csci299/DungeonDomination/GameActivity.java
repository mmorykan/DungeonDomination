package edu.moravian.csci299.DungeonDomination;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {

    private GameView gameView; // custom drawing view
    private SensorManager sensorManager; // sensor manager
    private Sensor sensor; // gravity sensor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        hideSystemUI();

        gameView = findViewById(R.id.game_view);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    /** Register gravity sensor to sensor manager. */
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(gameView, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    /** Unregister listener when MainActivity is resumed. */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gameView);
    }

    /** Set sensor and sensor manager to null when app is closed. */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager = null;
        sensor = null;
    }

    ///// Don't worry about the rest of this code - it deals with making a fullscreen app /////

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