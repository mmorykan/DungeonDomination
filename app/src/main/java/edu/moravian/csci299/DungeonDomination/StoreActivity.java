package edu.moravian.csci299.DungeonDomination;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Displays all the upgrades and their costs in the store
 */
public class StoreActivity extends AppCompatActivity implements View.OnClickListener {

    /** Prices for each upgrade */
    private static final int FIRST_UPGRADE = 100, SECOND_UPGRADE = 1000, THIRD_UPGRADE = 5000;

    /** Button ids */
    private static final List<Integer> buttonIds = Arrays.asList(
        R.id.upgrade_1_button,
        R.id.upgrade_2_button,
        R.id.upgrade_3_button
    );

    /** Image ids */
    private static final List<Integer> imageIds  = Arrays.asList(R.id.upgrade1, R.id.upgrade2, R.id.upgrade3);

    /** Coin balance */
    private TextView coins;

    /** Shared preferences */
    private SharedPreferences sharedPreferences;

    /** Stored attributes in the shared preferences */
    private int weaponUpgradesPurchased;
    private int coinBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        hideSystemUI();

        // set background color
        this.getWindow().getDecorView().setBackgroundColor(Color.CYAN);

        // get shared preferences
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_FILE, Context.MODE_PRIVATE);
        coinBalance = sharedPreferences.getInt(getResources().getString(R.string.coins_key), 0);
        weaponUpgradesPurchased = sharedPreferences.getInt(getResources().getString(R.string.weapon_upgrades),0);

        // set player's coin balance
        coins = findViewById(R.id.coin_balance);
        coins.setText(getString(R.string.coin_balance, String.valueOf(coinBalance)));

        // set background of upgrade icons
        for (Integer id : imageIds) {
            ImageView upgrade = findViewById(id);
            upgrade.setBackgroundColor(Color.rgb(0, 123, 123));
        }

        // click listeners for back button and buy button
        findViewById(R.id.store_back_button).setOnClickListener(this);

        for (int i = 0; i < buttonIds.size(); i++) {
            Button upgradeButton = findViewById(buttonIds.get(i));
            upgradeButton.setOnClickListener(this);
            if (weaponUpgradesPurchased > i) upgradeButton.setText(R.string.upgrade_purchased);
        }
    }

    /**
      * Finish this activity or choose an upgrade
     */
    @Override
    public void onClick(View v) {
        int coinsToSubtract = 0;
        int buttonId = v.getId();
        boolean hasPurchased = false;
        if (buttonId == R.id.store_back_button) finish();
        else if (buttonId == R.id.upgrade_1_button) {
            if (coinBalance >= FIRST_UPGRADE && weaponUpgradesPurchased == 0) {
                coinsToSubtract = FIRST_UPGRADE;
                hasPurchased = true;
            }
        } else if (buttonId == R.id.upgrade_2_button) {
            if (coinBalance >= SECOND_UPGRADE && weaponUpgradesPurchased == 1) {
                coinsToSubtract = SECOND_UPGRADE;
                hasPurchased = true;
            }
        } else {
            if (coinBalance >= THIRD_UPGRADE && weaponUpgradesPurchased == 2) {
                coinsToSubtract = THIRD_UPGRADE;
                hasPurchased = true;
            }
        }
        if (hasPurchased) {
            coinBalance -= coinsToSubtract;
            weaponUpgradesPurchased++;
            ((Button)findViewById(buttonId)).setText(R.string.upgrade_purchased);
            coins.setText(getString(R.string.coin_balance, String.valueOf(coinBalance)));
            sharedPreferences.edit().putInt(getResources().getString(R.string.coins_key), coinBalance).apply();
            sharedPreferences.edit().putInt(getResources().getString(R.string.weapon_upgrades), weaponUpgradesPurchased).apply();
        }
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