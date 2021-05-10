package edu.moravian.csci299.DungeonDomination;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import androidx.annotation.Nullable;

public class GameView extends View implements SensorEventListener {

    /** The game for the logic behind this view */
    private final Game game;
    Context gameActivity;

    /** The metrics about the display to convert from dp and sp to px */
    private final DisplayMetrics displayMetrics;

    /** The paints used for the different parts of the game */
    private final Paint outlinePaint = new Paint();
    private final Paint playerPaint = new Paint();
    private final Paint textPaint = new Paint();
    private final Paint bulletPaint = new Paint();
    private final Paint coinPaint = new Paint();
    private final Paint enemyPaint = new Paint();

    /** Shared preferences */
    private final SharedPreferences sharedPreferences;
    private final int currentCoins;

    public GameView(Context context) { this(context, null);  }
    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Get the metrics for the display so we can later convert between dp, sp, and px
        displayMetrics = context.getResources().getDisplayMetrics();

        // Make the game
        game = new Game();
        gameActivity = getContext();
        sharedPreferences = gameActivity.getSharedPreferences(MainActivity.SHARED_PREF_FILE, Context.MODE_PRIVATE);
        currentCoins = sharedPreferences.getInt("coins", 0);
        int playerColor = sharedPreferences.getInt(getResources().getString(R.string.color), Color.WHITE);

        // The color painted as the background
        setBackgroundColor(Color.BLACK);

        // Setup all of the paints used for drawing
        setPaint(outlinePaint, Color.BLACK);
        setPaint(bulletPaint, Color.GRAY);
        setPaint(playerPaint, playerColor);
        setPaint(textPaint, Color.WHITE);
        setPaint(coinPaint, Color.rgb(255, 215, 0));
    }

    /**
     * Sets the paint color.
     * @param paint paint object for text or shape
     * @param color color for the paint
     */
    private void setPaint(Paint paint, int color) {
        paint.setColor(color);
    }

    /**
     * Update and draw all aspects of the game.
     * @param canvas the canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        postInvalidateOnAnimation(); //automatically invalidate every frame for continuous playback

        game.update();
        drawBullets(canvas, game.getPlayerBullets());
        drawBullets(canvas, game.getEnemyBullets());
        drawPlayer(canvas);
        drawEnemies(canvas);
        drawText(canvas);
        drawCoin(canvas);
    }

    /**
     * Sets the size of the layout. Starts a new game if game has not started yet, 
     * given the size of the layout, number of purchased weapon upgrades (to increase
     * player's damage), and difficulty (multiplier to increase enemy health and coin value).
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (game.hasNotStarted()) {
            game.startGame(right - left, bottom - top,
                    5 * (sharedPreferences.getInt(getResources().getString(R.string.weapon_upgrades),0) + 1),
                    sharedPreferences.getInt(getResources().getString(R.string.difficulty), 1));
        }
        invalidate();
    }

    /**
     * Draws the player.
     * @param canvas the canvas
     */
    public void drawPlayer(Canvas canvas) {
        PointF playerLocation = game.getPlayer().getLocation();
        canvas.drawCircle(playerLocation.x, playerLocation.y, Player.BODY_PIECE_SIZE_DP + 5, outlinePaint);
        canvas.drawCircle(playerLocation.x, playerLocation.y, Player.BODY_PIECE_SIZE_DP, playerPaint);
    }

    /**
     * Draws each enemy on the canvas. Changes the enemy's color
     * depending on their remaining health.
     * @param canvas the canvas
     */
    public void drawEnemies(Canvas canvas) {
        for (Enemy enemy : game.getEnemies()) {
            PointF enemyLocation = enemy.getLocation();
            int health = enemy.getHealth();
            int initialHealth = enemy.getInitialHealth();
            if (health <= initialHealth/4) setPaint(enemyPaint, Color.RED);
            else if (health <= initialHealth/2) setPaint(enemyPaint, Color.YELLOW);
            else setPaint(enemyPaint, Color.GREEN);
            canvas.drawCircle(enemyLocation.x, enemyLocation.y, enemy.getRadius(), outlinePaint);
            canvas.drawCircle(enemyLocation.x, enemyLocation.y, enemy.getRadius() - 5, enemyPaint);
        }
    }

    /**
     * Draws each bullet on the canvas.
     * @param canvas the canvas
     * @param bullets list of all bullets to draw
     */
    public void drawBullets(Canvas canvas, List<Bullet> bullets) {
        for (Bullet bullet : bullets) {
            PointF bulletLocation = bullet.getLocation();
            canvas.drawCircle(bulletLocation.x, bulletLocation.y, bullet.getRadius(), outlinePaint);
            canvas.drawCircle(bulletLocation.x, bulletLocation.y, bullet.getRadius() - 5, bulletPaint);
        }
    }

    /**
     * Draws coin next to the number of coins the player has.
     * @param canvas the canvas
     */
    public void drawCoin(Canvas canvas) {
        canvas.drawCircle(50, 50, 30, outlinePaint);
        canvas.drawCircle(50, 50, 25, coinPaint);
        textPaint.setTextSize(40f);
        canvas.drawText(gameActivity.getString(R.string.coins), 37, 65, textPaint);
    }

    /**
     * Draw text for the number of coins the player has, their
     * current HP, and a "game over" message once game is over.
     * This message varies depending if player won or not.
     * @param canvas the canvas
     */
    public void drawText(Canvas canvas) {
        // coin count
        String coins = Integer.toString(this.currentCoins+game.getCurrentCollectedCoins());
        textPaint.setTextSize(48f);
        canvas.drawText(coins, 100, 67, textPaint);

        // current HP of player
        String hpText = gameActivity.getString(R.string.hp);
        String hp = Integer.toString(game.getPlayer().getHealth());
        canvas.drawText(hpText + " " + hp, (float)(displayMetrics.widthPixels - 70), 67, textPaint);

        // announce that game is over
        if (game.isGameOver()) {
            int gameOverMessage = game.hasPlayerWon() ? R.string.game_over_won : R.string.game_over_lost;
            int offset = game.hasPlayerWon() ? 150 : 70;
            canvas.drawText(getResources().getString(gameOverMessage), (float)(displayMetrics.widthPixels / 2) - offset,
                                           (float)(displayMetrics.heightPixels / 2), textPaint);
        }
    }

    /**
     * When the screen is touched, checks if the snake, food, or wall
     * was touched. If the game is over, return to main activity.
     * @param event touching the screen event
     * @return always return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (game.isGameOver()) {
            sharedPreferences.edit().putInt("coins", this.currentCoins + game.getCurrentCollectedCoins()).apply();
            ((GameActivity) gameActivity).finish();
        }
        int action = event.getAction();
        PointF point = new PointF(event.getX(), event.getY());
        if (action == MotionEvent.ACTION_DOWN) {
            game.touched(point);
        }
        invalidate();
        performClick();
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * Calculates the angle that the player is looking based off the
     * event values and set the direction for the game.
     * @param event event that sensor has changed
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        game.setMovementDirection(Math.atan2(event.values[0], event.values[1]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
